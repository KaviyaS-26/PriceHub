package com.pricehub.service;

import com.pricehub.dto.*;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Product;
import com.pricehub.repository.MarketplaceListingRepository;
import com.pricehub.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductComparisonService {

    private final ProductRepository productRepository;
    private final MarketplaceListingRepository listingRepository;

    public ProductComparisonService(ProductRepository productRepository,
                                    MarketplaceListingRepository listingRepository) {
        this.productRepository = productRepository;
        this.listingRepository = listingRepository;
    }

    /**
     * Dynamically compares a single product across its marketplace listings on every request.
     * Never reads or writes any stored 'recommended' or 'winner' value.
     */
    @Transactional(readOnly = true)
    public ProductCompareResponse compareProduct(Long productId, Double wPrice, Double wRating, Double wWarranty, String ratingMethod) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        // Default weights: price 0.5, rating 0.3, warranty 0.2
        double weightPrice = (wPrice != null && wPrice >= 0) ? wPrice : 0.5;
        double weightRating = (wRating != null && wRating >= 0) ? wRating : 0.3;
        double weightWarranty = (wWarranty != null && wWarranty >= 0) ? wWarranty : 0.2;
        String rMethod = (ratingMethod != null && !ratingMethod.trim().isEmpty()) ? ratingMethod.trim().toUpperCase() : "ABSOLUTE";

        List<MarketplaceListing> listings = listingRepository.findByProductId(productId);
        if (listings.isEmpty()) {
            listings = listingRepository.findByProduct(product);
        }

        ProductCompareResponse response = new ProductCompareResponse();
        response.setProductId(product.getId());
        response.setProductName(product.getName());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());

        Map<String, Object> weightsMap = new HashMap<>();
        weightsMap.put("wPrice", weightPrice);
        weightsMap.put("wRating", weightRating);
        weightsMap.put("wWarranty", weightWarranty);
        weightsMap.put("ratingMethod", rMethod);
        response.setWeightsUsed(weightsMap);

        if (listings.isEmpty()) {
            response.setAllOutOfStock(true);
            response.setExplanation("No marketplace listings found for this product.");
            return response;
        }

        // Separate in-stock and out-of-stock listings
        List<MarketplaceListing> inStockListings = listings.stream()
                .filter(MarketplaceListing::isInStock)
                .collect(Collectors.toList());

        List<MarketplaceListing> outOfStockListings = listings.stream()
                .filter(l -> !l.isInStock())
                .collect(Collectors.toList());

        if (inStockListings.isEmpty()) {
            response.setAllOutOfStock(true);
            response.setExplanation("No listings are currently in stock for this product.");
            response.setBest(null);
            response.setLowestPrice(null);
            response.setExtraCost(0.0);

            List<CompareListingDTO> allDTOs = new ArrayList<>();
            for (MarketplaceListing oos : outOfStockListings) {
                CompareListingDTO dto = toDTO(oos);
                dto.setExcludedFromRanking(true);
                dto.setInStock(false);
                dto.setRank(null);
                dto.setValueScore(null);
                allDTOs.add(dto);
            }
            response.setListings(allDTOs);
            return response;
        }

        // Compute min and max across IN-STOCK listings only
        double minPrice = inStockListings.stream().mapToDouble(MarketplaceListing::getPrice).min().orElse(0.0);
        double maxPrice = inStockListings.stream().mapToDouble(MarketplaceListing::getPrice).max().orElse(0.0);

        OptionalInt minWarrantyOpt = inStockListings.stream()
                .filter(l -> l.getWarrantyMonths() != null)
                .mapToInt(MarketplaceListing::getWarrantyMonths)
                .min();

        OptionalInt maxWarrantyOpt = inStockListings.stream()
                .filter(l -> l.getWarrantyMonths() != null)
                .mapToInt(MarketplaceListing::getWarrantyMonths)
                .max();

        OptionalDouble minRatingOpt = inStockListings.stream()
                .filter(l -> l.getRating() != null)
                .mapToDouble(MarketplaceListing::getRating)
                .min();

        OptionalDouble maxRatingOpt = inStockListings.stream()
                .filter(l -> l.getRating() != null)
                .mapToDouble(MarketplaceListing::getRating)
                .max();

        List<CompareListingDTO> inStockDTOs = new ArrayList<>();

        for (MarketplaceListing listing : inStockListings) {
            CompareListingDTO dto = toDTO(listing);
            dto.setInStock(true);
            dto.setExcludedFromRanking(false);

            // 1. Price score: lowest = 1, highest = 0, linear in between (all equal = 1)
            double priceScore;
            if (maxPrice == minPrice) {
                priceScore = 1.0;
            } else {
                priceScore = (maxPrice - listing.getPrice()) / (maxPrice - minPrice);
            }
            dto.setPriceScore(priceScore);

            // 2. Warranty score: longest = 1, shortest = 0 (all equal = 1)
            Double warrantyScore = null;
            if (listing.getWarrantyMonths() != null && minWarrantyOpt.isPresent() && maxWarrantyOpt.isPresent()) {
                int minW = minWarrantyOpt.getAsInt();
                int maxW = maxWarrantyOpt.getAsInt();
                if (maxW == minW) {
                    warrantyScore = 1.0;
                } else {
                    warrantyScore = (double) (listing.getWarrantyMonths() - minW) / (maxW - minW);
                }
            }
            dto.setWarrantyScore(warrantyScore);

            // 3. Rating score: ABSOLUTE = rating / 5. RELATIVE = best 1, worst 0 (all equal = 1)
            Double ratingScore = null;
            if (listing.getRating() != null) {
                if ("RELATIVE".equalsIgnoreCase(rMethod)) {
                    if (minRatingOpt.isPresent() && maxRatingOpt.isPresent()) {
                        double minR = minRatingOpt.getAsDouble();
                        double maxR = maxRatingOpt.getAsDouble();
                        if (maxR == minR) {
                            ratingScore = 1.0;
                        } else {
                            ratingScore = (listing.getRating() - minR) / (maxR - minR);
                        }
                    }
                } else {
                    // ABSOLUTE (default)
                    ratingScore = listing.getRating() / 5.0;
                }
            }
            dto.setRatingScore(ratingScore);

            // 4. Value score with dynamic weight re-scaling for available factors
            double availableWeights = 0.0;
            double weightedSum = 0.0;

            availableWeights += weightPrice;
            weightedSum += weightPrice * priceScore;

            if (ratingScore != null) {
                availableWeights += weightRating;
                weightedSum += weightRating * ratingScore;
            }

            if (warrantyScore != null) {
                availableWeights += weightWarranty;
                weightedSum += weightWarranty * warrantyScore;
            }

            double rawScore = 0.0;
            if (availableWeights > 0) {
                rawScore = 100.0 * (weightedSum / availableWeights);
            }
            double finalScore = Math.round(rawScore * 100.0) / 100.0;
            dto.setValueScore(finalScore);

            inStockDTOs.add(dto);
        }

        // Rank in-stock listings: descending valueScore, tie-breaker: lower price, second tie-breaker: higher rating
        inStockDTOs.sort((a, b) -> {
            int cmp = Double.compare(b.getValueScore(), a.getValueScore());
            if (cmp != 0) return cmp;
            int priceCmp = Double.compare(a.getPrice(), b.getPrice());
            if (priceCmp != 0) return priceCmp;
            double rA = a.getRating() != null ? a.getRating() : 0.0;
            double rB = b.getRating() != null ? b.getRating() : 0.0;
            return Double.compare(rB, rA);
        });

        for (int i = 0; i < inStockDTOs.size(); i++) {
            inStockDTOs.get(i).setRank(i + 1);
        }

        // Best recommendation
        CompareListingDTO bestListing = inStockDTOs.get(0);

        // Lowest price in-stock listing
        CompareListingDTO lowestPriceListing = inStockDTOs.stream()
                .min(Comparator.comparingDouble(CompareListingDTO::getPrice))
                .orElse(bestListing);

        double extraCost = Math.max(0.0, Math.round((bestListing.getPrice() - lowestPriceListing.getPrice()) * 100.0) / 100.0);

        // Plain-language reason & explanation
        String reason;
        String explanation;
        if (bestListing.getMarketplace().equalsIgnoreCase(lowestPriceListing.getMarketplace())) {
            reason = "Lowest price and best overall factor balance";
            explanation = String.format("%s offers the best value (Score: %.2f) with the lowest price of ₹%,.0f.",
                    bestListing.getMarketplaceName(), bestListing.getValueScore(), bestListing.getPrice());
        } else {
            reason = String.format("Higher value score (%.2f) due to superior warranty/rating balance", bestListing.getValueScore());
            explanation = String.format("%s is recommended (Score: %.2f) despite costing ₹%,.0f more than %s (₹%,.0f), thanks to its superior warranty and rating profile.",
                    bestListing.getMarketplaceName(), bestListing.getValueScore(), extraCost,
                    lowestPriceListing.getMarketplaceName(), lowestPriceListing.getPrice());
        }

        BestMarketplaceDTO bestDTO = new BestMarketplaceDTO(
                bestListing.getMarketplace(),
                reason,
                bestListing.getValueScore(),
                bestListing.getPrice()
        );

        LowestPriceDTO lowestPriceDTO = new LowestPriceDTO(
                lowestPriceListing.getMarketplace(),
                lowestPriceListing.getPrice()
        );

        response.setBest(bestDTO);
        response.setLowestPrice(lowestPriceDTO);
        response.setExtraCost(extraCost);
        response.setExplanation(explanation);
        response.setAllOutOfStock(false);

        // Combine ranked in-stock listings followed by out-of-stock listings
        List<CompareListingDTO> finalListings = new ArrayList<>(inStockDTOs);
        for (MarketplaceListing oos : outOfStockListings) {
            CompareListingDTO oosDto = toDTO(oos);
            oosDto.setInStock(false);
            oosDto.setExcludedFromRanking(true);
            oosDto.setRank(null);
            oosDto.setValueScore(null);
            finalListings.add(oosDto);
        }

        response.setListings(finalListings);
        return response;
    }

    private CompareListingDTO toDTO(MarketplaceListing listing) {
        CompareListingDTO dto = new CompareListingDTO();
        String code = listing.getMarketplace() != null ? listing.getMarketplace().getCode() : "UNKNOWN";
        dto.setMarketplace(code);
        dto.setMarketplaceName(listing.getMarketplace() != null ? listing.getMarketplace().getName() : code);
        dto.setPrice(listing.getPrice());
        dto.setWarrantyText(listing.getWarrantyText());
        dto.setWarrantyMonths(listing.getWarrantyMonths());
        dto.setRating(listing.getRating());
        dto.setInStock(listing.isInStock());
        dto.setProductUrl(listing.getProductUrl());
        dto.setImageUrl(listing.getImageUrl() != null && !listing.getImageUrl().isEmpty()
                ? listing.getImageUrl()
                : (listing.getProduct() != null ? listing.getProduct().getImageUrl() : ""));
        dto.setPriceDate(listing.getPriceDate());
        dto.setDataSource(listing.getDataSource() != null ? listing.getDataSource() : "COLLECTED");
        return dto;
    }
}
