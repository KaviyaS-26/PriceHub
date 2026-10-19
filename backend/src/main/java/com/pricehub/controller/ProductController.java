package com.pricehub.controller;

import com.pricehub.dto.ApiResponse;
import com.pricehub.dto.MarketplaceListingDTO;
import com.pricehub.dto.ProductDTO;
import com.pricehub.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String marketplace,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false, defaultValue = "BEST_VALUE") String sortBy) {

        String searchTerm = (q != null && !q.trim().isEmpty()) ? q.trim() : keyword;
        List<ProductDTO> products = productService.searchProducts(category, searchTerm, marketplace, minPrice, maxPrice, minRating, sortBy);
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
            @PathVariable Long id,
            @RequestParam(required = false) String cardType) {
        try {
            ProductDTO product = productService.getProductById(id, cardType);
            return ResponseEntity.ok(ApiResponse.ok(product));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/top-deals")
    public ResponseEntity<ApiResponse<List<MarketplaceListingDTO>>> getTopDeals(
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "12") Integer limit) {
        List<MarketplaceListingDTO> deals = productService.getTopDeals(category, limit);
        return ResponseEntity.ok(ApiResponse.ok(deals));
    }

    @GetMapping("/products/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }
}
