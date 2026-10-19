package com.pricehub.dto;

import java.time.LocalDateTime;

public class SavedProductDTO {
    private Long id;
    private ProductDTO product;
    private String notes;
    private LocalDateTime savedAt;

    public SavedProductDTO() {}

    public SavedProductDTO(Long id, ProductDTO product, String notes, LocalDateTime savedAt) {
        this.id = id;
        this.product = product;
        this.notes = notes;
        this.savedAt = savedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }
}
