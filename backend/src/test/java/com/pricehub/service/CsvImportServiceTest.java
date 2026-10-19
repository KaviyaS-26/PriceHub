package com.pricehub.service;

import com.pricehub.dto.CsvImportResultDTO;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Product;
import com.pricehub.repository.MarketplaceListingRepository;
import com.pricehub.repository.MarketplaceRepository;
import com.pricehub.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CsvImportServiceTest {

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MarketplaceRepository marketplaceRepository;

    @Autowired
    private MarketplaceListingRepository listingRepository;

    @Test
    @DisplayName("Verify CSV Import: 15 products, 45 listings, 3 out-of-stock (all Meesho), idempotency, and category mapping")
    void testImportPricesVerification() {
        // Initial import
        CsvImportResultDTO result = csvImportService.importFromDefaultLocation();

        assertNotNull(result, "Import result should not be null");
        assertEquals(15, productRepository.count(), "Expected exactly 15 products");
        assertEquals(45, listingRepository.count(), "Expected exactly 45 listings");

        // Verify out-of-stock count is exactly 3
        assertEquals(3, listingRepository.countByInStockFalse(), "Expected exactly 3 out-of-stock listings");
        assertEquals(42, listingRepository.countByInStockTrue(), "Expected exactly 42 in-stock listings");

        // Verify the 3 out-of-stock listings are all Meesho with the exact product names
        Optional<MarketplaceListing> meeshoTShirt = listingRepository.findByProductNameAndMarketplaceCode("Men's Cotton Round Neck T-Shirt", "MEESHO");
        assertTrue(meeshoTShirt.isPresent(), "Men's Cotton Round Neck T-Shirt on Meesho should exist");
        assertFalse(meeshoTShirt.get().isInStock(), "Men's Cotton Round Neck T-Shirt on Meesho must be out of stock");

        Optional<MarketplaceListing> meeshoAnalogWatch = listingRepository.findByProductNameAndMarketplaceCode("Women's Analog Wrist Watch Metal Strap", "MEESHO");
        assertTrue(meeshoAnalogWatch.isPresent(), "Women's Analog Wrist Watch Metal Strap on Meesho should exist");
        assertFalse(meeshoAnalogWatch.get().isInStock(), "Women's Analog Wrist Watch Metal Strap on Meesho must be out of stock");

        Optional<MarketplaceListing> meeshoDigitalWatch = listingRepository.findByProductNameAndMarketplaceCode("Men's Digital Sports Watch", "MEESHO");
        assertTrue(meeshoDigitalWatch.isPresent(), "Men's Digital Sports Watch on Meesho should exist");
        assertFalse(meeshoDigitalWatch.get().isInStock(), "Men's Digital Sports Watch on Meesho must be out of stock");

        // Verify all 45 listings have dataSource = "COLLECTED"
        List<MarketplaceListing> allListings = listingRepository.findAll();
        for (MarketplaceListing listing : allListings) {
            assertEquals("COLLECTED", listing.getDataSource(), "DataSource must be 'COLLECTED'");
            assertNotNull(listing.getProduct(), "Listing must be linked to a product");
            assertNotNull(listing.getMarketplace(), "Listing must be linked to a marketplace");
            assertTrue(listing.getPrice() > 0, "Listing price must be positive");
        }

        // Verify Category Mapping for products
        assertCategory("Wireless Bluetooth Neckband Earphones", "Audio");
        assertCategory("True Wireless Earbuds with Charging Case", "Audio");
        assertCategory("Wired In-Ear Earphones with Mic", "Audio");

        assertCategory("Women's Cotton Churidar Set", "Clothing");
        assertCategory("Women's Anarkali Kurti Set", "Clothing");
        assertCategory("Women's Floral Print Maxi Dress", "Clothing");
        assertCategory("Girls Cotton Party Frock", "Clothing");
        assertCategory("Men's Cotton Round Neck T-Shirt", "Clothing");

        assertCategory("Men's Running Sports Shoes", "Footwear");
        assertCategory("Women's Flat Sandals", "Footwear");

        assertCategory("Women's Analog Wrist Watch Metal Strap", "Watches");
        assertCategory("Men's Digital Sports Watch", "Watches");

        assertCategory("Women's Sling Bag", "Bags");
        assertCategory("Home Decor Item", "Home Decor");
        assertCategory("Kids Cycle", "Kids");

        // Test Idempotency: run import again
        CsvImportResultDTO secondResult = csvImportService.importFromDefaultLocation();
        assertEquals(15, productRepository.count(), "Product count should remain 15 after second import");
        assertEquals(45, listingRepository.count(), "Listing count should remain 45 after second import");
        assertEquals(45, secondResult.getUpdatedCount(), "All 45 listings should be updated, not duplicated");
        assertEquals(0, secondResult.getImportedCount(), "Zero new listings should be imported on second run");
    }

    private void assertCategory(String productName, String expectedCategory) {
        Optional<Product> prod = productRepository.findByNameIgnoreCase(productName);
        assertTrue(prod.isPresent(), "Product '" + productName + "' should exist");
        assertEquals(expectedCategory, prod.get().getCategory(),
                "Product '" + productName + "' should belong to category '" + expectedCategory + "'");
    }
}
