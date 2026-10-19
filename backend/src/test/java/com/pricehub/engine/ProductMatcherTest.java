package com.pricehub.engine;

import com.pricehub.dto.MatchConfidence;
import com.pricehub.dto.MatchResultDTO;
import com.pricehub.entity.Product;
import com.pricehub.entity.ProductSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductMatcherTest {

    private ProductMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new ProductMatcher();
    }

    @Test
    @DisplayName("Should detect Exact Match for identical product brand, model, and specs")
    void testExactMatchIdenticalProduct() {
        Product p1 = new Product("Apple MacBook Air M2 16GB 512GB", "Apple", "MC7X4HN/A", "Laptops", 99990.0, "", "");
        Product p2 = new Product("Apple MacBook Air M2 16GB 512GB", "Apple", "MC7X4HN/A", "Laptops", 96990.0, "", "");

        List<ProductSpecification> specs1 = new ArrayList<>();
        specs1.add(new ProductSpecification(p1, "ram", "16 GB", "Performance"));
        specs1.add(new ProductSpecification(p1, "storage", "512 GB", "Performance"));
        p1.setSpecifications(specs1);

        List<ProductSpecification> specs2 = new ArrayList<>();
        specs2.add(new ProductSpecification(p2, "ram", "16 GB", "Performance"));
        specs2.add(new ProductSpecification(p2, "storage", "512 GB", "Performance"));
        p2.setSpecifications(specs2);

        MatchResultDTO result = matcher.matchProducts(p1, p2);

        assertEquals(MatchConfidence.EXACT_MATCH, result.getConfidence());
        assertTrue(result.getMatchScore() >= 85.0);
        assertFalse(result.isCriticalMismatch());
    }

    @Test
    @DisplayName("Should detect Different Product for different brands")
    void testDifferentProductDifferentBrand() {
        Product p1 = new Product("Apple MacBook Air M2", "Apple", "MC7X4HN/A", "Laptops", 99990.0, "", "");
        Product p2 = new Product("Dell Inspiron 15 Core i7", "Dell", "IN3530", "Laptops", 64990.0, "", "");

        MatchResultDTO result = matcher.matchProducts(p1, p2);

        assertEquals(MatchConfidence.DIFFERENT_PRODUCT, result.getConfidence());
        assertTrue(result.isCriticalMismatch());
    }

    @Test
    @DisplayName("Should detect Different Product for different categories")
    void testDifferentProductDifferentCategory() {
        Product p1 = new Product("Sony WH-1000XM5", "Sony", "XM5", "Headphones", 29990.0, "", "");
        Product p2 = new Product("Sony Bravia 55 inch TV", "Sony", "KD55", "TVs", 54990.0, "", "");

        MatchResultDTO result = matcher.matchProducts(p1, p2);

        assertEquals(MatchConfidence.DIFFERENT_PRODUCT, result.getConfidence());
        assertTrue(result.isCriticalMismatch());
    }
}
