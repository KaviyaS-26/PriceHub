package com.pricehub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_preferences")
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "preferred_category", length = 50)
    private String preferredCategory = "Smartphones";

    @Column(name = "min_budget")
    private Double minBudget = 10000.0;

    @Column(name = "max_budget")
    private Double maxBudget = 80000.0;

    @Column(name = "preferred_brand", length = 100)
    private String preferredBrand = "Any";

    @Column(name = "preferred_marketplace", length = 50)
    private String preferredMarketplace = "ALL";

    @Column(name = "max_delivery_days")
    private Integer maxDeliveryDays = 3;

    @Column(name = "default_sort", length = 50)
    private String defaultSort = "BEST_VALUE";

    public UserPreference() {}

    public UserPreference(User user) {
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPreferredCategory() { return preferredCategory; }
    public void setPreferredCategory(String preferredCategory) { this.preferredCategory = preferredCategory; }

    public Double getMinBudget() { return minBudget; }
    public void setMinBudget(Double minBudget) { this.minBudget = minBudget; }

    public Double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }

    public String getPreferredBrand() { return preferredBrand; }
    public void setPreferredBrand(String preferredBrand) { this.preferredBrand = preferredBrand; }

    public String getPreferredMarketplace() { return preferredMarketplace; }
    public void setPreferredMarketplace(String preferredMarketplace) { this.preferredMarketplace = preferredMarketplace; }

    public Integer getMaxDeliveryDays() { return maxDeliveryDays; }
    public void setMaxDeliveryDays(Integer maxDeliveryDays) { this.maxDeliveryDays = maxDeliveryDays; }

    public String getDefaultSort() { return defaultSort; }
    public void setDefaultSort(String defaultSort) { this.defaultSort = defaultSort; }
}
