package com.pricehub.controller;

import com.pricehub.dto.*;
import com.pricehub.security.SecurityUtils;
import com.pricehub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    public UserController(UserService userService, SecurityUtils securityUtils) {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/account/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        return ResponseEntity.ok(ApiResponse.ok(userService.getProfile(userId)));
    }

    @PutMapping("/account/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@RequestBody UpdateProfileRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        try {
            return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", userService.updateProfile(userId, request)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/account/preferences")
    public ResponseEntity<ApiResponse<UserPreferenceDTO>> updatePreferences(@RequestBody UserPreferenceDTO dto) {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Preferences saved successfully", userService.updateUserPreferences(userId, dto)));
    }

    // Saved Products
    @PostMapping("/saved/{productId}")
    public ResponseEntity<ApiResponse<String>> saveProduct(@PathVariable Long productId, @RequestBody(required = false) Map<String, String> body) {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        String notes = body != null ? body.get("notes") : "";
        userService.saveProduct(userId, productId, notes);
        return ResponseEntity.ok(ApiResponse.ok("Product saved to your wishlist", null));
    }

    @GetMapping("/saved")
    public ResponseEntity<ApiResponse<List<SavedProductDTO>>> getSavedProducts() {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        return ResponseEntity.ok(ApiResponse.ok(userService.getSavedProducts(userId)));
    }

    @DeleteMapping("/saved/{productId}")
    public ResponseEntity<ApiResponse<String>> removeSavedProduct(@PathVariable Long productId) {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        userService.removeSavedProduct(userId, productId);
        return ResponseEntity.ok(ApiResponse.ok("Product removed from wishlist", null));
    }

    // Price Alerts
    @PostMapping("/alerts")
    public ResponseEntity<ApiResponse<PriceAlertDTO>> createPriceAlert(@Valid @RequestBody PriceAlertRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Price alert created successfully!", userService.createPriceAlert(userId, request)));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<PriceAlertDTO>>> getPriceAlerts() {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserPriceAlerts(userId)));
    }

    @DeleteMapping("/alerts/{id}")
    public ResponseEntity<ApiResponse<String>> deletePriceAlert(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        userService.deletePriceAlert(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Price alert removed", null));
    }

    // Comparison History
    @PostMapping("/comparison/history")
    public ResponseEntity<ApiResponse<String>> recordComparison(@RequestBody Map<String, String> body) {
        Long userId = securityUtils.getCurrentUserId();
        if (userId != null && body != null && body.containsKey("productIds")) {
            userService.recordComparison(userId, body.get("productIds"), body.getOrDefault("summaryTitle", "Product Comparison"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Recorded comparison", null));
    }

    @GetMapping("/comparison/history")
    public ResponseEntity<ApiResponse<List<ComparisonHistoryDTO>>> getComparisonHistory() {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserComparisonHistory(userId)));
    }

    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<String>> deleteAccount() {
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        userService.deleteAccount(userId);
        return ResponseEntity.ok(ApiResponse.ok("Account deleted successfully", null));
    }
}
