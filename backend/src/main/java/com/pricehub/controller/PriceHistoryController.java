package com.pricehub.controller;

import com.pricehub.dto.ApiResponse;
import com.pricehub.dto.PriceHistoryDTO;
import com.pricehub.service.PriceHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping("/products/{id}/price-history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductPriceHistory(@PathVariable Long id) {
        try {
            Map<String, Object> analytics = priceHistoryService.getProductPriceAnalytics(id);
            return ResponseEntity.ok(ApiResponse.ok(analytics));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/products/listings/{listingId}/price-history")
    public ResponseEntity<ApiResponse<List<PriceHistoryDTO>>> getListingPriceHistory(@PathVariable Long listingId) {
        List<PriceHistoryDTO> history = priceHistoryService.getHistoryForListing(listingId);
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}
