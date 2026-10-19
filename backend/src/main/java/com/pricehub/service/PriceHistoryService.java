package com.pricehub.service;

import com.pricehub.dto.PriceHistoryDTO;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.PriceHistory;
import com.pricehub.entity.Product;
import com.pricehub.repository.MarketplaceListingRepository;
import com.pricehub.repository.PriceHistoryRepository;
import com.pricehub.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;
    private final MarketplaceListingRepository listingRepository;
    private final ProductRepository productRepository;

    public PriceHistoryService(PriceHistoryRepository priceHistoryRepository,
                               MarketplaceListingRepository listingRepository,
                               ProductRepository productRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
        this.listingRepository = listingRepository;
        this.productRepository = productRepository;
    }

    public List<PriceHistoryDTO> getHistoryForListing(Long listingId) {
        return priceHistoryRepository.findByMarketplaceListingIdOrderByRecordedAtAsc(listingId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getProductPriceAnalytics(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        List<MarketplaceListing> listings = listingRepository.findByProductId(productId);
        List<Long> listingIds = listings.stream().map(MarketplaceListing::getId).collect(Collectors.toList());

        List<PriceHistory> allHistories = priceHistoryRepository.findByMarketplaceListingIdInOrderByRecordedAtAsc(listingIds);

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("productId", productId);
        analytics.put("productTitle", product.getTitle());
        analytics.put("category", product.getCategory());
        analytics.put("isDemoData", true);

        if (!allHistories.isEmpty()) {
            double lowestRecorded = allHistories.stream().mapToDouble(PriceHistory::getObservedPrice).min().orElse(0.0);
            double highestRecorded = allHistories.stream().mapToDouble(PriceHistory::getObservedPrice).max().orElse(0.0);

            PriceHistory latest = allHistories.get(allHistories.size() - 1);
            PriceHistory previous = allHistories.size() > 1 ? allHistories.get(allHistories.size() - 2) : latest;

            double currentPrice = latest.getObservedPrice();
            double prevPrice = previous.getObservedPrice();
            double changePercent = prevPrice > 0 ? ((currentPrice - prevPrice) / prevPrice) * 100.0 : 0.0;

            analytics.put("currentObservedPrice", currentPrice);
            analytics.put("previousObservedPrice", prevPrice);
            analytics.put("highestRecordedPrice", highestRecorded);
            analytics.put("lowestRecordedPrice", lowestRecorded);
            analytics.put("priceChangePercentage", Math.round(changePercent * 10.0) / 10.0);
            analytics.put("lastUpdated", latest.getRecordedAt());
        }

        // Group history points by marketplace for multi-series line chart
        Map<String, List<PriceHistoryDTO>> seriesByMarketplace = new HashMap<>();
        for (MarketplaceListing l : listings) {
            String mName = l.getMarketplace().getName();
            List<PriceHistoryDTO> series = allHistories.stream()
                    .filter(h -> h.getMarketplaceListing().getId().equals(l.getId()))
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            seriesByMarketplace.put(mName, series);
        }

        analytics.put("historySeries", seriesByMarketplace);
        return analytics;
    }

    private PriceHistoryDTO mapToDTO(PriceHistory ph) {
        return new PriceHistoryDTO(
                ph.getId(),
                ph.getMarketplaceListing().getId(),
                ph.getMarketplaceListing().getMarketplace().getName(),
                ph.getMarketplaceListing().getMarketplace().getCode(),
                ph.getObservedPrice(),
                ph.getRecordedAt(),
                ph.getIsDemoData()
        );
    }
}
