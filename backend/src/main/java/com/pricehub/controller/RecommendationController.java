package com.pricehub.controller;

import com.pricehub.dto.ApiResponse;
import com.pricehub.dto.RecommendationRequest;
import com.pricehub.dto.RecommendationResponse;
import com.pricehub.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RecommendationResponse>> getRecommendations(@RequestBody RecommendationRequest request) {
        RecommendationResponse response = recommendationService.getRecommendations(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
