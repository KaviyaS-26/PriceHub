package com.pricehub.controller;

import com.pricehub.dto.ApiResponse;
import com.pricehub.dto.DealLabCriteriaDTO;
import com.pricehub.dto.DealLabResultDTO;
import com.pricehub.service.DealLabService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DealLabController {

    private final DealLabService dealLabService;

    public DealLabController(DealLabService dealLabService) {
        this.dealLabService = dealLabService;
    }

    /**
     * GET /api/deals?category=&maxBudget=&minRating=&minWarrantyMonths=&marketplace=
     * Computes candidate evaluation and rankings dynamically on every request.
     */
    @GetMapping("/deals")
    public ResponseEntity<ApiResponse<DealLabResultDTO>> getDeals(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minWarrantyMonths,
            @RequestParam(required = false) String marketplace) {
        DealLabCriteriaDTO criteria = new DealLabCriteriaDTO(category, maxBudget, minRating, minWarrantyMonths, marketplace);
        DealLabResultDTO result = dealLabService.solveDealLab(criteria);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/deallab/solve")
    public ResponseEntity<ApiResponse<DealLabResultDTO>> solveDealLabPost(@RequestBody DealLabCriteriaDTO criteria) {
        DealLabResultDTO result = dealLabService.solveDealLab(criteria);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/deallab/solve")
    public ResponseEntity<ApiResponse<DealLabResultDTO>> solveDealLabGet(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double maxBudget,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minWarrantyMonths,
            @RequestParam(required = false) String marketplace) {
        DealLabCriteriaDTO criteria = new DealLabCriteriaDTO(category, maxBudget, minRating, minWarrantyMonths, marketplace);
        DealLabResultDTO result = dealLabService.solveDealLab(criteria);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/deallab/default-criteria")
    public ResponseEntity<ApiResponse<DealLabCriteriaDTO>> getDefaultCriteria() {
        DealLabCriteriaDTO criteria = new DealLabCriteriaDTO();
        criteria.setCategory("All");
        criteria.setMaxBudget(1000.0);
        criteria.setMinRating(4.0);
        criteria.setMinWarrantyMonths(0);
        criteria.setMarketplace("ALL");
        return ResponseEntity.ok(ApiResponse.ok(criteria));
    }
}
