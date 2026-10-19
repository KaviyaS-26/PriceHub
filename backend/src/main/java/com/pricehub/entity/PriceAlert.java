package com.pricehub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_alerts")
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "listing_id", nullable = true)
    private MarketplaceListing marketplaceListing;

    @Column(name = "marketplace")
    private String marketplace;

    @Column(name = "target_price", nullable = false)
    private Double targetPrice;

    @Column(name = "current_price_at_creation", nullable = false)
    private Double currentPriceAtCreation;

    @Column(name = "is_triggered", nullable = false)
    private Boolean isTriggered = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "triggered_at")
    private LocalDateTime triggeredAt;

    public PriceAlert() {}

    public PriceAlert(User user, MarketplaceListing marketplaceListing, Double targetPrice, Double currentPriceAtCreation) {
        this.user = user;
        this.marketplaceListing = marketplaceListing;
        this.targetPrice = targetPrice;
        this.currentPriceAtCreation = currentPriceAtCreation;
        this.isTriggered = false;
        this.isActive = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public MarketplaceListing getMarketplaceListing() { return marketplaceListing; }
    public void setMarketplaceListing(MarketplaceListing marketplaceListing) { this.marketplaceListing = marketplaceListing; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getMarketplace() { return marketplace; }
    public void setMarketplace(String marketplace) { this.marketplace = marketplace; }

    public Double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(Double targetPrice) { this.targetPrice = targetPrice; }

    public Double getCurrentPriceAtCreation() { return currentPriceAtCreation; }
    public void setCurrentPriceAtCreation(Double currentPriceAtCreation) { this.currentPriceAtCreation = currentPriceAtCreation; }

    public Boolean getIsTriggered() { return isTriggered; }
    public void setIsTriggered(Boolean triggered) { isTriggered = triggered; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(LocalDateTime triggeredAt) { this.triggeredAt = triggeredAt; }
}
