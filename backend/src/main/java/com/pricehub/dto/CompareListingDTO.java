package com.pricehub.dto;

import java.util.HashMap;
import java.util.Map;

public class CompareListingDTO {

    private String marketplace;
    private String marketplaceName;
    private Double price;
    private String warrantyText;
    private Integer warrantyMonths;
    private Double rating;
    private Boolean inStock;
    private String productUrl;
    private String imageUrl;
    private String priceDate;
    private String dataSource = "COLLECTED";

    private Double priceScore;
    private Double ratingScore;
    private Double warrantyScore;
    private Map<String, Double> perFactorScores = new HashMap<>();

    private Double valueScore;
    private Integer rank;
    private Boolean excludedFromRanking = false;

    public CompareListingDTO() {}

    public String getMarketplace() { return marketplace; }
    public void setMarketplace(String marketplace) { this.marketplace = marketplace; }

    public String getMarketplaceName() { return marketplaceName; }
    public void setMarketplaceName(String marketplaceName) { this.marketplaceName = marketplaceName; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getWarrantyText() { return warrantyText; }
    public void setWarrantyText(String warrantyText) { this.warrantyText = warrantyText; }

    public Integer getWarrantyMonths() { return warrantyMonths; }
    public void setWarrantyMonths(Integer warrantyMonths) { this.warrantyMonths = warrantyMonths; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Boolean getInStock() { return inStock; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPriceDate() { return priceDate; }
    public void setPriceDate(String priceDate) { this.priceDate = priceDate; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public Double getPriceScore() { return priceScore; }
    public void setPriceScore(Double priceScore) {
        this.priceScore = priceScore;
        if (priceScore != null) perFactorScores.put("price", priceScore);
    }

    public Double getRatingScore() { return ratingScore; }
    public void setRatingScore(Double ratingScore) {
        this.ratingScore = ratingScore;
        if (ratingScore != null) perFactorScores.put("rating", ratingScore);
    }

    public Double getWarrantyScore() { return warrantyScore; }
    public void setWarrantyScore(Double warrantyScore) {
        this.warrantyScore = warrantyScore;
        if (warrantyScore != null) perFactorScores.put("warranty", warrantyScore);
    }

    public Map<String, Double> getPerFactorScores() { return perFactorScores; }
    public void setPerFactorScores(Map<String, Double> perFactorScores) { this.perFactorScores = perFactorScores; }

    public Double getValueScore() { return valueScore; }
    public void setValueScore(Double valueScore) { this.valueScore = valueScore; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public Boolean getExcludedFromRanking() { return excludedFromRanking; }
    public void setExcludedFromRanking(Boolean excludedFromRanking) { this.excludedFromRanking = excludedFromRanking; }
}
