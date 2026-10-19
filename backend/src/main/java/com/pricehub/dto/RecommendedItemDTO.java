package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;

public class RecommendedItemDTO {
    private ProductDTO product;
    private MarketplaceListingDTO bestListing;
    private Double valueScore; // 0-100
    private String recommendationLabel; // "Lowest Cost", "Best Overall Value", "Best Specification Match", "Editor's Choice"
    private String plainLanguageExplanation;
    private List<String> highlightPros = new ArrayList<>();
    private List<String> highlightCons = new ArrayList<>();

    public RecommendedItemDTO() {}

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public MarketplaceListingDTO getBestListing() { return bestListing; }
    public void setBestListing(MarketplaceListingDTO bestListing) { this.bestListing = bestListing; }

    public Double getValueScore() { return valueScore; }
    public void setValueScore(Double valueScore) { this.valueScore = valueScore; }

    public String getRecommendationLabel() { return recommendationLabel; }
    public void setRecommendationLabel(String recommendationLabel) { this.recommendationLabel = recommendationLabel; }

    public String getPlainLanguageExplanation() { return plainLanguageExplanation; }
    public void setPlainLanguageExplanation(String plainLanguageExplanation) { this.plainLanguageExplanation = plainLanguageExplanation; }

    public List<String> getHighlightPros() { return highlightPros; }
    public void setHighlightPros(List<String> highlightPros) { this.highlightPros = highlightPros; }

    public List<String> getHighlightCons() { return highlightCons; }
    public void setHighlightCons(List<String> highlightCons) { this.highlightCons = highlightCons; }
}
