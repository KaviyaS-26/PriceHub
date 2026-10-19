package com.pricehub.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MarketplaceListingDTO {
    private Long id;
    private Long productId;
    private String productTitle;
    private MarketplaceDTO marketplace;
    private String externalListingId;
    private String title;
    private Double listedPrice;
    private Double deliveryCharge;
    private Double mandatoryCharges;
    private Double rating;
    private Integer reviewCount;
    private Integer warrantyMonths;
    private Integer deliveryEstimateDays;
    private String stockStatus;
    private Boolean isVerified;
    private String productUrl;
    private String dataSource;
    private LocalDateTime lastUpdated;
    private List<OfferDTO> offers = new ArrayList<>();
    private CostBreakdownDTO costBreakdown;
    private Double valueScore;
    private List<String> badges = new ArrayList<>();
    private List<MarketplaceListingDTO> allMarketplaceListings = new ArrayList<>();

    public MarketplaceListingDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public MarketplaceDTO getMarketplace() { return marketplace; }
    public void setMarketplace(MarketplaceDTO marketplace) { this.marketplace = marketplace; }

    public String getExternalListingId() { return externalListingId; }
    public void setExternalListingId(String externalListingId) { this.externalListingId = externalListingId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Double getListedPrice() { return listedPrice; }
    public void setListedPrice(Double listedPrice) { this.listedPrice = listedPrice; }

    public Double getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(Double deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public Double getMandatoryCharges() { return mandatoryCharges; }
    public void setMandatoryCharges(Double mandatoryCharges) { this.mandatoryCharges = mandatoryCharges; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public Integer getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(Integer warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public Integer getDeliveryEstimateDays() { return deliveryEstimateDays; }
    public void setDeliveryEstimateDays(Integer deliveryEstimateDays) { this.deliveryEstimateDays = deliveryEstimateDays; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean verified) { isVerified = verified; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public List<OfferDTO> getOffers() { return offers; }
    public void setOffers(List<OfferDTO> offers) { this.offers = offers; }

    public CostBreakdownDTO getCostBreakdown() { return costBreakdown; }
    public void setCostBreakdown(CostBreakdownDTO costBreakdown) { this.costBreakdown = costBreakdown; }

    public Double getValueScore() { return valueScore; }
    public void setValueScore(Double valueScore) { this.valueScore = valueScore; }

    public List<String> getBadges() { return badges; }
    public void setBadges(List<String> badges) { this.badges = badges; }

    public List<MarketplaceListingDTO> getAllMarketplaceListings() { return allMarketplaceListings; }
    public void setAllMarketplaceListings(List<MarketplaceListingDTO> allMarketplaceListings) { this.allMarketplaceListings = allMarketplaceListings; }
}
