package com.pricehub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, length = 255)
    private String name;

    @Column(length = 255)
    private String title;

    @Column(length = 100)
    private String brand;

    @Column(name = "model_number", length = 100)
    private String modelNumber;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "base_price")
    private Double basePrice;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "rating_average")
    private Double ratingAverage = 4.5;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductSpecification> specifications = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MarketplaceListing> listings = new ArrayList<>();

    public Product() {}

    public Product(String name, String category) {
        this.name = name;
        this.title = name;
        this.category = category;
    }

    public Product(String title, String brand, String modelNumber, String category, Double basePrice, String imageUrl, String description) {
        this.name = title;
        this.title = title;
        this.brand = brand;
        this.modelNumber = modelNumber;
        this.category = category;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        if (this.name == null && this.title != null) {
            this.name = this.title;
        }
        if (this.title == null && this.name != null) {
            this.title = this.name;
        }
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() {
        return name != null ? name : title;
    }

    public void setName(String name) {
        this.name = name;
        if (this.title == null) {
            this.title = name;
        }
    }

    public String getTitle() {
        return title != null ? title : name;
    }

    public void setTitle(String title) {
        this.title = title;
        if (this.name == null) {
            this.name = title;
        }
    }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ProductSpecification> getSpecifications() { return specifications; }
    public void setSpecifications(List<ProductSpecification> specifications) { this.specifications = specifications; }

    public List<MarketplaceListing> getListings() { return listings; }
    public void setListings(List<MarketplaceListing> listings) { this.listings = listings; }
}
