package com.pricehub.dto;

import java.util.ArrayList;
import java.util.List;

public class DealItemDTO {
    private Long id;
    private Long productId;
    private String productTitle;
    private String category;
    private String marketplace;
    private String marketplaceName;
    private Double price;
    private Double rating;
    private Integer warrantyMonths;
    private String warrantyText;
    private Double valueScore;
    private String imageUrl;
    private String productUrl;
    private Boolean inStock;
    private String priceDate;
    private List<String> failureReasons = new ArrayList<>();

    public DealItemDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMarketplace() { return marketplace; }
    public void setMarketplace(String marketplace) { this.marketplace = marketplace; }

    public String getMarketplaceName() { return marketplaceName; }
    public void setMarketplaceName(String marketplaceName) { this.marketplaceName = marketplaceName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(Integer warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public String getWarrantyText() { return warrantyText; }
    public void setWarrantyText(String warrantyText) { this.warrantyText = warrantyText; }

    public Double getValueScore() { return valueScore; }
    public void setValueScore(Double valueScore) { this.valueScore = valueScore; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public Boolean getInStock() { return inStock; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }

    public String getPriceDate() { return priceDate; }
    public void setPriceDate(String priceDate) { this.priceDate = priceDate; }

    public List<String> getFailureReasons() { return failureReasons; }
    public void setFailureReasons(List<String> failureReasons) { this.failureReasons = failureReasons; }
}
