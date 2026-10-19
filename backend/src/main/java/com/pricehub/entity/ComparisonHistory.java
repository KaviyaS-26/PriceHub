package com.pricehub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comparison_history")
public class ComparisonHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "product_ids", nullable = false, length = 255)
    private String productIds; // comma-separated product IDs e.g. "1,2,3"

    @Column(name = "summary_title", length = 255)
    private String summaryTitle;

    @Column(name = "compared_at", nullable = false, updatable = false)
    private LocalDateTime comparedAt;

    public ComparisonHistory() {}

    public ComparisonHistory(User user, String productIds, String summaryTitle) {
        this.user = user;
        this.productIds = productIds;
        this.summaryTitle = summaryTitle;
    }

    @PrePersist
    protected void onCreate() {
        this.comparedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getProductIds() { return productIds; }
    public void setProductIds(String productIds) { this.productIds = productIds; }

    public String getSummaryTitle() { return summaryTitle; }
    public void setSummaryTitle(String summaryTitle) { this.summaryTitle = summaryTitle; }

    public LocalDateTime getComparedAt() { return comparedAt; }
    public void setComparedAt(LocalDateTime comparedAt) { this.comparedAt = comparedAt; }
}
