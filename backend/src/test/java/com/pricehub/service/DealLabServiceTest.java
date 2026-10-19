package com.pricehub.service;

import com.pricehub.dto.DealItemDTO;
import com.pricehub.dto.DealLabCriteriaDTO;
import com.pricehub.dto.DealLabResultDTO;
import com.pricehub.dto.RelaxationSuggestionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DealLabServiceTest {

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private DealLabService dealLabService;

    @BeforeEach
    void setUp() {
        csvImportService.importFromDefaultLocation();
    }

    @Test
    @DisplayName("Test 1: No filters (defaults) -> all in-stock candidate listings qualify, 0 fail")
    void test1_NoFiltersDefaults() {
        DealLabCriteriaDTO criteria = new DealLabCriteriaDTO();
        criteria.setCategory("All");
        criteria.setMaxBudget(null);
        criteria.setMinRating(null);
        criteria.setMinWarrantyMonths(null);
        criteria.setMarketplace("ALL");

        DealLabResultDTO result = dealLabService.solveDealLab(criteria);

        assertNotNull(result);
        assertTrue(result.getTotalCandidates() > 0, "Candidates should not be empty");
        assertEquals(result.getTotalCandidates(), result.getTotalQualifying(), "All candidates qualify when no constraints are set");
        assertEquals(0, result.getTotalFailing(), "0 should fail when no constraints are set");
        assertEquals(result.getTotalCandidates(), result.getQualifying().size());
        assertEquals(0, result.getFailing().size());
    }

    @Test
    @DisplayName("Test 2: category=Audio, maxBudget=1000, minRating=4.0 -> candidate listings filtered and evaluated correctly")
    void test2_CategoryAudio_MaxBudget1000_MinRating4() {
        DealLabCriteriaDTO criteria = new DealLabCriteriaDTO();
        criteria.setCategory("Audio");
        criteria.setMaxBudget(1000.0);
        criteria.setMinRating(4.0);
        criteria.setMinWarrantyMonths(null);
        criteria.setMarketplace("ALL");

        DealLabResultDTO result = dealLabService.solveDealLab(criteria);

        assertNotNull(result);
        assertEquals(9, result.getTotalCandidates(), "3 Audio products x 3 marketplaces = 9 candidate listings");
        assertTrue(result.getTotalQualifying() > 0);
        assertTrue(result.getTotalFailing() > 0);
        assertEquals(result.getTotalCandidates(), result.getTotalQualifying() + result.getTotalFailing());

        // Every qualifying listing must have category Audio, price <= 1000, and rating >= 4.0
        for (DealItemDTO item : result.getQualifying()) {
            assertEquals("Audio", item.getCategory());
            assertTrue(item.getPrice() <= 1000.0, "Qualifying price must be <= 1000: " + item.getPrice());
            assertNotNull(item.getRating());
            assertTrue(item.getRating() >= 4.0, "Qualifying rating must be >= 4.0: " + item.getRating());
            assertTrue(item.getValueScore() > 0, "Qualifying item must have compare engine valueScore");
        }

        // Failing listings must have failed reason explaining why
        for (DealItemDTO item : result.getFailing()) {
            assertEquals("Audio", item.getCategory());
            assertFalse(item.getFailureReasons().isEmpty(), "Failing item must have failure reasons");
        }
    }

    @Test
    @DisplayName("Test 3: marketplace=Meesho, minRating=4.0 -> Meesho candidates evaluated, only rating >= 4.0 qualify")
    void test3_MarketplaceMeesho_MinRating4() {
        DealLabCriteriaDTO criteria = new DealLabCriteriaDTO();
        criteria.setCategory(null);
        criteria.setMaxBudget(null);
        criteria.setMinRating(4.0);
        criteria.setMinWarrantyMonths(null);
        criteria.setMarketplace("Meesho");

        DealLabResultDTO result = dealLabService.solveDealLab(criteria);

        assertNotNull(result);
        assertTrue(result.getTotalCandidates() > 0);
        assertTrue(result.getTotalQualifying() > 0);

        for (DealItemDTO item : result.getQualifying()) {
            assertEquals("MEESHO", item.getMarketplace());
            assertNotNull(item.getRating());
            assertTrue(item.getRating() >= 4.0);
        }

        for (DealItemDTO item : result.getFailing()) {
            assertEquals("MEESHO", item.getMarketplace());
            assertTrue(item.getRating() == null || item.getRating() < 4.0);
        }
    }

    @Test
    @DisplayName("Test 4: category=Footwear, maxBudget=300, minRating=4.0, minWarrantyMonths=12 -> Qualifying: 0, Relaxing warranty unlocks 2")
    void test4_Footwear_MaxBudget300_MinRating4_MinWarranty12() {
        DealLabCriteriaDTO criteria = new DealLabCriteriaDTO();
        criteria.setCategory("Footwear");
        criteria.setMaxBudget(300.0);
        criteria.setMinRating(4.0);
        criteria.setMinWarrantyMonths(12);
        criteria.setMarketplace("ALL");

        DealLabResultDTO result = dealLabService.solveDealLab(criteria);

        assertNotNull(result);
        assertEquals(6, result.getTotalCandidates());
        assertEquals(0, result.getTotalQualifying());
        assertEquals(6, result.getTotalFailing());

        // Relaxing minWarranty alone must unlock exactly 2 listings:
        // Women's Flat Sandals on Meesho (150, rating 4.0) and Flipkart (200, rating 4.1)
        Optional<RelaxationSuggestionDTO> warrantySuggestion = result.getRelaxationSuggestions().stream()
                .filter(s -> "minWarrantyMonths".equalsIgnoreCase(s.getConstraint()) || "WARRANTY".equalsIgnoreCase(s.getConstraintType()))
                .findFirst();

        assertTrue(warrantySuggestion.isPresent(), "Expected relaxation suggestion for warranty");
        assertEquals(2, warrantySuggestion.get().getAdditionalProductsUnlocked());
    }

    @Test
    @DisplayName("Test 5: maxBudget=200 -> Only listings with price <= 200 qualify")
    void test5_MaxBudget200() {
        DealLabCriteriaDTO criteria = new DealLabCriteriaDTO();
        criteria.setCategory(null);
        criteria.setMaxBudget(200.0);
        criteria.setMinRating(null);
        criteria.setMinWarrantyMonths(null);
        criteria.setMarketplace("ALL");

        DealLabResultDTO result = dealLabService.solveDealLab(criteria);

        assertNotNull(result);
        assertTrue(result.getTotalCandidates() > 0);
        assertTrue(result.getTotalQualifying() > 0);
        for (DealItemDTO item : result.getQualifying()) {
            assertTrue(item.getPrice() <= 200.0, "Listing price " + item.getPrice() + " should be <= 200");
        }
        for (DealItemDTO item : result.getFailing()) {
            assertTrue(item.getPrice() > 200.0, "Failing price " + item.getPrice() + " should be > 200");
        }
    }
}
