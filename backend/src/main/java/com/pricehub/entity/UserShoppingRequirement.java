package com.pricehub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_shopping_requirements")
public class UserShoppingRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "max_budget")
    private Double maxBudget;

    @Column(name = "required_ram", length = 50)
    private String requiredRam;

    @Column(name = "required_storage", length = 50)
    private String requiredStorage;

    @Column(name = "preferred_brand", length = 100)
    private String preferredBrand;

    @Column(name = "min_rating")
    private Double minRating;

    @Column(name = "min_warranty_months")
    private Integer minWarrantyMonths;

    @Column(name = "max_delivery_days")
    private Integer maxDeliveryDays;

    @Column(name = "preferred_marketplace", length = 50)
    private String preferredMarketplace;

    @Column(name = "payment_card_type", length = 50)
    private String paymentCardType; // HDFC_CREDIT, ICICI_CREDIT, SBI_DEBIT, ANY

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public UserShoppingRequirement() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }

    public String getRequiredRam() { return requiredRam; }
    public void setRequiredRam(String requiredRam) { this.requiredRam = requiredRam; }

    public String getRequiredStorage() { return requiredStorage; }
    public void setRequiredStorage(String requiredStorage) { this.requiredStorage = requiredStorage; }

    public String getPreferredBrand() { return preferredBrand; }
    public void setPreferredBrand(String preferredBrand) { this.preferredBrand = preferredBrand; }

    public Double getMinRating() { return minRating; }
    public void setMinRating(Double minRating) { this.minRating = minRating; }

    public Integer getMinWarrantyMonths() { return minWarrantyMonths; }
    public void setMinWarrantyMonths(Integer minWarrantyMonths) { this.minWarrantyMonths = minWarrantyMonths; }

    public Integer getMaxDeliveryDays() { return maxDeliveryDays; }
    public void setMaxDeliveryDays(Integer maxDeliveryDays) { this.maxDeliveryDays = maxDeliveryDays; }

    public String getPreferredMarketplace() { return preferredMarketplace; }
    public void setPreferredMarketplace(String preferredMarketplace) { this.preferredMarketplace = preferredMarketplace; }

    public String getPaymentCardType() { return paymentCardType; }
    public void setPaymentCardType(String paymentCardType) { this.paymentCardType = paymentCardType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
