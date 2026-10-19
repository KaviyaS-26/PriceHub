package com.pricehub.service;

import com.pricehub.dto.RecommendationRequest;
import com.pricehub.dto.RecommendationResponse;
import com.pricehub.engine.RecommendationEngine;
import com.pricehub.entity.Product;
import com.pricehub.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    private final ProductRepository productRepository;
    private final RecommendationEngine recommendationEngine;

    public RecommendationService(ProductRepository productRepository,
                                 RecommendationEngine recommendationEngine) {
        this.productRepository = productRepository;
        this.recommendationEngine = recommendationEngine;
    }

    public RecommendationResponse getRecommendations(RecommendationRequest request) {
        List<Product> products;
        if (request.getCategory() != null && !request.getCategory().isBlank() && !"ALL".equalsIgnoreCase(request.getCategory())) {
            products = productRepository.findByCategoryIgnoreCase(request.getCategory().trim());
        } else {
            products = productRepository.findAll();
        }

        if (request.getPreferredBrand() != null && !request.getPreferredBrand().isBlank() && !"ANY".equalsIgnoreCase(request.getPreferredBrand())) {
            products = products.stream()
                    .filter(p -> p.getBrand().equalsIgnoreCase(request.getPreferredBrand().trim()))
                    .toList();
        }

        return recommendationEngine.generateRecommendations(products, request);
    }
}
