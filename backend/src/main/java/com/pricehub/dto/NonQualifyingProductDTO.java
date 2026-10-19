package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;

public class NonQualifyingProductDTO {
    private ProductDTO product;
    private Double currentEffectiveCost;
    private List<String> failureReasons = new ArrayList<>();
    private String primaryLimitingFactor; // e.g. "Exceeds budget by ₹4,999", "Only 8GB RAM"
    private String howToQualify; // "Increase budget to ₹64,999"

    public NonQualifyingProductDTO() {}

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public Double getCurrentEffectiveCost() { return currentEffectiveCost; }
    public void setCurrentEffectiveCost(Double currentEffectiveCost) { this.currentEffectiveCost = currentEffectiveCost; }

    public List<String> getFailureReasons() { return failureReasons; }
    public void setFailureReasons(List<String> failureReasons) { this.failureReasons = failureReasons; }

    public String getPrimaryLimitingFactor() { return primaryLimitingFactor; }
    public void setPrimaryLimitingFactor(String primaryLimitingFactor) { this.primaryLimitingFactor = primaryLimitingFactor; }

    public String getHowToQualify() { return howToQualify; }
    public void setHowToQualify(String howToQualify) { this.howToQualify = howToQualify; }
}
