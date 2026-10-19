package com.pricehub.engine;

import com.pricehub.dto.MatchConfidence;
import com.pricehub.dto.MatchResultDTO;
import com.pricehub.entity.Product;
import com.pricehub.entity.ProductSpecification;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Section 8: Smart Product Matching Engine.
 * Compares brand, model number, RAM, storage, processor, screen size.
 * Deterministic matching rules + token similarity scoring.
 */
@Component
public class ProductMatcher {

    public MatchResultDTO matchProducts(Product p1, Product p2) {
        if (p1 == null || p2 == null) {
            return new MatchResultDTO(MatchConfidence.DIFFERENT_PRODUCT, 0.0, "One or both products are null");
        }

        if (p1.getId() != null && p1.getId().equals(p2.getId())) {
            MatchResultDTO exact = new MatchResultDTO(MatchConfidence.EXACT_MATCH, 100.0, "Same verified catalog product identity");
            exact.getMatchingAttributes().addAll(Arrays.asList("Brand", "Model Number", "Category", "Specifications"));
            return exact;
        }

        List<String> matchingAttributes = new ArrayList<>();
        List<String> differingAttributes = new ArrayList<>();
        double score = 0.0;
        double maxScore = 100.0;

        // 1. Brand matching (Critical 30 pts)
        boolean brandMatch = normalize(p1.getBrand()).equalsIgnoreCase(normalize(p2.getBrand()));
        if (brandMatch) {
            score += 30;
            matchingAttributes.add("Brand (" + p1.getBrand() + ")");
        } else {
            differingAttributes.add(String.format("Brand mismatch: %s vs %s", p1.getBrand(), p2.getBrand()));
            MatchResultDTO diff = new MatchResultDTO(MatchConfidence.DIFFERENT_PRODUCT, 10.0, "Different brands");
            diff.setCriticalMismatch(true);
            diff.setDifferingAttributes(differingAttributes);
            return diff;
        }

        // 2. Category matching (Critical 20 pts)
        boolean catMatch = normalize(p1.getCategory()).equalsIgnoreCase(normalize(p2.getCategory()));
        if (catMatch) {
            score += 20;
            matchingAttributes.add("Category (" + p1.getCategory() + ")");
        } else {
            differingAttributes.add(String.format("Category mismatch: %s vs %s", p1.getCategory(), p2.getCategory()));
            MatchResultDTO diff = new MatchResultDTO(MatchConfidence.DIFFERENT_PRODUCT, 25.0, "Different product categories");
            diff.setCriticalMismatch(true);
            diff.setDifferingAttributes(differingAttributes);
            return diff;
        }

        // 3. Model number & title similarity (30 pts)
        String model1 = normalize(p1.getModelNumber() != null ? p1.getModelNumber() : "");
        String model2 = normalize(p2.getModelNumber() != null ? p2.getModelNumber() : "");

        boolean modelMatch = !model1.isEmpty() && !model2.isEmpty() && model1.equals(model2);
        if (modelMatch) {
            score += 30;
            matchingAttributes.add("Model Number (" + p1.getModelNumber() + ")");
        } else {
            double titleSim = calculateTokenJaccardSimilarity(p1.getTitle(), p2.getTitle());
            score += titleSim * 30;
            if (titleSim > 0.7) {
                matchingAttributes.add("Title Token Match (" + Math.round(titleSim * 100) + "%)");
            } else {
                differingAttributes.add(String.format("Title / Model variance (%s vs %s)", p1.getModelNumber(), p2.getModelNumber()));
            }
        }

        // 4. Specification Comparison (20 pts)
        Map<String, String> specs1 = extractSpecMap(p1);
        Map<String, String> specs2 = extractSpecMap(p2);

        String[] keySpecs = {"ram", "storage", "processor", "screen_size"};
        int checkedKeySpecs = 0;
        int matchedKeySpecs = 0;

        for (String key : keySpecs) {
            String v1 = specs1.get(key);
            String v2 = specs2.get(key);
            if (v1 != null && v2 != null) {
                checkedKeySpecs++;
                if (normalize(v1).equals(normalize(v2))) {
                    matchedKeySpecs++;
                    matchingAttributes.add(key.toUpperCase() + ": " + v1);
                } else {
                    differingAttributes.add(key.toUpperCase() + ": " + v1 + " vs " + v2);
                }
            }
        }

        if (checkedKeySpecs > 0) {
            double specRatio = (double) matchedKeySpecs / checkedKeySpecs;
            score += specRatio * 20;
        } else {
            score += 10; // Neutral if specs not populated
        }

        MatchResultDTO result = new MatchResultDTO();
        result.setMatchScore(Math.round(score * 10.0) / 10.0);
        result.setMatchingAttributes(matchingAttributes);
        result.setDifferingAttributes(differingAttributes);

        if (score >= 85.0 && differingAttributes.isEmpty()) {
            result.setConfidence(MatchConfidence.EXACT_MATCH);
            result.setReason("Exact match across brand, model, and key specifications.");
        } else if (score >= 65.0) {
            result.setConfidence(MatchConfidence.LIKELY_MATCH);
            result.setReason("Likely match with minor attribute or variant differences.");
        } else {
            result.setConfidence(MatchConfidence.DIFFERENT_PRODUCT);
            result.setReason("Significant differences detected in model or specifications.");
            result.setCriticalMismatch(true);
        }

        return result;
    }

    private Map<String, String> extractSpecMap(Product p) {
        Map<String, String> map = new HashMap<>();
        if (p.getSpecifications() != null) {
            for (ProductSpecification spec : p.getSpecifications()) {
                if (spec.getSpecKey() != null && spec.getSpecValue() != null) {
                    map.put(spec.getSpecKey().toLowerCase().trim(), spec.getSpecValue().trim());
                }
            }
        }
        return map;
    }

    private double calculateTokenJaccardSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        Set<String> set1 = new HashSet<>(Arrays.asList(normalize(s1).split("\\s+")));
        Set<String> set2 = new HashSet<>(Arrays.asList(normalize(s2).split("\\s+")));

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private String normalize(String str) {
        if (str == null) return "";
        return str.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").replaceAll("\\s+", " ").trim();
    }
}
