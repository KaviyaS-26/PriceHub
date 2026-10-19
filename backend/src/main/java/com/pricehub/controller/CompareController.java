package com.pricehub.controller;

import com.pricehub.dto.ApiResponse;
import com.pricehub.dto.ProductCompareResponse;
import com.pricehub.service.ProductComparisonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CompareController {

    private final ProductComparisonService comparisonService;

    public CompareController(ProductComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @GetMapping("/compare")
    public ResponseEntity<ApiResponse<ProductCompareResponse>> compare(
            @RequestParam("product") Long productId,
            @RequestParam(value = "wPrice", required = false, defaultValue = "0.5") Double wPrice,
            @RequestParam(value = "wRating", required = false, defaultValue = "0.3") Double wRating,
            @RequestParam(value = "wWarranty", required = false, defaultValue = "0.2") Double wWarranty,
            @RequestParam(value = "ratingMethod", required = false, defaultValue = "ABSOLUTE") String ratingMethod) {

        try {
            ProductCompareResponse response = comparisonService.compareProduct(
                    productId, wPrice, wRating, wWarranty, ratingMethod
            );
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
