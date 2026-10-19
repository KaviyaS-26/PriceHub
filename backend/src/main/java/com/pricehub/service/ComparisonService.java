package com.pricehub.service;

import com.pricehub.dto.*;
import com.pricehub.engine.EffectiveCostCalculator;
import com.pricehub.engine.ProductMatcher;
import com.pricehub.engine.ValueScoreCalculator;
import com.pricehub.entity.Product;
import com.pricehub.entity.ProductSpecification;
import com.pricehub.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ComparisonService {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ProductMatcher productMatcher;
    private final EffectiveCostCalculator costCalculator;
    private final ValueScoreCalculator valueScoreCalculator;

    public ComparisonService(ProductRepository productRepository,
                             ProductService productService,
                             ProductMatcher productMatcher,
                             EffectiveCostCalculator costCalculator,
                             ValueScoreCalculator valueScoreCalculator) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.productMatcher = productMatcher;
        this.costCalculator = costCalculator;
        this.valueScoreCalculator = valueScoreCalculator;
    }

    public ComparisonResponse compareProducts(List<Long> productIds, String cardType) {
        ComparisonResponse response = new ComparisonResponse();

        if (productIds == null || productIds.isEmpty()) {
            response.setSmartSummary("Please select at least 2 products to compare.");
            return response;
        }

        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) {
            response.setSmartSummary("No products found for the provided IDs.");
            return response;
        }

        List<ProductDTO> productDTOs = new ArrayList<>();
        List<MarketplaceListingDTO> allListings = new ArrayList<>();

        for (Product p : products) {
            ProductDTO pDto = productService.mapProductToDTO(p, cardType);
            productDTOs.add(pDto);
            allListings.addAll(pDto.getListings());
        }
        response.setProducts(productDTOs);

        // 1. Run Smart Matcher (if 2 products)
        if (products.size() >= 2) {
            MatchResultDTO matchResult = productMatcher.matchProducts(products.get(0), products.get(1));
            response.setMatchResult(matchResult);
        }

        // 2. Build Specification Matrix
        Map<String, List<String>> specMatrix = new LinkedHashMap<>();
        Set<String> allSpecKeys = new LinkedHashSet<>();

        for (Product p : products) {
            if (p.getSpecifications() != null) {
                for (ProductSpecification s : p.getSpecifications()) {
                    allSpecKeys.add(s.getSpecKey());
                }
            }
        }

        for (String key : allSpecKeys) {
            List<String> values = new ArrayList<>();
            for (Product p : products) {
                String val = "Not available";
                if (p.getSpecifications() != null) {
                    for (ProductSpecification s : p.getSpecifications()) {
                        if (key.equalsIgnoreCase(s.getSpecKey())) {
                            val = s.getSpecValue();
                            break;
                        }
                    }
                }
                values.add(val);
            }
            specMatrix.put(key, values);
        }
        response.setSpecificationMatrix(specMatrix);

        // 3. Find Best Listings across all compared products
        if (!allListings.isEmpty()) {
            MarketplaceListingDTO lowestCostListing = allListings.stream()
                    .min(Comparator.comparingDouble(l -> l.getCostBreakdown() != null ? l.getCostBreakdown().getEffectiveCost() : Double.MAX_VALUE))
                    .orElse(allListings.get(0));
            response.setLowestEffectiveCostListing(lowestCostListing);

            MarketplaceListingDTO bestRatedListing = allListings.stream()
                    .max(Comparator.comparingDouble(l -> l.getRating() != null ? l.getRating() : 0.0))
                    .orElse(allListings.get(0));
            response.setBestRatedListing(bestRatedListing);

            double minPrice = allListings.stream().mapToDouble(l -> l.getCostBreakdown().getEffectiveCost()).min().orElse(1000);
            double maxPrice = allListings.stream().mapToDouble(l -> l.getCostBreakdown().getEffectiveCost()).max().orElse(100000);

            for (MarketplaceListingDTO l : allListings) {
                // Approximate listing evaluation
                double score = valueScoreCalculator.calculateValueScore(null, l.getCostBreakdown().getEffectiveCost(), minPrice, maxPrice, "BEST_VALUE");
                l.setValueScore(score);
            }

            MarketplaceListingDTO bestValueListing = allListings.stream()
                    .max(Comparator.comparingDouble(l -> l.getValueScore() != null ? l.getValueScore() : 0.0))
                    .orElse(allListings.get(0));
            response.setBestValueListing(bestValueListing);

            double priceDiff = maxPrice - minPrice;
            double savingsPercent = maxPrice > 0 ? (priceDiff / maxPrice) * 100.0 : 0.0;
            response.setMaxPriceDifference(priceDiff);
            response.setMaxSavingsPercentage(Math.round(savingsPercent * 10.0) / 10.0);
        }

        // 4. Identify Key Differences
        List<String> keyDifferences = new ArrayList<>();
        if (productDTOs.size() >= 2) {
            ProductDTO p1 = productDTOs.get(0);
            ProductDTO p2 = productDTOs.get(1);

            if (p1.getLowestEffectivePrice() != null && p2.getLowestEffectivePrice() != null) {
                double diff = Math.abs(p1.getLowestEffectivePrice() - p2.getLowestEffectivePrice());
                if (diff > 0) {
                    ProductDTO cheaper = p1.getLowestEffectivePrice() < p2.getLowestEffectivePrice() ? p1 : p2;
                    keyDifferences.add(String.format("%s is cheaper by ₹%,.0f (Effective ₹%,.0f vs ₹%,.0f)",
                            cheaper.getTitle(), diff, cheaper.getLowestEffectivePrice(),
                            cheaper == p1 ? p2.getLowestEffectivePrice() : p1.getLowestEffectivePrice()));
                }
            }

            if (p1.getRatingAverage() != null && p2.getRatingAverage() != null) {
                if (Math.abs(p1.getRatingAverage() - p2.getRatingAverage()) >= 0.2) {
                    ProductDTO better = p1.getRatingAverage() > p2.getRatingAverage() ? p1 : p2;
                    keyDifferences.add(String.format("%s has higher user satisfaction (%.1f★ vs %.1f★)",
                            better.getTitle(), better.getRatingAverage(), better == p1 ? p2.getRatingAverage() : p1.getRatingAverage()));
                }
            }
        }
        response.setKeyDifferences(keyDifferences);

        // Smart Summary
        if (response.getLowestEffectiveCostListing() != null) {
            response.setSmartSummary(String.format(
                    "Across %d compared products, the best overall deal is on %s for %s at an effective cost of ₹%,.0f (Save up to %.0f%%).",
                    products.size(),
                    response.getLowestEffectiveCostListing().getMarketplace().getName(),
                    response.getLowestEffectiveCostListing().getProductTitle(),
                    response.getLowestEffectiveCostListing().getCostBreakdown().getEffectiveCost(),
                    response.getMaxSavingsPercentage() != null ? response.getMaxSavingsPercentage() : 0.0
            ));
        }

        return response;
    }
}
