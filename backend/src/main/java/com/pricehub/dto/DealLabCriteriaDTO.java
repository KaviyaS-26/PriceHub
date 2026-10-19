package com.pricehub.dto;

public class DealLabCriteriaDTO {
    private String category;
    private Double maxBudget;
    private Double minRating;
    private Integer minWarrantyMonths;
    private String marketplace;
    private String preferredMarketplace;

    // Deprecated fields kept for backward compatibility if any
    private String requiredRam;
    private String requiredStorage;
    private String preferredBrand;
    private Integer maxDeliveryDays;
    private String paymentCardType;

    public DealLabCriteriaDTO() {}

    public DealLabCriteriaDTO(String category, Double maxBudget, Double minRating, Integer minWarrantyMonths, String marketplace) {
        this.category = category;
        this.maxBudget = maxBudget;
        this.minRating = minRating;
        this.minWarrantyMonths = minWarrantyMonths;
        this.marketplace = marketplace;
        this.preferredMarketplace = marketplace;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }

    public Double getMinRating() { return minRating; }
    public void setMinRating(Double minRating) { this.minRating = minRating; }

    public Integer getMinWarrantyMonths() { return minWarrantyMonths; }
    public void setMinWarrantyMonths(Integer minWarrantyMonths) { this.minWarrantyMonths = minWarrantyMonths; }

    public String getMarketplace() {
        return marketplace != null ? marketplace : preferredMarketplace;
    }
    public void setMarketplace(String marketplace) {
        this.marketplace = marketplace;
        this.preferredMarketplace = marketplace;
    }

    public String getPreferredMarketplace() {
        return marketplace != null ? marketplace : preferredMarketplace;
    }
    public void setPreferredMarketplace(String preferredMarketplace) {
        this.preferredMarketplace = preferredMarketplace;
        this.marketplace = preferredMarketplace;
    }

    public String getRequiredRam() { return requiredRam; }
    public void setRequiredRam(String requiredRam) { this.requiredRam = requiredRam; }

    public String getRequiredStorage() { return requiredStorage; }
    public void setRequiredStorage(String requiredStorage) { this.requiredStorage = requiredStorage; }

    public String getPreferredBrand() { return preferredBrand; }
    public void setPreferredBrand(String preferredBrand) { this.preferredBrand = preferredBrand; }

    public Integer getMaxDeliveryDays() { return maxDeliveryDays; }
    public void setMaxDeliveryDays(Integer maxDeliveryDays) { this.maxDeliveryDays = maxDeliveryDays; }

    public String getPaymentCardType() { return paymentCardType; }
    public void setPaymentCardType(String paymentCardType) { this.paymentCardType = paymentCardType; }
}
