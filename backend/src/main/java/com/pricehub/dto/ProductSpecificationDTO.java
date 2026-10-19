package com.pricehub.dto;

public class ProductSpecificationDTO {
    private Long id;
    private String specKey;
    private String specValue;
    private String specGroup;

    public ProductSpecificationDTO() {}

    public ProductSpecificationDTO(Long id, String specKey, String specValue, String specGroup) {
        this.id = id;
        this.specKey = specKey;
        this.specValue = specValue;
        this.specGroup = specGroup;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSpecKey() { return specKey; }
    public void setSpecKey(String specKey) { this.specKey = specKey; }

    public String getSpecValue() { return specValue; }
    public void setSpecValue(String specValue) { this.specValue = specValue; }

    public String getSpecGroup() { return specGroup; }
    public void setSpecGroup(String specGroup) { this.specGroup = specGroup; }
}
