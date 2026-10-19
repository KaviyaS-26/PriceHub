package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;

public class QualifyingProductDTO {
    private ProductDTO product;
    private MarketplaceListingDTO bestListing;
    private Double effectiveCost;
    private Double valueScore;
    private String qualificationSummary;
    private List<String> matchedCriteria = new ArrayList<>();
    private String tradeOffAnalysis; // Trade-off between price, spec, delivery

    public QualifyingProductDTO() {}

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public MarketplaceListingDTO getBestListing() { return bestListing; }
    public void setBestListing(MarketplaceListingDTO bestListing) { this.bestListing = bestListing; }

    public Double getEffectiveCost() { return effectiveCost; }
    public void setEffectiveCost(Double effectiveCost) { this.effectiveCost = effectiveCost; }

    public Double getValueScore() { return valueScore; }
    public void setValueScore(Double valueScore) { this.valueScore = valueScore; }

    public String getQualificationSummary() { return qualificationSummary; }
    public void setQualificationSummary(String qualificationSummary) { this.qualificationSummary = qualificationSummary; }

    public List<String> getMatchedCriteria() { return matchedCriteria; }
    public void setMatchedCriteria(List<String> matchedCriteria) { this.matchedCriteria = matchedCriteria; }

    public String getTradeOffAnalysis() { return tradeOffAnalysis; }
    public void setTradeOffAnalysis(String tradeOffAnalysis) { this.tradeOffAnalysis = tradeOffAnalysis; }
}
