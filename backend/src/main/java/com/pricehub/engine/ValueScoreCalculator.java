package com.pricehub.engine;

import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Product;
import com.pricehub.entity.ProductSpecification;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Section 10: Best-Value Recommendation Engine Score Calculator.
 * Multi-criteria weighted scoring algorithm normalizing price, rating, specs, warranty, delivery.
 */
@Component
public class ValueScoreCalculator {

    public double calculateValueScore(MarketplaceListing listing, double effectiveCost, double minCategoryPrice, double maxCategoryPrice, String userPriority) {
        if (listing == null) return 50.0;

        // Weights setup based on user priority
        double wPrice = 0.35;
        double wRating = 0.25;
        double wSpecs = 0.20;
        double wWarranty = 0.10;
        double wDelivery = 0.10;

        if ("LOWEST_PRICE".equalsIgnoreCase(userPriority)) {
            wPrice = 0.60; wRating = 0.15; wSpecs = 0.10; wWarranty = 0.08; wDelivery = 0.07;
        } else if ("HIGHER_RATING".equalsIgnoreCase(userPriority)) {
            wPrice = 0.15; wRating = 0.50; wSpecs = 0.15; wWarranty = 0.10; wDelivery = 0.10;
        } else if ("BETTER_SPECS".equalsIgnoreCase(userPriority)) {
            wPrice = 0.15; wRating = 0.15; wSpecs = 0.50; wWarranty = 0.10; wDelivery = 0.10;
        } else if ("LONGER_WARRANTY".equalsIgnoreCase(userPriority)) {
            wPrice = 0.15; wRating = 0.15; wSpecs = 0.10; wWarranty = 0.50; wDelivery = 0.10;
        } else if ("FASTER_DELIVERY".equalsIgnoreCase(userPriority)) {
            wPrice = 0.15; wRating = 0.15; wSpecs = 0.10; wWarranty = 0.10; wDelivery = 0.50;
        }

        // 1. Normalized Price Score (100 is cheapest, 0 is most expensive)
        double priceScore = 50.0;
        if (maxCategoryPrice > minCategoryPrice && effectiveCost > 0) {
            double normalized = 1.0 - ((effectiveCost - minCategoryPrice) / (maxCategoryPrice - minCategoryPrice));
            priceScore = Math.max(0.0, Math.min(1.0, normalized)) * 100.0;
        } else {
            priceScore = 75.0;
        }

        // 2. Rating Score (0 to 100)
        double rating = listing.getRating() != null ? listing.getRating() : 4.0;
        double ratingScore = (rating / 5.0) * 100.0;

        // 3. Spec Score (0 to 100)
        double specScore = computeSpecScore(listing.getProduct());

        // 4. Warranty Score (0 to 100)
        int warrantyMonths = listing.getWarrantyMonths() != null ? listing.getWarrantyMonths() : 12;
        double warrantyScore = Math.min(100.0, (warrantyMonths / 24.0) * 100.0);

        // 5. Delivery Score (0 to 100 - faster is higher)
        int deliveryDays = listing.getDeliveryEstimateDays() != null ? listing.getDeliveryEstimateDays() : 3;
        double deliveryScore = Math.max(20.0, 100.0 - ((deliveryDays - 1) * 20.0));

        // Combined Weighted Score
        double totalScore = (priceScore * wPrice) +
                            (ratingScore * wRating) +
                            (specScore * wSpecs) +
                            (warrantyScore * wWarranty) +
                            (deliveryScore * wDelivery);

        return Math.round(Math.max(10.0, Math.min(100.0, totalScore)) * 10.0) / 10.0;
    }

    private double computeSpecScore(Product product) {
        if (product == null || product.getSpecifications() == null || product.getSpecifications().isEmpty()) {
            return 60.0;
        }

        double score = 50.0;
        for (ProductSpecification spec : product.getSpecifications()) {
            String key = spec.getSpecKey() != null ? spec.getSpecKey().toLowerCase() : "";
            String val = spec.getSpecValue() != null ? spec.getSpecValue().toUpperCase() : "";

            if (key.contains("ram")) {
                if (val.contains("32")) score += 20;
                else if (val.contains("16")) score += 15;
                else if (val.contains("8") || val.contains("12")) score += 10;
            } else if (key.contains("storage")) {
                if (val.contains("1 TB") || val.contains("1TB")) score += 20;
                else if (val.contains("512")) score += 15;
                else if (val.contains("256")) score += 10;
            } else if (key.contains("processor")) {
                if (val.contains("I7") || val.contains("I9") || val.contains("RYZEN 7") || val.contains("M2") || val.contains("M3") || val.contains("GEN 3")) score += 15;
                else score += 10;
            }
        }

        return Math.min(100.0, score);
    }
}
