package com.pricehub.dto;

public class MarketplaceDTO {
    private Long id;
    private String name;
    private String code;
    private String baseUrl;
    private String logoUrl;
    private Double trustScore;

    public MarketplaceDTO() {}

    public MarketplaceDTO(Long id, String name, String code, String baseUrl, String logoUrl, Double trustScore) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.baseUrl = baseUrl;
        this.logoUrl = logoUrl;
        this.trustScore = trustScore;
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
}
