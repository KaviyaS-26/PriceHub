package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;

public class RecommendationResponse {
    private String category;
    private String userPriority;
    private RecommendedItemDTO lowestCostRecommendation;
    private RecommendedItemDTO bestValueRecommendation;
    private RecommendedItemDTO bestSpecRecommendation;
    private List<RecommendedItemDTO> allRankedItems = new ArrayList<>();
    private String algorithmSummary;

    public RecommendationResponse() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUserPriority() { return userPriority; }
    public void setUserPriority(String userPriority) { this.userPriority = userPriority; }

    public RecommendedItemDTO getLowestCostRecommendation() { return lowestCostRecommendation; }
    public void setLowestCostRecommendation(RecommendedItemDTO lowestCostRecommendation) { this.lowestCostRecommendation = lowestCostRecommendation; }

    public RecommendedItemDTO getBestValueRecommendation() { return bestValueRecommendation; }
    public void setBestValueRecommendation(RecommendedItemDTO bestValueRecommendation) { this.bestValueRecommendation = bestValueRecommendation; }

    public RecommendedItemDTO getBestSpecRecommendation() { return bestSpecRecommendation; }
    public void setBestSpecRecommendation(RecommendedItemDTO bestSpecRecommendation) { this.bestSpecRecommendation = bestSpecRecommendation; }

    public List<RecommendedItemDTO> getAllRankedItems() { return allRankedItems; }
    public void setAllRankedItems(List<RecommendedItemDTO> allRankedItems) { this.allRankedItems = allRankedItems; }

    public String getAlgorithmSummary() { return algorithmSummary; }
    public void setAlgorithmSummary(String algorithmSummary) { this.algorithmSummary = algorithmSummary; }
}
