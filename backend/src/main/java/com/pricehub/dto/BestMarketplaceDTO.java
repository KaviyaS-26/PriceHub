package com.pricehub.dto;

public class BestMarketplaceDTO {

    private String marketplace;
    private String reason;
    private Double valueScore;
    private Double price;

    public BestMarketplaceDTO() {}

    public BestMarketplaceDTO(String marketplace, String reason, Double valueScore, Double price) {
        this.marketplace = marketplace;
        this.reason = reason;
        this.valueScore = valueScore;
        this.price = price;
    }

    public String getMarketplace() { return marketplace; }
    public void setMarketplace(String marketplace) { this.marketplace = marketplace; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Double getValueScore() { return valueScore; }
    public void setValueScore(Double valueScore) { this.valueScore = valueScore; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
