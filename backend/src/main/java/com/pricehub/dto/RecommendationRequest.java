package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;

public class RecommendationRequest {
    private String category;
    private Double maxBudget;
    private String userPriority = "BEST_VALUE"; // LOWEST_PRICE, HIGHER_RATING, BETTER_SPECS, LONGER_WARRANTY, FASTER_DELIVERY, BEST_VALUE
    private String preferredMarketplace;
    private String preferredBrand;
    private String cardType;

    public RecommendationRequest() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }

    public String getUserPriority() { return userPriority; }
    public void setUserPriority(String userPriority) { this.userPriority = userPriority; }

    public String getPreferredMarketplace() { return preferredMarketplace; }
    public void setPreferredMarketplace(String preferredMarketplace) { this.preferredMarketplace = preferredMarketplace; }

    public String getPreferredBrand() { return preferredBrand; }
    public void setPreferredBrand(String preferredBrand) { this.preferredBrand = preferredBrand; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }
}
