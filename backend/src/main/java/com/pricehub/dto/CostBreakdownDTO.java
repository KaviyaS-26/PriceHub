package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;

public class CostBreakdownDTO {
    private Double listedPrice;
    private Double deliveryCharge;
    private Double mandatoryCharges;
    private Double grossPrice;
    private Double totalDiscountAmount;
    private Double effectiveCost;
    private Double savingsPercentage;
    private List<OfferDTO> appliedOffers = new ArrayList<>();
    private String calculationExplanation;
    private Boolean isDemoData;
    private String priceFreshness = "Verified 10m ago";

    public CostBreakdownDTO() {}

    public Double getListedPrice() { return listedPrice; }
    public void setListedPrice(Double listedPrice) { this.listedPrice = listedPrice; }

    public Double getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(Double deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public Double getMandatoryCharges() { return mandatoryCharges; }
    public void setMandatoryCharges(Double mandatoryCharges) { this.mandatoryCharges = mandatoryCharges; }

    public Double getGrossPrice() { return grossPrice; }
    public void setGrossPrice(Double grossPrice) { this.grossPrice = grossPrice; }

    public Double getTotalDiscountAmount() { return totalDiscountAmount; }
    public void setTotalDiscountAmount(Double totalDiscountAmount) { this.totalDiscountAmount = totalDiscountAmount; }

    public Double getEffectiveCost() { return effectiveCost; }
    public void setEffectiveCost(Double effectiveCost) { this.effectiveCost = effectiveCost; }

    public Double getSavingsPercentage() { return savingsPercentage; }
    public void setSavingsPercentage(Double savingsPercentage) { this.savingsPercentage = savingsPercentage; }

    public List<OfferDTO> getAppliedOffers() { return appliedOffers; }
    public void setAppliedOffers(List<OfferDTO> appliedOffers) { this.appliedOffers = appliedOffers; }

    public String getCalculationExplanation() { return calculationExplanation; }
    public void setCalculationExplanation(String calculationExplanation) { this.calculationExplanation = calculationExplanation; }

    public Boolean getIsDemoData() { return isDemoData; }
    public void setIsDemoData(Boolean demoData) { isDemoData = demoData; }

    public String getPriceFreshness() { return priceFreshness; }
    public void setPriceFreshness(String priceFreshness) { this.priceFreshness = priceFreshness; }
}
