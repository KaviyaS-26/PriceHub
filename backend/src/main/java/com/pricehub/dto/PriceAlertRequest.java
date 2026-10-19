package com.pricehub.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PriceAlertRequest {

    private Long listingId;
    private Long productId;
    private String marketplace;

    @NotNull(message = "Target price is required")
    @Positive(message = "Target price must be positive")
    private Double targetPrice;

    public PriceAlertRequest() {}

    public PriceAlertRequest(Long listingId, Double targetPrice) {
        this.listingId = listingId;
        this.targetPrice = targetPrice;
    }

    public PriceAlertRequest(Long productId, String marketplace, Double targetPrice) {
        this.productId = productId;
        this.marketplace = marketplace;
        this.targetPrice = targetPrice;
    }

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getMarketplace() { return marketplace; }
    public void setMarketplace(String marketplace) { this.marketplace = marketplace; }

    public Double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(Double targetPrice) { this.targetPrice = targetPrice; }
}
