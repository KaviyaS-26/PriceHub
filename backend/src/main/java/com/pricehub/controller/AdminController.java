package com.pricehub.controller;

import com.pricehub.dto.ApiResponse;
import com.pricehub.dto.CsvImportResultDTO;
import com.pricehub.service.CsvImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CsvImportService csvImportService;

    public AdminController(CsvImportService csvImportService) {
        this.csvImportService = csvImportService;
    }

    @PostMapping("/import-prices")
    public ResponseEntity<ApiResponse<CsvImportResultDTO>> importPrices(
            @RequestParam(value = "file", required = false) MultipartFile file) {
        CsvImportResultDTO result;
        if (file != null && !file.isEmpty()) {
            result = csvImportService.importPricesFromMultipartFile(file);
        } else {
            result = csvImportService.importFromDefaultLocation();
        }
        return ResponseEntity.ok(ApiResponse.ok("Prices imported successfully", result));
    }
}
