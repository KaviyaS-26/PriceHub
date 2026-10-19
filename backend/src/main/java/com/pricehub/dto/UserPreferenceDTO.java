package com.pricehub.dto;

public class UserPreferenceDTO {
    private String preferredCategory;
    private Double minBudget;
    private Double maxBudget;
    private String preferredBrand;
    private String preferredMarketplace;
    private Integer maxDeliveryDays;
    private String defaultSort;

    public UserPreferenceDTO() {}

    public UserPreferenceDTO(String preferredCategory, Double minBudget, Double maxBudget, String preferredBrand, String preferredMarketplace, Integer maxDeliveryDays, String defaultSort) {
        this.preferredCategory = preferredCategory;
        this.minBudget = minBudget;
        this.maxBudget = maxBudget;
        this.preferredBrand = preferredBrand;
        this.preferredMarketplace = preferredMarketplace;
        this.maxDeliveryDays = maxDeliveryDays;
        this.defaultSort = defaultSort;
    }

    public String getPreferredCategory() { return preferredCategory; }
    public void setPreferredCategory(String preferredCategory) { this.preferredCategory = preferredCategory; }

    public Double getMinBudget() { return minBudget; }
    public void setMinBudget(Double minBudget) { this.minBudget = minBudget; }

    public Double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }

    public String getPreferredBrand() { return preferredBrand; }
    public void setPreferredBrand(String preferredBrand) { this.preferredBrand = preferredBrand; }

    public String getPreferredMarketplace() { return preferredMarketplace; }
    public void setPreferredMarketplace(String preferredMarketplace) { this.preferredMarketplace = preferredMarketplace; }

    public Integer getMaxDeliveryDays() { return maxDeliveryDays; }
    public void setMaxDeliveryDays(Integer maxDeliveryDays) { this.maxDeliveryDays = maxDeliveryDays; }

    public String getDefaultSort() { return defaultSort; }
    public void setDefaultSort(String defaultSort) { this.defaultSort = defaultSort; }
}
