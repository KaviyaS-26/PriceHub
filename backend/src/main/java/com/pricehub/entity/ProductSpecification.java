package com.pricehub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_specifications")
public class ProductSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "spec_key", nullable = false, length = 100)
    private String specKey; // e.g. "ram", "storage", "processor", "screen_size", "battery", "warranty_period"

    @Column(name = "spec_value", nullable = false, length = 255)
    private String specValue; // e.g. "16 GB", "512 GB SSD", "Intel Core i7-13700H"

    @Column(name = "spec_group", length = 100)
    private String specGroup; // "Performance", "Display", "Battery & Power", "General"

    public ProductSpecification() {}

    public ProductSpecification(Product product, String specKey, String specValue, String specGroup) {
        this.product = product;
        this.specKey = specKey;
        this.specValue = specValue;
        this.specGroup = specGroup;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getSpecKey() { return specKey; }
    public void setSpecKey(String specKey) { this.specKey = specKey; }

    public String getSpecValue() { return specValue; }
    public void setSpecValue(String specValue) { this.specValue = specValue; }

    public String getSpecGroup() { return specGroup; }
    public void setSpecGroup(String specGroup) { this.specGroup = specGroup; }
}
