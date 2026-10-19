package com.pricehub.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCompareResponse {

    private Long productId;
    private String productName;
    private String category;
    private String imageUrl;

    private List<CompareListingDTO> listings = new ArrayList<>();
    private BestMarketplaceDTO best;
    private LowestPriceDTO lowestPrice;
    private Double extraCost;
    private String explanation;
    private Map<String, Object> weightsUsed = new HashMap<>();
    private Boolean allOutOfStock = false;

    public ProductCompareResponse() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<CompareListingDTO> getListings() { return listings; }
    public void setListings(List<CompareListingDTO> listings) { this.listings = listings; }

    public BestMarketplaceDTO getBest() { return best; }
    public void setBest(BestMarketplaceDTO best) { this.best = best; }

    public LowestPriceDTO getLowestPrice() { return lowestPrice; }
    public void setLowestPrice(LowestPriceDTO lowestPrice) { this.lowestPrice = lowestPrice; }

    public Double getExtraCost() { return extraCost; }
    public void setExtraCost(Double extraCost) { this.extraCost = extraCost; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Map<String, Object> getWeightsUsed() { return weightsUsed; }
    public void setWeightsUsed(Map<String, Object> weightsUsed) { this.weightsUsed = weightsUsed; }

    public Boolean getAllOutOfStock() { return allOutOfStock; }
    public void setAllOutOfStock(Boolean allOutOfStock) { this.allOutOfStock = allOutOfStock; }
}
