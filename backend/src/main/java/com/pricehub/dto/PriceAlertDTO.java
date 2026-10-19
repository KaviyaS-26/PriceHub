package com.pricehub.dto;

import java.time.LocalDateTime;

public class PriceAlertDTO {
    private Long id;
    private Long productId;
    private Long listingId;
    private String productTitle;
    private String productImageUrl;
    private String marketplace;
    private String marketplaceName;
    private Double targetPrice;
    private Double currentPrice;
    private Double priceAtCreation;
    private String status;
    private Boolean isTriggered;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime triggeredAt;

    public PriceAlertDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public String getMarketplaceName() { return marketplaceName; }
    public void setMarketplaceName(String marketplaceName) { this.marketplaceName = marketplaceName; }
    public String getMarketplace() { return marketplace; }
    public void setMarketplace(String marketplace) { this.marketplace = marketplace; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(Double targetPrice) { this.targetPrice = targetPrice; }

    public Double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(Double currentPrice) { this.currentPrice = currentPrice; }

    public Double getPriceAtCreation() { return priceAtCreation; }
    public void setPriceAtCreation(Double priceAtCreation) { this.priceAtCreation = priceAtCreation; }

    public Boolean getIsTriggered() { return isTriggered; }
    public void setIsTriggered(Boolean triggered) { isTriggered = triggered; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }
}
