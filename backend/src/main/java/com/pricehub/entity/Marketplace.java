package com.pricehub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "marketplaces")
public class Marketplace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // AMAZON, FLIPKART, RELIANCE_DIGITAL

    @Column(name = "base_url", length = 255)
    private String baseUrl;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "trust_score")
    private Double trustScore = 4.8;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public Marketplace() {}

    public Marketplace(String name, String code, String baseUrl, String logoUrl, Double trustScore) {
        this.name = name;
        this.code = code;
        this.baseUrl = baseUrl;
        this.logoUrl = logoUrl;
        this.trustScore = trustScore;
        this.isActive = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public Double getTrustScore() { return trustScore; }
    public void setTrustScore(Double trustScore) { this.trustScore = trustScore; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
