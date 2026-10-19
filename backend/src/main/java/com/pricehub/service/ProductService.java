package com.pricehub.service;

import com.pricehub.adapter.MarketplaceAdapter;
import com.pricehub.adapter.MarketplaceAdapterRegistry;
import com.pricehub.dto.*;
import com.pricehub.engine.EffectiveCostCalculator;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Product;
import com.pricehub.entity.ProductSpecification;
import com.pricehub.repository.MarketplaceListingRepository;
import com.pricehub.repository.MarketplaceRepository;
import com.pricehub.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MarketplaceListingRepository listingRepository;
    private final MarketplaceRepository marketplaceRepository;
    private final MarketplaceAdapterRegistry adapterRegistry;
    private final EffectiveCostCalculator costCalculator;

    public ProductService(ProductRepository productRepository,
                          MarketplaceListingRepository listingRepository,
                          MarketplaceRepository marketplaceRepository,
                          MarketplaceAdapterRegistry adapterRegistry,
                          EffectiveCostCalculator costCalculator) {
        this.productRepository = productRepository;
        this.listingRepository = listingRepository;
        this.marketplaceRepository = marketplaceRepository;
        this.adapterRegistry = adapterRegistry;
        this.costCalculator = costCalculator;
    }

    public List<ProductDTO> searchProducts(String category, String keyword, String marketplaceCode,
                                          Double minPrice, Double maxPrice, Double minRating, String sortBy) {
        List<Product> products;

        if (keyword != null && !keyword.isBlank()) {
            products = productRepository.searchByKeyword(keyword.trim());
            if (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) {
                products = products.stream()
                        .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(category.trim()))
                        .collect(Collectors.toList());
            }
        } else if (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) {
            products = productRepository.findByCategoryIgnoreCase(category.trim());
        } else {
            products = productRepository.findAll();
        }

        // Map and filter products
        List<ProductDTO> dtos = products.stream()
                .map(p -> mapProductToDTO(p, null))
                .filter(p -> {
                    if (minRating != null && minRating > 0 && p.getRatingAverage() != null && p.getRatingAverage() < minRating) {
                        return false;
                    }
                    if (minPrice != null && minPrice > 0 && p.getLowestEffectivePrice() != null && p.getLowestEffectivePrice() < minPrice) {
                        return false;
                    }
                    if (maxPrice != null && maxPrice > 0 && p.getLowestEffectivePrice() != null && p.getLowestEffectivePrice() > maxPrice) {
                        return false;
                    }
                    if (marketplaceCode != null && !marketplaceCode.isBlank() && !"ALL".equalsIgnoreCase(marketplaceCode)) {
                        boolean hasMatchingListing = p.getListings() != null && p.getListings().stream().anyMatch(l ->
                                l.getMarketplace() != null && (
                                        l.getMarketplace().getCode().equalsIgnoreCase(marketplaceCode.trim()) ||
                                        l.getMarketplace().getName().equalsIgnoreCase(marketplaceCode.trim())
                                )
                        );
                        if (!hasMatchingListing) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // Sort
        if ("PRICE_LOW_HIGH".equalsIgnoreCase(sortBy) || "LOWEST_PRICE".equalsIgnoreCase(sortBy)) {
            dtos.sort(Comparator.comparingDouble(p -> p.getLowestEffectivePrice() != null ? p.getLowestEffectivePrice() : 0.0));
        } else if ("PRICE_HIGH_LOW".equalsIgnoreCase(sortBy)) {
            dtos.sort((a, b) -> Double.compare(b.getLowestEffectivePrice() != null ? b.getLowestEffectivePrice() : 0.0,
                    a.getLowestEffectivePrice() != null ? a.getLowestEffectivePrice() : 0.0));
        } else if ("RATING".equalsIgnoreCase(sortBy) || "HIGHER_RATING".equalsIgnoreCase(sortBy)) {
            dtos.sort((a, b) -> Double.compare(b.getRatingAverage() != null ? b.getRatingAverage() : 0.0,
                    a.getRatingAverage() != null ? a.getRatingAverage() : 0.0));
        }

        return dtos;
    }

    public ProductDTO getProductById(Long id, String cardType) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        return mapProductToDTO(product, cardType);
    }

    public List<MarketplaceListingDTO> getTopDeals(String category, Integer limit) {
        List<Product> products;
        if (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) {
            products = productRepository.findByCategoryIgnoreCase(category.trim());
        } else {
            products = productRepository.findAll();
        }

        List<MarketplaceListingDTO> topUniqueDeals = new ArrayList<>();

        for (Product product : products) {
            ProductDTO pDto = mapProductToDTO(product, null);
            if (pDto.getListings() != null && !pDto.getListings().isEmpty()) {
                MarketplaceListingDTO bestDeal = pDto.getListings().get(0);

                // Deep copy child listings to break any cyclic reference during JSON serialization
                List<MarketplaceListingDTO> cleanListings = new ArrayList<>();
                for (MarketplaceListingDTO item : pDto.getListings()) {
                    MarketplaceListingDTO copy = new MarketplaceListingDTO();
                    copy.setId(item.getId());
                    copy.setProductId(item.getProductId());
                    copy.setProductTitle(item.getProductTitle());
                    copy.setMarketplace(item.getMarketplace());
                    copy.setExternalListingId(item.getExternalListingId());
                    copy.setTitle(item.getTitle());
                    copy.setListedPrice(item.getListedPrice());
                    copy.setDeliveryCharge(item.getDeliveryCharge());
                    copy.setMandatoryCharges(item.getMandatoryCharges());
                    copy.setRating(item.getRating());
                    copy.setReviewCount(item.getReviewCount());
                    copy.setWarrantyMonths(item.getWarrantyMonths());
                    copy.setDeliveryEstimateDays(item.getDeliveryEstimateDays());
                    copy.setStockStatus(item.getStockStatus());
                    copy.setIsVerified(item.getIsVerified());
                    copy.setProductUrl(item.getProductUrl());
                    copy.setDataSource(item.getDataSource());
                    copy.setLastUpdated(item.getLastUpdated());
                    copy.setOffers(item.getOffers());
                    copy.setCostBreakdown(item.getCostBreakdown());
                    copy.setValueScore(item.getValueScore());
                    copy.setBadges(item.getBadges());
                    copy.setAllMarketplaceListings(new ArrayList<>());
                    cleanListings.add(copy);
                }

                MarketplaceListingDTO dealEntry = new MarketplaceListingDTO();
                dealEntry.setId(bestDeal.getId());
                dealEntry.setProductId(bestDeal.getProductId());
                dealEntry.setProductTitle(bestDeal.getProductTitle());
                dealEntry.setMarketplace(bestDeal.getMarketplace());
                dealEntry.setExternalListingId(bestDeal.getExternalListingId());
                dealEntry.setTitle(bestDeal.getTitle());
                dealEntry.setListedPrice(bestDeal.getListedPrice());
                dealEntry.setDeliveryCharge(bestDeal.getDeliveryCharge());
                dealEntry.setMandatoryCharges(bestDeal.getMandatoryCharges());
                dealEntry.setRating(bestDeal.getRating());
                dealEntry.setReviewCount(bestDeal.getReviewCount());
                dealEntry.setWarrantyMonths(bestDeal.getWarrantyMonths());
                dealEntry.setDeliveryEstimateDays(bestDeal.getDeliveryEstimateDays());
                dealEntry.setStockStatus(bestDeal.getStockStatus());
                dealEntry.setIsVerified(bestDeal.getIsVerified());
                dealEntry.setProductUrl(bestDeal.getProductUrl());
                dealEntry.setDataSource(bestDeal.getDataSource());
                dealEntry.setLastUpdated(bestDeal.getLastUpdated());
                dealEntry.setOffers(bestDeal.getOffers());
                dealEntry.setCostBreakdown(bestDeal.getCostBreakdown());
                dealEntry.setValueScore(bestDeal.getValueScore());
                dealEntry.setBadges(bestDeal.getBadges());
                dealEntry.setAllMarketplaceListings(cleanListings);

                topUniqueDeals.add(dealEntry);
            }
        }

        int max = limit != null ? limit : 12;
        return topUniqueDeals.stream()
                .sorted((a, b) -> Double.compare(
                        b.getCostBreakdown() != null ? b.getCostBreakdown().getSavingsPercentage() : 0,
                        a.getCostBreakdown() != null ? a.getCostBreakdown().getSavingsPercentage() : 0))
                .limit(max)
                .collect(Collectors.toList());
    }

    public ProductDTO mapProductToDTO(Product p, String cardType) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setBrand(p.getBrand());
        dto.setModelNumber(p.getModelNumber());
        dto.setCategory(p.getCategory());
        dto.setBasePrice(p.getBasePrice());
        dto.setImageUrl(p.getImageUrl());
        dto.setDescription(p.getDescription());
        dto.setRatingAverage(p.getRatingAverage());
        dto.setCreatedAt(p.getCreatedAt());

        // Specifications
        List<ProductSpecificationDTO> specDTOs = new ArrayList<>();
        if (p.getSpecifications() != null) {
            for (ProductSpecification s : p.getSpecifications()) {
                specDTOs.add(new ProductSpecificationDTO(s.getId(), s.getSpecKey(), s.getSpecValue(), s.getSpecGroup()));
            }
        }
        dto.setSpecifications(specDTOs);

        // Listings & effective prices
        List<MarketplaceListingDTO> listingDTOs = new ArrayList<>();
        double lowestEffective = Double.MAX_VALUE;
        double highestEffective = 0.0;
        String bestMarketplace = "Amazon India";

        if (p.getListings() != null && !p.getListings().isEmpty()) {
            for (MarketplaceListing l : p.getListings()) {
                MarketplaceAdapter adapter = adapterRegistry.getAdapter(l.getMarketplace() != null ? l.getMarketplace().getCode() : null);
                MarketplaceListingDTO lDto;
                if (adapter != null) {
                    lDto = adapter.mapToDTO(l, cardType);
                } else {
                    lDto = new MarketplaceListingDTO();
                    lDto.setId(l.getId());
                    lDto.setProductId(l.getProduct() != null ? l.getProduct().getId() : null);
                    lDto.setProductTitle(l.getProduct() != null ? l.getProduct().getTitle() : null);
                    if (l.getMarketplace() != null) {
                        lDto.setMarketplace(new MarketplaceDTO(
                                l.getMarketplace().getId(),
                                l.getMarketplace().getName(),
                                l.getMarketplace().getCode(),
                                l.getMarketplace().getBaseUrl(),
                                l.getMarketplace().getLogoUrl(),
                                l.getMarketplace().getTrustScore()
                        ));
                    }
                    lDto.setTitle(l.getTitle());
                    lDto.setListedPrice(l.getListedPrice());
                    lDto.setDeliveryCharge(l.getDeliveryCharge());
                    lDto.setMandatoryCharges(l.getMandatoryCharges());
                    lDto.setRating(l.getRating());
                    lDto.setReviewCount(l.getReviewCount());
                    lDto.setWarrantyMonths(l.getWarrantyMonths());
                    lDto.setStockStatus(l.getStockStatus());
                    lDto.setIsVerified(l.getIsVerified());
                    lDto.setProductUrl(l.getProductUrl());
                    lDto.setDataSource(l.getDataSource());
                    lDto.setLastUpdated(l.getLastUpdated());
                }

                CostBreakdownDTO breakdown = costCalculator.computeEffectiveCost(l, cardType);
                lDto.setCostBreakdown(breakdown);

                double eff = breakdown.getEffectiveCost();
                if (eff < lowestEffective) {
                    lowestEffective = eff;
                    bestMarketplace = l.getMarketplace().getName();
                }
                if (eff > highestEffective) {
                    highestEffective = eff;
                }

                listingDTOs.add(lDto);
            }
        } else {
            lowestEffective = p.getBasePrice();
            highestEffective = p.getBasePrice();
        }

        // Sort listings by lowest effective price first
        listingDTOs.sort(Comparator.comparingDouble(l -> l.getCostBreakdown() != null ? l.getCostBreakdown().getEffectiveCost() : 0.0));

        dto.setListings(listingDTOs);
        dto.setAvailableListingsCount(listingDTOs.size());
        dto.setLowestEffectivePrice(lowestEffective < Double.MAX_VALUE ? lowestEffective : p.getBasePrice());
        dto.setHighestEffectivePrice(highestEffective > 0 ? highestEffective : p.getBasePrice());
        dto.setBestValueMarketplace(bestMarketplace);

        return dto;
    }

    public List<String> getAllCategories() {
        List<String> categories = productRepository.findDistinctCategories();
        if (categories == null || categories.isEmpty()) {
            return Arrays.asList("Audio", "Clothing", "Footwear", "Watches", "Bags", "Home Decor", "Kids");
        }
        return categories;
    }
}
