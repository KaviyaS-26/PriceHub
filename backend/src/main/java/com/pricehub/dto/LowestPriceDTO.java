package com.pricehub.dto;

public class LowestPriceDTO {

    private String marketplace;
    private Double price;

    public LowestPriceDTO() {}

    public LowestPriceDTO(String marketplace, Double price) {
        this.marketplace = marketplace;
        this.price = price;
    }

    public String getMarketplace() { return marketplace; }
    public void setMarketplace(String marketplace) { this.marketplace = marketplace; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
