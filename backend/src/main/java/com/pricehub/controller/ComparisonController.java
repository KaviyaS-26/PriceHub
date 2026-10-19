package com.pricehub.controller;

import com.pricehub.dto.ApiResponse;
import com.pricehub.dto.ComparisonResponse;
import com.pricehub.service.ComparisonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comparison")
public class ComparisonController {

    private final ComparisonService comparisonService;

    public ComparisonController(ComparisonService comparisonService) {
        this.comparisonService = comparisonService;
    }

    @PostMapping("/compare")
    public ResponseEntity<ApiResponse<ComparisonResponse>> compare(@RequestBody Map<String, Object> body) {
        List<Long> productIds = null;
        if (body.get("productIds") instanceof List<?>) {
            List<?> rawList = (List<?>) body.get("productIds");
            productIds = rawList.stream().map(o -> Long.valueOf(o.toString())).toList();
        }

        String cardType = body.containsKey("cardType") ? body.get("cardType").toString() : null;

        ComparisonResponse response = comparisonService.compareProducts(productIds, cardType);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/sample")
    public ResponseEntity<ApiResponse<ComparisonResponse>> getSampleComparison() {
        // Return comparison between first 2 sample laptops or smartphones
        ComparisonResponse response = comparisonService.compareProducts(Arrays.asList(1L, 2L), "ANY");
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
