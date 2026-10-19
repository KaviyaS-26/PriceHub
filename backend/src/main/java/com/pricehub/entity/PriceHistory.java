package com.pricehub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_history")
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private MarketplaceListing marketplaceListing;

    @Column(name = "observed_price", nullable = false)
    private Double observedPrice;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "is_demo_data")
    private Boolean isDemoData = true;

    public PriceHistory() {}

    public PriceHistory(MarketplaceListing marketplaceListing, Double observedPrice, LocalDateTime recordedAt, Boolean isDemoData) {
        this.marketplaceListing = marketplaceListing;
        this.observedPrice = observedPrice;
        this.recordedAt = recordedAt != null ? recordedAt : LocalDateTime.now();
        this.isDemoData = isDemoData != null ? isDemoData : true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MarketplaceListing getMarketplaceListing() { return marketplaceListing; }
    public void setMarketplaceListing(MarketplaceListing marketplaceListing) { this.marketplaceListing = marketplaceListing; }

    public Double getObservedPrice() { return observedPrice; }
    public void setObservedPrice(Double observedPrice) { this.observedPrice = observedPrice; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public Boolean getIsDemoData() { return isDemoData; }
    public void setIsDemoData(Boolean demoData) { isDemoData = demoData; }
}
