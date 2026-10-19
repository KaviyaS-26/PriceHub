package com.pricehub.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComparisonHistoryDTO {
    private Long id;
    private String productIds;
    private String summaryTitle;
    private LocalDateTime comparedAt;
    private List<ProductDTO> products = new ArrayList<>();

    public ComparisonHistoryDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductIds() { return productIds; }
    public void setProductIds(String productIds) { this.productIds = productIds; }

    public String getSummaryTitle() { return summaryTitle; }
    public void setSummaryTitle(String summaryTitle) { this.summaryTitle = summaryTitle; }

    public LocalDateTime getComparedAt() { return comparedAt; }
    public void setComparedAt(LocalDateTime comparedAt) { this.comparedAt = comparedAt; }

    public List<ProductDTO> getProducts() { return products; }
    public void setProducts(List<ProductDTO> products) { this.products = products; }
}
