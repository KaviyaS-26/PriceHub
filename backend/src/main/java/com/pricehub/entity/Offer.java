package com.pricehub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private MarketplaceListing marketplaceListing;

    @Column(name = "offer_type", nullable = false, length = 50)
    private String offerType; // PUBLIC, BANK_CARD, COUPON, EXCHANGE, MEMBERSHIP

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;

    @Column(name = "discount_percentage")
    private Double discountPercentage = 0.0;

    @Column(name = "min_purchase_amount")
    private Double minimumPurchaseAmount = 0.0;

    @Column(name = "bank_name", length = 100)
    private String bankName; // e.g. "HDFC", "ICICI", "SBI", "Axis", "All"

    @Column(name = "card_type", length = 50)
    private String cardType; // "CREDIT", "DEBIT", "ANY"

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    @Column(name = "terms_conditions", length = 255)
    private String termsConditions;

    @Column(name = "is_verified")
    private Boolean isVerified = true;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    public Offer() {}

    public Offer(MarketplaceListing marketplaceListing, String offerType, String title, String description, Double discountAmount, Double discountPercentage, Double minimumPurchaseAmount, String bankName, String cardType, String couponCode) {
        this.marketplaceListing = marketplaceListing;
        this.offerType = offerType;
        this.title = title;
        this.description = description;
        this.discountAmount = discountAmount != null ? discountAmount : 0.0;
        this.discountPercentage = discountPercentage != null ? discountPercentage : 0.0;
        this.minimumPurchaseAmount = minimumPurchaseAmount != null ? minimumPurchaseAmount : 0.0;
        this.bankName = bankName;
        this.cardType = cardType != null ? cardType : "ANY";
        this.couponCode = couponCode;
        this.isVerified = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MarketplaceListing getMarketplaceListing() { return marketplaceListing; }
    public void setMarketplaceListing(MarketplaceListing marketplaceListing) { this.marketplaceListing = marketplaceListing; }

    public String getOfferType() { return offerType; }
    public void setOfferType(String offerType) { this.offerType = offerType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }

    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }

    public Double getMinimumPurchaseAmount() { return minimumPurchaseAmount; }
    public void setMinimumPurchaseAmount(Double minimumPurchaseAmount) { this.minimumPurchaseAmount = minimumPurchaseAmount; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public String getTermsConditions() { return termsConditions; }
    public void setTermsConditions(String termsConditions) { this.termsConditions = termsConditions; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean verified) { isVerified = verified; }

    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
}
