package com.pricehub.service;

import com.pricehub.dto.CompareListingDTO;
import com.pricehub.dto.ProductCompareResponse;
import com.pricehub.entity.Marketplace;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Product;
import com.pricehub.repository.MarketplaceListingRepository;
import com.pricehub.repository.MarketplaceRepository;
import com.pricehub.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductComparisonServiceTest {

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private ProductComparisonService comparisonService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MarketplaceRepository marketplaceRepository;

    @Autowired
    private MarketplaceListingRepository listingRepository;

    @BeforeEach
    void setUp() {
        // Ensure standard dataset from prices.csv is loaded
        csvImportService.importFromDefaultLocation();
    }

    @Test
    @DisplayName("1. Wireless Bluetooth Neckband Earphones: Amazon 80.6, Meesho 73.4, Flipkart 45.2 -> Best Amazon")
    void testNeckbandEarphonesComparison() {
        Product product = findProductOrThrow("Wireless Bluetooth Neckband Earphones");
        ProductCompareResponse response = comparisonService.compareProduct(product.getId(), 0.5, 0.3, 0.2, "ABSOLUTE");

        assertNotNull(response);
        assertFalse(response.getAllOutOfStock());
        assertEquals("AMAZON", response.getBest().getMarketplace());

        Map<String, Double> scoreByMarketplace = response.getListings().stream()
                .collect(Collectors.toMap(CompareListingDTO::getMarketplace, CompareListingDTO::getValueScore));

        assertEquals(80.6, scoreByMarketplace.get("AMAZON"), 0.05, "Amazon valueScore should be ~80.6");
        assertEquals(73.4, scoreByMarketplace.get("MEESHO"), 0.05, "Meesho valueScore should be ~73.4");
        assertEquals(45.2, scoreByMarketplace.get("FLIPKART"), 0.05, "Flipkart valueScore should be ~45.2");

        // Amazon wins even though Meesho is cheaper (222 vs 379)
        assertEquals("AMAZON", response.getBest().getMarketplace());
        assertEquals("MEESHO", response.getLowestPrice().getMarketplace());
        assertEquals(157.0, response.getExtraCost(), 0.01);
    }

    @Test
    @DisplayName("2. Women's Sling Bag: Meesho 96.4, Flipkart 78.87, Amazon 47.0 -> Best Meesho")
    void testWomensSlingBagComparison() {
        Product product = findProductOrThrow("Women's Sling Bag");
        ProductCompareResponse response = comparisonService.compareProduct(product.getId(), 0.5, 0.3, 0.2, "ABSOLUTE");

        assertNotNull(response);
        assertEquals("MEESHO", response.getBest().getMarketplace());

        Map<String, Double> scoreByMarketplace = response.getListings().stream()
                .collect(Collectors.toMap(CompareListingDTO::getMarketplace, CompareListingDTO::getValueScore));

        assertEquals(96.4, scoreByMarketplace.get("MEESHO"), 0.05, "Meesho valueScore should be ~96.4");
        assertEquals(78.87, scoreByMarketplace.get("FLIPKART"), 0.05, "Flipkart valueScore should be ~78.87");
        assertEquals(47.0, scoreByMarketplace.get("AMAZON"), 0.05, "Amazon valueScore should be ~47.0");
    }

    @Test
    @DisplayName("3. Men's Cotton Round Neck T-Shirt: Flipkart 95.8, Amazon 43.4, Meesho excluded (out of stock) -> Best Flipkart")
    void testMensTShirtComparison() {
        Product product = findProductOrThrow("Men's Cotton Round Neck T-Shirt");
        ProductCompareResponse response = comparisonService.compareProduct(product.getId(), 0.5, 0.3, 0.2, "ABSOLUTE");

        assertNotNull(response);
        assertEquals("FLIPKART", response.getBest().getMarketplace());

        CompareListingDTO flipkart = findListing(response.getListings(), "FLIPKART");
        CompareListingDTO amazon = findListing(response.getListings(), "AMAZON");
        CompareListingDTO meesho = findListing(response.getListings(), "MEESHO");

        assertEquals(95.8, flipkart.getValueScore(), 0.05, "Flipkart valueScore should be ~95.8");
        assertEquals(43.4, amazon.getValueScore(), 0.05, "Amazon valueScore should be ~43.4");

        // Meesho is out of stock and excluded from ranking
        assertFalse(meesho.getInStock(), "Meesho listing must be out of stock");
        assertTrue(meesho.getExcludedFromRanking(), "Meesho must be excluded from ranking");
        assertNull(meesho.getValueScore(), "Out of stock listing must have null valueScore");
        assertNull(meesho.getRank(), "Out of stock listing must have null rank");
    }

    @Test
    @DisplayName("4. Kids Cycle: Meesho 92.8, Amazon 60.07, Flipkart 44.6 -> Best Meesho")
    void testKidsCycleComparison() {
        Product product = findProductOrThrow("Kids Cycle");
        ProductCompareResponse response = comparisonService.compareProduct(product.getId(), 0.5, 0.3, 0.2, "ABSOLUTE");

        assertNotNull(response);
        assertEquals("MEESHO", response.getBest().getMarketplace());

        Map<String, Double> scoreByMarketplace = response.getListings().stream()
                .collect(Collectors.toMap(CompareListingDTO::getMarketplace, CompareListingDTO::getValueScore));

        assertEquals(92.8, scoreByMarketplace.get("MEESHO"), 0.05, "Meesho valueScore should be ~92.8");
        assertEquals(60.07, scoreByMarketplace.get("AMAZON"), 0.05, "Amazon valueScore should be ~60.07");
        assertEquals(44.6, scoreByMarketplace.get("FLIPKART"), 0.05, "Flipkart valueScore should be ~44.6");
    }

    @Test
    @DisplayName("5. Missing rating edge case: factor skipped and remaining weights re-scaled, not treated as 1.0")
    void testMissingRatingRescaling() {
        // Create an isolated product with 2 listings: one with rating, one with missing rating
        Product testProduct = productRepository.save(new Product("Test Missing Rating Product", "Audio"));
        Marketplace amazon = marketplaceRepository.findByCodeIgnoreCase("AMAZON").get();
        Marketplace flipkart = marketplaceRepository.findByCodeIgnoreCase("FLIPKART").get();

        // Listing 1: Price 100, Rating 4.0 (0.8 score), Warranty 12 months (1.0 score)
        MarketplaceListing l1 = new MarketplaceListing(testProduct, amazon, "Test 1", 100.0, 0.0, 0.0, 4.0, 10, 12, 2, "IN_STOCK", "", "COLLECTED");
        l1.setWarrantyMonths(12);
        l1.setPrice(100.0);

        // Listing 2: Price 100, Rating NULL (missing), Warranty 12 months (1.0 score)
        MarketplaceListing l2 = new MarketplaceListing(testProduct, flipkart, "Test 2", 100.0, 0.0, 0.0, null, 0, 12, 2, "IN_STOCK", "", "COLLECTED");
        l2.setRating(null);
        l2.setWarrantyMonths(12);
        l2.setPrice(100.0);

        listingRepository.saveAll(Arrays.asList(l1, l2));

        ProductCompareResponse response = comparisonService.compareProduct(testProduct.getId(), 0.5, 0.3, 0.2, "ABSOLUTE");
        CompareListingDTO flipkartDTO = findListing(response.getListings(), "FLIPKART");

        assertNull(flipkartDTO.getRating(), "Rating should be null");
        assertNull(flipkartDTO.getRatingScore(), "Rating score should be null (skipped factor)");

        // For l2: Price score = 1.0 (weight 0.5), Warranty score = 1.0 (weight 0.2)
        // Available weights = 0.5 + 0.2 = 0.7. Weighted sum = 0.5*1.0 + 0.2*1.0 = 0.7.
        // valueScore = 100 * (0.7 / 0.7) = 100.0.
        // If it had treated missing rating as 0, score would have been 70.
        // If it had treated missing rating as 5.0 (perfect), score would have been 100.
        // Let's test with Price = 0 score (worst price):
        l2.setPrice(200.0);
        listingRepository.save(l2);

        ProductCompareResponse response2 = comparisonService.compareProduct(testProduct.getId(), 0.5, 0.3, 0.2, "ABSOLUTE");
        CompareListingDTO flipkartDTO2 = findListing(response2.getListings(), "FLIPKART");
        // Now l2 price score = 0.0, warranty = 1.0. Available weights = 0.7.
        // Weighted sum = 0.5*0 + 0.2*1.0 = 0.2. valueScore = 100 * (0.2 / 0.7) = 28.57!
        assertEquals(28.57, flipkartDTO2.getValueScore(), 0.05,
                "Missing rating must rescale weights over (price + warranty) = 0.7");
    }

    @Test
    @DisplayName("6. All-equal values edge case: score is 100.0 when all factors are equal")
    void testAllEqualValues() {
        Product testProduct = productRepository.save(new Product("Test All Equal Product", "Audio"));
        Marketplace amazon = marketplaceRepository.findByCodeIgnoreCase("AMAZON").get();
        Marketplace flipkart = marketplaceRepository.findByCodeIgnoreCase("FLIPKART").get();

        MarketplaceListing l1 = new MarketplaceListing(testProduct, amazon, "Test 1", 500.0, 0.0, 0.0, 5.0, 10, 12, 2, "IN_STOCK", "", "COLLECTED");
        MarketplaceListing l2 = new MarketplaceListing(testProduct, flipkart, "Test 2", 500.0, 0.0, 0.0, 5.0, 10, 12, 2, "IN_STOCK", "", "COLLECTED");
        listingRepository.saveAll(Arrays.asList(l1, l2));

        ProductCompareResponse response = comparisonService.compareProduct(testProduct.getId(), 0.5, 0.3, 0.2, "ABSOLUTE");
        for (CompareListingDTO dto : response.getListings()) {
            assertEquals(100.0, dto.getValueScore(), 0.01, "All equal factors with rating 5.0 must give valueScore 100.0");
        }
    }

    @Test
    @DisplayName("7. No in-stock listings edge case: allOutOfStock = true, best is null, clear explanation")
    void testNoInStockListings() {
        Product testProduct = productRepository.save(new Product("Test OOS Product", "Audio"));
        Marketplace meesho = marketplaceRepository.findByCodeIgnoreCase("MEESHO").get();

        MarketplaceListing l1 = new MarketplaceListing(testProduct, meesho, "Test 1", 500.0, 0.0, 0.0, 4.0, 10, 12, 2, "OUT_OF_STOCK", "", "COLLECTED");
        l1.setInStock(false);
        listingRepository.save(l1);

        ProductCompareResponse response = comparisonService.compareProduct(testProduct.getId(), 0.5, 0.3, 0.2, "ABSOLUTE");
        assertTrue(response.getAllOutOfStock(), "allOutOfStock should be true");
        assertNull(response.getBest(), "Best recommendation must be null when all listings are out of stock");
        assertNull(response.getLowestPrice(), "Lowest price must be null when all listings are out of stock");
        assertNotNull(response.getExplanation());
        assertTrue(response.getExplanation().toLowerCase().contains("no listings are currently in stock"));
    }

    private Product findProductOrThrow(String name) {
        return productRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new AssertionError("Expected product not found in database: " + name));
    }

    private CompareListingDTO findListing(List<CompareListingDTO> listings, String marketplace) {
        return listings.stream()
                .filter(l -> l.getMarketplace().equalsIgnoreCase(marketplace))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Listing not found for marketplace: " + marketplace));
    }
}
