package com.pricehub.service;

import com.pricehub.dto.PriceAlertDTO;
import com.pricehub.dto.PriceAlertRequest;
import com.pricehub.entity.Product;
import com.pricehub.entity.User;
import com.pricehub.repository.ProductRepository;
import com.pricehub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PriceAlertTest {

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private User testUser;
    private Product neckbandProduct;

    @BeforeEach
    void setUp() {
        csvImportService.importFromDefaultLocation();

        testUser = userRepository.findByEmail("test_alerts_user@example.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("test_alerts_user@example.com");
            u.setPasswordHash("password123");
            u.setFullName("Alerts Test User");
            return userRepository.save(u);
        });

        // Find "Wireless Bluetooth Neckband Earphones"
        neckbandProduct = productRepository.findAll().stream()
                .filter(p -> p.getTitle().toLowerCase().contains("wireless bluetooth neckband earphones"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Wireless Bluetooth Neckband Earphones not found in seeded catalog"));
    }

    @Test
    @DisplayName("Wireless Bluetooth Neckband Earphones, target ₹250 is met on Meesho (₹222)")
    void testTargetMetOnMeesho() {
        PriceAlertRequest req = new PriceAlertRequest();
        req.setProductId(neckbandProduct.getId());
        req.setTargetPrice(250.0);

        userService.createPriceAlert(testUser.getId(), req);

        List<PriceAlertDTO> alerts = userService.getUserPriceAlerts(testUser.getId());
        assertFalse(alerts.isEmpty());
        PriceAlertDTO alert = alerts.get(0);

        assertEquals("Target met on Meesho at ₹222", alert.getStatus());
        assertTrue(alert.getIsTriggered());
    }

    @Test
    @DisplayName("Wireless Bluetooth Neckband Earphones, target ₹200 is not met and the lowest in-stock price is ₹222, ₹22 above the target")
    void testTargetNotMet() {
        PriceAlertRequest req = new PriceAlertRequest();
        req.setProductId(neckbandProduct.getId());
        req.setTargetPrice(200.0);

        userService.createPriceAlert(testUser.getId(), req);

        List<PriceAlertDTO> alerts = userService.getUserPriceAlerts(testUser.getId());
        assertFalse(alerts.isEmpty());
        PriceAlertDTO alert = alerts.get(0);

        assertEquals("Not met: lowest in-stock price is ₹222, ₹22 above your target", alert.getStatus());
        assertFalse(alert.getIsTriggered());
    }
}
