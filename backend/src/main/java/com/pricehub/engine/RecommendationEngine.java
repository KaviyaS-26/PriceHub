package com.pricehub.engine;

import com.pricehub.adapter.MarketplaceAdapter;
import com.pricehub.adapter.MarketplaceAdapterRegistry;
import com.pricehub.dto.*;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Product;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Section 10: Recommendation Engine.
 * Evaluates listings, computes value scores, assigns recommendation labels,
 * and generates human-readable plain language explanations.
 */
@Component
public class RecommendationEngine {

    private final EffectiveCostCalculator costCalculator;
    private final ValueScoreCalculator valueScoreCalculator;
    private final MarketplaceAdapterRegistry adapterRegistry;

    public RecommendationEngine(EffectiveCostCalculator costCalculator,
                                ValueScoreCalculator valueScoreCalculator,
                                MarketplaceAdapterRegistry adapterRegistry) {
        this.costCalculator = costCalculator;
        this.valueScoreCalculator = valueScoreCalculator;
        this.adapterRegistry = adapterRegistry;
    }

    public RecommendationResponse generateRecommendations(List<Product> candidateProducts, RecommendationRequest request) {
        RecommendationResponse response = new RecommendationResponse();
        response.setCategory(request.getCategory());
        response.setUserPriority(request.getUserPriority());

        if (candidateProducts == null || candidateProducts.isEmpty()) {
            response.setAlgorithmSummary("No candidate products found matching the criteria.");
            return response;
        }

        // 1. Gather all listings and calculate effective costs
        List<ListingEvaluation> evaluations = new ArrayList<>();
        double minPrice = Double.MAX_VALUE;
        double maxPrice = 0.0;

        for (Product product : candidateProducts) {
            if (product.getListings() == null) continue;

            for (MarketplaceListing listing : product.getListings()) {
                if (request.getPreferredMarketplace() != null &&
                    !"ALL".equalsIgnoreCase(request.getPreferredMarketplace()) &&
                    !request.getPreferredMarketplace().equalsIgnoreCase(listing.getMarketplace().getCode())) {
                    continue;
                }

                CostBreakdownDTO cost = costCalculator.computeEffectiveCost(listing, request.getCardType());
                double effectiveCost = cost.getEffectiveCost();

                if (request.getMaxBudget() != null && effectiveCost > request.getMaxBudget()) {
                    continue; // Out of budget
                }

                if (effectiveCost < minPrice) minPrice = effectiveCost;
                if (effectiveCost > maxPrice) maxPrice = effectiveCost;

                evaluations.add(new ListingEvaluation(product, listing, cost));
            }
        }

        if (evaluations.isEmpty()) {
            response.setAlgorithmSummary("No listings qualified within the specified budget of ₹" + request.getMaxBudget());
            return response;
        }

        if (minPrice == Double.MAX_VALUE) minPrice = 10000;
        if (maxPrice == 0.0) maxPrice = 100000;

        // 2. Compute Value Scores
        for (ListingEvaluation eval : evaluations) {
            double vScore = valueScoreCalculator.calculateValueScore(
                    eval.listing,
                    eval.costBreakdown.getEffectiveCost(),
                    minPrice,
                    maxPrice,
                    request.getUserPriority()
            );
            eval.valueScore = vScore;
        }

        // 3. Sort evaluations
        List<ListingEvaluation> sortedByValue = evaluations.stream()
                .sorted((a, b) -> Double.compare(b.valueScore, a.valueScore))
                .collect(Collectors.toList());

        List<ListingEvaluation> sortedByCost = evaluations.stream()
                .sorted(Comparator.comparingDouble(a -> a.costBreakdown.getEffectiveCost()))
                .collect(Collectors.toList());

        List<ListingEvaluation> sortedByRating = evaluations.stream()
                .sorted((a, b) -> Double.compare(b.listing.getRating() != null ? b.listing.getRating() : 0,
                                                 a.listing.getRating() != null ? a.listing.getRating() : 0))
                .collect(Collectors.toList());

        // 4. Create Recommended Item DTOs
        ListingEvaluation bestValueEval = sortedByValue.get(0);
        ListingEvaluation lowestCostEval = sortedByCost.get(0);
        ListingEvaluation bestSpecEval = sortedByRating.get(0);

        RecommendedItemDTO bestValueDTO = buildRecommendedItem(bestValueEval, "Best Overall Value",
                String.format("Recommended because it achieves the highest multi-factor score (%.1f/100) considering effective cost (₹%,.0f), customer rating (%.1f★), and delivery time (%d days).",
                        bestValueEval.valueScore, bestValueEval.costBreakdown.getEffectiveCost(),
                        bestValueEval.listing.getRating(), bestValueEval.listing.getDeliveryEstimateDays()));

        RecommendedItemDTO lowestCostDTO = buildRecommendedItem(lowestCostEval, "Lowest Cost",
                String.format("Recommended as the most economical option with the lowest verified effective purchase price of ₹%,.0f across all monitored marketplaces.",
                        lowestCostEval.costBreakdown.getEffectiveCost()));

        RecommendedItemDTO bestSpecDTO = buildRecommendedItem(bestSpecEval, "Best Specification Match",
                String.format("Recommended for power users requiring top-tier hardware and highest customer satisfaction (%.1f★ rating with %d verified reviews).",
                        bestSpecEval.listing.getRating(), bestSpecEval.listing.getReviewCount()));

        response.setBestValueRecommendation(bestValueDTO);
        response.setLowestCostRecommendation(lowestCostDTO);
        response.setBestSpecRecommendation(bestSpecDTO);

        // Map all unique products
        Set<Long> addedProductIds = new HashSet<>();
        List<RecommendedItemDTO> allRanked = new ArrayList<>();

        for (ListingEvaluation eval : sortedByValue) {
            if (!addedProductIds.contains(eval.product.getId())) {
                addedProductIds.add(eval.product.getId());
                String label = eval == bestValueEval ? "Best Overall Value" :
                               eval == lowestCostEval ? "Lowest Cost" : "Great Value Alternative";
                allRanked.add(buildRecommendedItem(eval, label,
                        String.format("Effective price ₹%,.0f on %s with value score %.1f/100.",
                                eval.costBreakdown.getEffectiveCost(), eval.listing.getMarketplace().getName(), eval.valueScore)));
            }
        }

        response.setAllRankedItems(allRanked);
        response.setAlgorithmSummary(String.format("Evaluated %d marketplace listings using dynamic weighted multi-attribute optimization with priority '%s'.",
                evaluations.size(), request.getUserPriority()));

        return response;
    }

    private RecommendedItemDTO buildRecommendedItem(ListingEvaluation eval, String label, String explanation) {
        RecommendedItemDTO item = new RecommendedItemDTO();
        item.setProduct(mapProductToDTO(eval.product));

        MarketplaceAdapter adapter = adapterRegistry.getAdapter(eval.listing.getMarketplace().getCode());
        MarketplaceListingDTO listingDTO = adapter != null ? adapter.mapToDTO(eval.listing, null) : new MarketplaceListingDTO();
        listingDTO.setCostBreakdown(eval.costBreakdown);
        listingDTO.setValueScore(eval.valueScore);

        item.setBestListing(listingDTO);
        item.setValueScore(eval.valueScore);
        item.setRecommendationLabel(label);
        item.setPlainLanguageExplanation(explanation);

        // Pros and Cons
        List<String> pros = new ArrayList<>();
        List<String> cons = new ArrayList<>();

        if (eval.costBreakdown.getTotalDiscountAmount() > 0) {
            pros.add(String.format("Save ₹%,.0f with available discounts", eval.costBreakdown.getTotalDiscountAmount()));
        }
        if (eval.listing.getRating() >= 4.5) {
            pros.add(String.format("Top-rated (%.1f★) by verified buyers", eval.listing.getRating()));
        }
        if (eval.listing.getDeliveryEstimateDays() <= 2) {
            pros.add("Fast delivery within 48 hours");
        }
        if (eval.listing.getWarrantyMonths() >= 12) {
            pros.add(String.format("%d Months official brand warranty", eval.listing.getWarrantyMonths()));
        }

        if (eval.listing.getDeliveryCharge() > 0) {
            cons.add(String.format("₹%,.0f shipping fee applied", eval.listing.getDeliveryCharge()));
        }
        if (eval.listing.getDeliveryEstimateDays() > 3) {
            cons.add(String.format("Delivery takes %d days", eval.listing.getDeliveryEstimateDays()));
        }

        item.setHighlightPros(pros);
        item.setHighlightCons(cons);

        return item;
    }

    private ProductDTO mapProductToDTO(Product p) {
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

        List<ProductSpecificationDTO> specDTOs = new ArrayList<>();
        if (p.getSpecifications() != null) {
            for (var s : p.getSpecifications()) {
                specDTOs.add(new ProductSpecificationDTO(s.getId(), s.getSpecKey(), s.getSpecValue(), s.getSpecGroup()));
            }
        }
        dto.setSpecifications(specDTOs);
        return dto;
    }

    private static class ListingEvaluation {
        Product product;
        MarketplaceListing listing;
        CostBreakdownDTO costBreakdown;
        double valueScore;

        ListingEvaluation(Product product, MarketplaceListing listing, CostBreakdownDTO costBreakdown) {
            this.product = product;
            this.listing = listing;
            this.costBreakdown = costBreakdown;
        }
    }
}
