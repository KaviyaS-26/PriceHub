package com.pricehub.dto;

import java.time.LocalDateTime;

public class PriceHistoryDTO {
    private Long id;
    private Long listingId;
    private String marketplaceName;
    private String marketplaceCode;
    private Double observedPrice;
    private LocalDateTime recordedAt;
    private Boolean isDemoData;

    public PriceHistoryDTO() {}

    public PriceHistoryDTO(Long id, Long listingId, String marketplaceName, String marketplaceCode, Double observedPrice, LocalDateTime recordedAt, Boolean isDemoData) {
        this.id = id;
        this.listingId = listingId;
        this.marketplaceName = marketplaceName;
        this.marketplaceCode = marketplaceCode;
        this.observedPrice = observedPrice;
        this.recordedAt = recordedAt;
        this.isDemoData = isDemoData;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public String getMarketplaceName() { return marketplaceName; }
    public void setMarketplaceName(String marketplaceName) { this.marketplaceName = marketplaceName; }

    public String getMarketplaceCode() { return marketplaceCode; }
    public void setMarketplaceCode(String marketplaceCode) { this.marketplaceCode = marketplaceCode; }

    public Double getObservedPrice() { return observedPrice; }
    public void setObservedPrice(Double observedPrice) { this.observedPrice = observedPrice; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public Boolean getIsDemoData() { return isDemoData; }
    public void setIsDemoData(Boolean demoData) { isDemoData = demoData; }
}
