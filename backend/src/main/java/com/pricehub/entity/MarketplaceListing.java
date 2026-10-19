package com.pricehub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "marketplace_listings")
public class MarketplaceListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marketplace_id", nullable = false)
    private Marketplace marketplace;

    @Column(name = "external_listing_id", length = 100)
    private String externalListingId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "price")
    private Double price;

    @Column(name = "listed_price", nullable = false)
    private Double listedPrice = 0.0;

    @Column(name = "delivery_charge", nullable = false)
    private Double deliveryCharge = 0.0;

    @Column(name = "mandatory_charges", nullable = false)
    private Double mandatoryCharges = 0.0;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "in_stock")
    private Boolean inStock = true;

    @Column(name = "warranty_text", length = 100)
    private String warrantyText;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    @Column(name = "delivery_estimate_days")
    private Integer deliveryEstimateDays = 2;

    @Column(name = "stock_status", length = 50)
    private String stockStatus = "IN_STOCK"; // IN_STOCK, OUT_OF_STOCK

    @Column(name = "is_verified")
    private Boolean isVerified = true;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "product_url", length = 2000)
    private String productUrl;

    @Column(name = "price_date", length = 50)
    private String priceDate;

    @Column(name = "data_source", length = 50)
    private String dataSource = "COLLECTED";

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "marketplaceListing", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Offer> offers = new ArrayList<>();

    @OneToMany(mappedBy = "marketplaceListing", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PriceHistory> priceHistories = new ArrayList<>();

    public MarketplaceListing() {
        this.lastUpdated = LocalDateTime.now();
    }

    public MarketplaceListing(Product product, Marketplace marketplace, String title, Double listedPrice,
                              Double deliveryCharge, Double mandatoryCharges, Double rating,
                              Integer reviewCount, Integer warrantyMonths, Integer deliveryEstimateDays,
                              String stockStatus, String productUrl, String dataSource) {
        this.product = product;
        this.marketplace = marketplace;
        this.title = title;
        this.price = listedPrice;
        this.listedPrice = listedPrice != null ? listedPrice : 0.0;
        this.deliveryCharge = deliveryCharge != null ? deliveryCharge : 0.0;
        this.mandatoryCharges = mandatoryCharges != null ? mandatoryCharges : 0.0;
        this.rating = rating;
        this.reviewCount = reviewCount != null ? reviewCount : 0;
        this.warrantyMonths = warrantyMonths;
        this.deliveryEstimateDays = deliveryEstimateDays != null ? deliveryEstimateDays : 2;
        this.stockStatus = stockStatus != null ? stockStatus : "IN_STOCK";
        this.inStock = !"OUT_OF_STOCK".equalsIgnoreCase(stockStatus);
        this.isVerified = true;
        this.productUrl = productUrl;
        this.dataSource = dataSource != null ? dataSource : "COLLECTED";
        this.lastUpdated = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (this.price == null && this.listedPrice != null) {
            this.price = this.listedPrice;
        } else if (this.listedPrice == null && this.price != null) {
            this.listedPrice = this.price;
        }
        if (this.inStock != null) {
            this.stockStatus = this.inStock ? "IN_STOCK" : "OUT_OF_STOCK";
        }
        this.lastUpdated = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Marketplace getMarketplace() { return marketplace; }
    public void setMarketplace(Marketplace marketplace) { this.marketplace = marketplace; }

    public String getExternalListingId() { return externalListingId; }
    public void setExternalListingId(String externalListingId) { this.externalListingId = externalListingId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getPrice() {
        return price != null ? price : listedPrice;
    }

    public void setPrice(Double price) {
        this.price = price;
        this.listedPrice = price != null ? price : 0.0;
    }

    public Double getListedPrice() {
        return getPrice();
    }

    public void setListedPrice(Double listedPrice) {
        setPrice(listedPrice);
    }

    public Double getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(Double deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public Double getMandatoryCharges() { return mandatoryCharges; }
    public void setMandatoryCharges(Double mandatoryCharges) { this.mandatoryCharges = mandatoryCharges; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public Boolean getInStock() {
        return inStock != null ? inStock : !"OUT_OF_STOCK".equalsIgnoreCase(stockStatus);
    }

    public boolean isInStock() {
        return Boolean.TRUE.equals(getInStock());
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
        this.stockStatus = Boolean.TRUE.equals(inStock) ? "IN_STOCK" : "OUT_OF_STOCK";
    }

    public String getWarrantyText() { return warrantyText; }
    public void setWarrantyText(String warrantyText) { this.warrantyText = warrantyText; }

    public Integer getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(Integer warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public Integer getDeliveryEstimateDays() { return deliveryEstimateDays; }
    public void setDeliveryEstimateDays(Integer deliveryEstimateDays) { this.deliveryEstimateDays = deliveryEstimateDays; }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
        this.inStock = !"OUT_OF_STOCK".equalsIgnoreCase(stockStatus);
    }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean verified) { isVerified = verified; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public String getPriceDate() { return priceDate; }
    public void setPriceDate(String priceDate) { this.priceDate = priceDate; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public List<Offer> getOffers() { return offers; }
    public void setOffers(List<Offer> offers) { this.offers = offers; }

    public List<PriceHistory> getPriceHistories() { return priceHistories; }
    public void setPriceHistories(List<PriceHistory> priceHistories) { this.priceHistories = priceHistories; }
}
