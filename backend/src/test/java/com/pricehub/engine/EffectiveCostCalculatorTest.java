package com.pricehub.engine;

import com.pricehub.adapter.AmazonAdapter;
import com.pricehub.adapter.FlipkartAdapter;
import com.pricehub.adapter.MarketplaceAdapterRegistry;
import com.pricehub.adapter.RelianceDigitalAdapter;
import com.pricehub.dto.CostBreakdownDTO;
import com.pricehub.entity.Marketplace;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Offer;
import com.pricehub.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EffectiveCostCalculatorTest {

    private EffectiveCostCalculator costCalculator;
    private Marketplace amazon;
    private Product laptop;

    @BeforeEach
    void setUp() {
        AmazonAdapter amazonAdapter = new AmazonAdapter();
        FlipkartAdapter flipkartAdapter = new FlipkartAdapter();
        RelianceDigitalAdapter relianceAdapter = new RelianceDigitalAdapter();
        MarketplaceAdapterRegistry registry = new MarketplaceAdapterRegistry(
                Arrays.asList(amazonAdapter, flipkartAdapter, relianceAdapter)
        );
        costCalculator = new EffectiveCostCalculator(registry);

        amazon = new Marketplace("Amazon India", "AMAZON", "https://amazon.in", "", 4.9);
        laptop = new Product("Dell Inspiron 15", "Dell", "IN3530", "Laptops", 60000.0, "", "");
    }

    @Test
    @DisplayName("Should accurately calculate effective cost subtracting bank discounts")
    void testEffectiveCostWithDiscounts() {
        MarketplaceListing listing = new MarketplaceListing(
                laptop, amazon, laptop.getTitle(),
                60000.0, 0.0, 0.0, 4.5, 500, 12, 2, "IN_STOCK", "", "DEMO_DATA"
        );

        Offer bankOffer = new Offer(listing, "BANK_CARD", "HDFC Instant Discount", "", 3000.0, 0.0, 20000.0, "HDFC", "CREDIT", "HDFC3K");
        Offer couponOffer = new Offer(listing, "COUPON", "₹500 Cart Coupon", "", 500.0, 0.0, 5000.0, null, "ANY", "SAVE500");
        listing.setOffers(Arrays.asList(bankOffer, couponOffer));

        CostBreakdownDTO breakdown = costCalculator.computeEffectiveCost(listing, "HDFC");

        assertEquals(60000.0, breakdown.getListedPrice());
        assertEquals(3500.0, breakdown.getTotalDiscountAmount());
        assertEquals(56500.0, breakdown.getEffectiveCost()); // 60000 - 3000 - 500
        assertTrue(breakdown.getSavingsPercentage() > 5.0);
    }
}
