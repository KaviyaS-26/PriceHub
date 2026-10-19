package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComparisonResponse {
    private List<ProductDTO> products = new ArrayList<>();
    private MatchResultDTO matchResult;
    private MarketplaceListingDTO lowestEffectiveCostListing;
    private MarketplaceListingDTO bestRatedListing;
    private MarketplaceListingDTO bestValueListing;
    private Double maxPriceDifference;
    private Double maxSavingsPercentage;
    private Map<String, List<String>> specificationMatrix; // specKey -> [prod1Val, prod2Val, ...]
    private List<String> keyDifferences = new ArrayList<>();
    private String smartSummary;

    public ComparisonResponse() {}

    public List<ProductDTO> getProducts() { return products; }
    public void setProducts(List<ProductDTO> products) { this.products = products; }

    public MatchResultDTO getMatchResult() { return matchResult; }
    public void setMatchResult(MatchResultDTO matchResult) { this.matchResult = matchResult; }

    public MarketplaceListingDTO getLowestEffectiveCostListing() { return lowestEffectiveCostListing; }
    public void setLowestEffectiveCostListing(MarketplaceListingDTO lowestEffectiveCostListing) { this.lowestEffectiveCostListing = lowestEffectiveCostListing; }

    public MarketplaceListingDTO getBestRatedListing() { return bestRatedListing; }
    public void setBestRatedListing(MarketplaceListingDTO bestRatedListing) { this.bestRatedListing = bestRatedListing; }

    public MarketplaceListingDTO getBestValueListing() { return bestValueListing; }
    public void setBestValueListing(MarketplaceListingDTO bestValueListing) { this.bestValueListing = bestValueListing; }

    public Double getMaxPriceDifference() { return maxPriceDifference; }
    public void setMaxPriceDifference(Double maxPriceDifference) { this.maxPriceDifference = maxPriceDifference; }

    public Double getMaxSavingsPercentage() { return maxSavingsPercentage; }
    public void setMaxSavingsPercentage(Double maxSavingsPercentage) { this.maxSavingsPercentage = maxSavingsPercentage; }

    public Map<String, List<String>> getSpecificationMatrix() { return specificationMatrix; }
    public void setSpecificationMatrix(Map<String, List<String>> specificationMatrix) { this.specificationMatrix = specificationMatrix; }

    public List<String> getKeyDifferences() { return keyDifferences; }
    public void setKeyDifferences(List<String> keyDifferences) { this.keyDifferences = keyDifferences; }

    public String getSmartSummary() { return smartSummary; }
    public void setSmartSummary(String smartSummary) { this.smartSummary = smartSummary; }
}
