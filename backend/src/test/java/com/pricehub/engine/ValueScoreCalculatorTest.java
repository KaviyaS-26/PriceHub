package com.pricehub.engine;

import com.pricehub.entity.Marketplace;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueScoreCalculatorTest {

    private ValueScoreCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ValueScoreCalculator();
    }

    @Test
    @DisplayName("Should generate higher value score for cheaper price with fast delivery and high rating")
    void testValueScoreCalculation() {
        Product p = new Product("MacBook Air M2", "Apple", "M2", "Laptops", 99990.0, "", "");
        Marketplace m = new Marketplace("Amazon India", "AMAZON", "", "", 4.9);
        MarketplaceListing listing = new MarketplaceListing(p, m, p.getTitle(), 90000.0, 0.0, 0.0, 4.9, 1000, 12, 1, "IN_STOCK", "", "DEMO_DATA");

        double score = calculator.calculateValueScore(listing, 85000.0, 80000.0, 120000.0, "BEST_VALUE");

        assertTrue(score >= 70.0 && score <= 100.0);
    }
}
