package com.pricehub.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductDTO {
    private Long id;
    private String name;
    private String title;
    private String brand;
    private String modelNumber;
    private String category;
    private Double basePrice;
    private String imageUrl;
    private String description;
    private Double ratingAverage;
    private Double lowestEffectivePrice;
    private Double highestEffectivePrice;
    private Integer availableListingsCount;
    private String bestValueMarketplace;
    private LocalDateTime createdAt;
    private List<ProductSpecificationDTO> specifications = new ArrayList<>();
    private List<MarketplaceListingDTO> listings = new ArrayList<>();

    public ProductDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name != null ? name : title; }
    public void setName(String name) { this.name = name; }

    public String getTitle() { return title != null ? title : name; }
    public void setTitle(String title) { this.title = title; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModelNumber() { return modelNumber; }
    public void setModelNumber(String modelNumber) { this.modelNumber = modelNumber; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getRatingAverage() { return ratingAverage; }
    public void setRatingAverage(Double ratingAverage) { this.ratingAverage = ratingAverage; }

    public Double getLowestEffectivePrice() { return lowestEffectivePrice; }
    public void setLowestEffectivePrice(Double lowestEffectivePrice) { this.lowestEffectivePrice = lowestEffectivePrice; }

    public Double getHighestEffectivePrice() { return highestEffectivePrice; }
    public void setHighestEffectivePrice(Double highestEffectivePrice) { this.highestEffectivePrice = highestEffectivePrice; }

    public Integer getAvailableListingsCount() { return availableListingsCount; }
    public void setAvailableListingsCount(Integer availableListingsCount) { this.availableListingsCount = availableListingsCount; }

    public String getBestValueMarketplace() { return bestValueMarketplace; }
    public void setBestValueMarketplace(String bestValueMarketplace) { this.bestValueMarketplace = bestValueMarketplace; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ProductSpecificationDTO> getSpecifications() { return specifications; }
    public void setSpecifications(List<ProductSpecificationDTO> specifications) { this.specifications = specifications; }

    public List<MarketplaceListingDTO> getListings() { return listings; }
    public void setListings(List<MarketplaceListingDTO> listings) { this.listings = listings; }
}
