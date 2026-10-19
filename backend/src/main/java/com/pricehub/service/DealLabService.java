package com.pricehub.service;

import com.pricehub.dto.*;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Product;
import com.pricehub.repository.MarketplaceListingRepository;
import com.pricehub.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DealLabService {

    private final ProductRepository productRepository;
    private final MarketplaceListingRepository listingRepository;
    private final ProductComparisonService comparisonService;

    public DealLabService(ProductRepository productRepository,
                          MarketplaceListingRepository listingRepository,
                          ProductComparisonService comparisonService) {
        this.productRepository = productRepository;
        this.listingRepository = listingRepository;
        this.comparisonService = comparisonService;
    }

    /**
     * Solves Deal Lab constraints dynamically on every request.
     * Nothing is stored. Candidates are in-stock listings only.
     * Each listing's valueScore is computed by the compare engine across the in-stock
     * listings of its own product (weights price 0.5, rating 0.3, warranty 0.2, ABSOLUTE rating).
     */
    @Transactional(readOnly = true)
    public DealLabResultDTO solveDealLab(DealLabCriteriaDTO criteria) {
        if (criteria == null) {
            criteria = new DealLabCriteriaDTO();
        }

        // 1. Retrieve all listings and filter in-stock candidates
        List<MarketplaceListing> allListings = listingRepository.findAll();

        String targetCategory = criteria.getCategory();
        boolean filterCategory = targetCategory != null &&
                !targetCategory.trim().isEmpty() &&
                !targetCategory.equalsIgnoreCase("ALL");

        String targetMarketplace = criteria.getMarketplace();
        boolean filterMarketplace = targetMarketplace != null &&
                !targetMarketplace.trim().isEmpty() &&
                !targetMarketplace.equalsIgnoreCase("ALL");

        List<MarketplaceListing> candidateListings = allListings.stream()
                .filter(MarketplaceListing::isInStock)
                .filter(l -> !filterCategory || (l.getProduct() != null &&
                        l.getProduct().getCategory() != null &&
                        l.getProduct().getCategory().equalsIgnoreCase(targetCategory.trim())))
                .filter(l -> !filterMarketplace || (l.getMarketplace() != null && (
                        l.getMarketplace().getCode().equalsIgnoreCase(targetMarketplace.trim()) ||
                        l.getMarketplace().getName().equalsIgnoreCase(targetMarketplace.trim())
                )))
                .collect(Collectors.toList());

        // 2. Compute valueScore for each product's in-stock listings using the compare engine
        Map<Long, Map<String, Double>> productMarketplaceScores = new HashMap<>();
        Set<Long> productIds = candidateListings.stream()
                .filter(l -> l.getProduct() != null && l.getProduct().getId() != null)
                .map(l -> l.getProduct().getId())
                .collect(Collectors.toSet());

        for (Long pid : productIds) {
            try {
                ProductCompareResponse comp = comparisonService.compareProduct(pid, 0.5, 0.3, 0.2, "ABSOLUTE");
                Map<String, Double> mScores = new HashMap<>();
                if (comp.getListings() != null) {
                    for (CompareListingDTO cd : comp.getListings()) {
                        if (cd.getValueScore() != null && cd.getMarketplace() != null) {
                            mScores.put(cd.getMarketplace().toUpperCase(), cd.getValueScore());
                        }
                    }
                }
                productMarketplaceScores.put(pid, mScores);
            } catch (Exception ignored) {
            }
        }

        // 3. Evaluate criteria constraints for each candidate listing
        boolean hasBudget = criteria.getMaxBudget() != null && criteria.getMaxBudget() > 0;
        boolean hasRating = criteria.getMinRating() != null && criteria.getMinRating() > 0;
        boolean hasWarranty = criteria.getMinWarrantyMonths() != null && criteria.getMinWarrantyMonths() > 0;

        List<DealItemDTO> qualifyingList = new ArrayList<>();
        List<DealItemDTO> failingList = new ArrayList<>();

        DecimalFormat df = new DecimalFormat("#,##0");

        for (MarketplaceListing l : candidateListings) {
            DealItemDTO item = mapToDealItem(l, productMarketplaceScores);

            List<String> failureReasons = new ArrayList<>();

            // A. Budget check
            if (hasBudget) {
                if (item.getPrice() == null || item.getPrice() > criteria.getMaxBudget()) {
                    failureReasons.add("Price \u20B9" + (item.getPrice() != null ? df.format(item.getPrice()) : "N/A") +
                            " exceeds budget \u20B9" + df.format(criteria.getMaxBudget()));
                }
            }

            // B. Rating check
            if (hasRating) {
                if (item.getRating() == null) {
                    failureReasons.add("Rating not available");
                } else if (item.getRating() < criteria.getMinRating()) {
                    failureReasons.add(String.format("Rating %.1f is below minimum %.1f", item.getRating(), criteria.getMinRating()));
                }
            }

            // C. Warranty check
            if (hasWarranty) {
                if (item.getWarrantyMonths() == null || item.getWarrantyMonths() <= 0) {
                    failureReasons.add("Warranty not available");
                } else if (item.getWarrantyMonths() < criteria.getMinWarrantyMonths()) {
                    failureReasons.add(String.format("Warranty %d mo is below minimum %d mo", item.getWarrantyMonths(), criteria.getMinWarrantyMonths()));
                }
            }

            item.setFailureReasons(failureReasons);

            if (failureReasons.isEmpty()) {
                qualifyingList.add(item);
            } else {
                failingList.add(item);
            }
        }

        // 4. Sort qualifying listings by valueScore DESC (ties: price ASC)
        qualifyingList.sort((a, b) -> {
            int scoreCmp = Double.compare(
                    b.getValueScore() != null ? b.getValueScore() : 0.0,
                    a.getValueScore() != null ? a.getValueScore() : 0.0
            );
            if (scoreCmp != 0) return scoreCmp;
            return Double.compare(
                    a.getPrice() != null ? a.getPrice() : 0.0,
                    b.getPrice() != null ? b.getPrice() : 0.0
            );
        });

        // Sort failing listings by price ASC
        failingList.sort(Comparator.comparing(
                item -> item.getPrice() != null ? item.getPrice() : Double.MAX_VALUE
        ));

        // 5. Calculate relaxation suggestions:
        // For each active constraint, how many failing listings would be unlocked if that constraint alone was dropped?
        List<RelaxationSuggestionDTO> suggestions = new ArrayList<>();

        if (!failingList.isEmpty()) {
            if (hasBudget) {
                int unlockedBudget = 0;
                for (DealItemDTO item : failingList) {
                    boolean ratingOk = !hasRating || (item.getRating() != null && item.getRating() >= criteria.getMinRating());
                    boolean warrantyOk = !hasWarranty || (item.getWarrantyMonths() != null && item.getWarrantyMonths() >= criteria.getMinWarrantyMonths());
                    if (ratingOk && warrantyOk) {
                        unlockedBudget++;
                    }
                }
                if (unlockedBudget > 0) {
                    suggestions.add(new RelaxationSuggestionDTO(
                            "maxBudget",
                            "BUDGET",
                            "Removing budget limit unlocks " + unlockedBudget + " more deal" + (unlockedBudget > 1 ? "s" : ""),
                            unlockedBudget,
                            "Remove budget limit"
                    ));
                }
            }

            if (hasRating) {
                int unlockedRating = 0;
                for (DealItemDTO item : failingList) {
                    boolean budgetOk = !hasBudget || (item.getPrice() != null && item.getPrice() <= criteria.getMaxBudget());
                    boolean warrantyOk = !hasWarranty || (item.getWarrantyMonths() != null && item.getWarrantyMonths() >= criteria.getMinWarrantyMonths());
                    if (budgetOk && warrantyOk) {
                        unlockedRating++;
                    }
                }
                if (unlockedRating > 0) {
                    suggestions.add(new RelaxationSuggestionDTO(
                            "minRating",
                            "RATING",
                            "Removing rating requirement unlocks " + unlockedRating + " more deal" + (unlockedRating > 1 ? "s" : ""),
                            unlockedRating,
                            "Remove rating filter"
                    ));
                }
            }

            if (hasWarranty) {
                int unlockedWarranty = 0;
                for (DealItemDTO item : failingList) {
                    boolean budgetOk = !hasBudget || (item.getPrice() != null && item.getPrice() <= criteria.getMaxBudget());
                    boolean ratingOk = !hasRating || (item.getRating() != null && item.getRating() >= criteria.getMinRating());
                    if (budgetOk && ratingOk) {
                        unlockedWarranty++;
                    }
                }
                if (unlockedWarranty > 0) {
                    suggestions.add(new RelaxationSuggestionDTO(
                            "minWarrantyMonths",
                            "WARRANTY",
                            "Relaxing warranty requirement unlocks " + unlockedWarranty + " more deal" + (unlockedWarranty > 1 ? "s" : ""),
                            unlockedWarranty,
                            "Remove warranty filter"
                    ));
                }
            }
        }

        DealLabResultDTO result = new DealLabResultDTO();
        result.setCriteria(criteria);
        result.setTotalEvaluated(candidateListings.size());
        result.setTotalCandidates(candidateListings.size());
        result.setTotalQualifying(qualifyingList.size());
        result.setTotalNonQualifying(failingList.size());
        result.setTotalFailing(failingList.size());
        result.setQualifying(qualifyingList);
        result.setFailing(failingList);
        result.setRelaxationSuggestions(suggestions);

        // Populate legacy objects for backwards compatibility
        List<QualifyingProductDTO> qpList = new ArrayList<>();
        for (DealItemDTO item : qualifyingList) {
            QualifyingProductDTO qp = new QualifyingProductDTO();
            ProductDTO pDto = new ProductDTO();
            pDto.setId(item.getProductId());
            pDto.setTitle(item.getProductTitle());
            pDto.setCategory(item.getCategory());
            pDto.setImageUrl(item.getImageUrl());
            qp.setProduct(pDto);
            qp.setValueScore(item.getValueScore());
            qp.setEffectiveCost(item.getPrice());

            MarketplaceListingDTO mDto = new MarketplaceListingDTO();
            mDto.setId(item.getId());
            MarketplaceDTO mObj = new MarketplaceDTO();
            mObj.setCode(item.getMarketplace());
            mObj.setName(item.getMarketplaceName());
            mDto.setMarketplace(mObj);
            mDto.setListedPrice(item.getPrice());
            mDto.setRating(item.getRating());
            mDto.setWarrantyMonths(item.getWarrantyMonths());
            mDto.setProductUrl(item.getProductUrl());
            qp.setBestListing(mDto);

            qpList.add(qp);
        }
        result.setQualifyingProducts(qpList);
        if (!qpList.isEmpty()) {
            result.setBestMatchProduct(qpList.get(0));
            result.setCheapestQualifyingProduct(qpList.stream()
                    .min(Comparator.comparing(QualifyingProductDTO::getEffectiveCost))
                    .orElse(qpList.get(0)));
        }

        List<NonQualifyingProductDTO> nqpList = new ArrayList<>();
        for (DealItemDTO item : failingList) {
            NonQualifyingProductDTO nqp = new NonQualifyingProductDTO();
            ProductDTO pDto = new ProductDTO();
            pDto.setId(item.getProductId());
            pDto.setTitle(item.getProductTitle());
            pDto.setCategory(item.getCategory());
            nqp.setProduct(pDto);
            nqp.setCurrentEffectiveCost(item.getPrice());
            nqp.setFailureReasons(item.getFailureReasons());
            if (!item.getFailureReasons().isEmpty()) {
                nqp.setPrimaryLimitingFactor(item.getFailureReasons().get(0));
            }
            nqpList.add(nqp);
        }
        result.setNonQualifyingProducts(nqpList);

        String summary;
        if (candidateListings.isEmpty()) {
            summary = "No candidate products found matching the selected filters.";
        } else if (qualifyingList.isEmpty()) {
            summary = "0 of " + candidateListings.size() + " candidate deals qualify. Relax a constraint below to see available deals.";
        } else {
            summary = qualifyingList.size() + " of " + candidateListings.size() + " candidate deals qualify under your criteria.";
        }
        result.setSynthesisSummary(summary);

        return result;
    }

    private DealItemDTO mapToDealItem(MarketplaceListing l, Map<Long, Map<String, Double>> productMarketplaceScores) {
        DealItemDTO item = new DealItemDTO();
        item.setId(l.getId());

        Product p = l.getProduct();
        if (p != null) {
            item.setProductId(p.getId());
            item.setProductTitle(p.getName() != null && !p.getName().isEmpty() ? p.getName() : p.getTitle());
            item.setCategory(p.getCategory());
            item.setImageUrl(p.getImageUrl());
        }

        if (l.getMarketplace() != null) {
            item.setMarketplace(l.getMarketplace().getCode());
            item.setMarketplaceName(l.getMarketplace().getName());
        }

        item.setPrice(l.getPrice());
        item.setRating(l.getRating());
        item.setWarrantyMonths(l.getWarrantyMonths());
        item.setWarrantyText(l.getWarrantyText() != null ? l.getWarrantyText() :
                (l.getWarrantyMonths() != null && l.getWarrantyMonths() > 0 ? l.getWarrantyMonths() + " months" : "No warranty"));

        // Retrieve compare engine valueScore
        Double score = null;
        if (p != null && p.getId() != null && l.getMarketplace() != null && l.getMarketplace().getCode() != null) {
            Map<String, Double> mScores = productMarketplaceScores.get(p.getId());
            if (mScores != null) {
                score = mScores.get(l.getMarketplace().getCode().toUpperCase());
            }
        }
        item.setValueScore(score != null ? Math.round(score * 10.0) / 10.0 : 0.0);

        item.setProductUrl(l.getProductUrl());
        item.setInStock(l.isInStock());
        item.setPriceDate(l.getPriceDate());

        return item;
    }
}
