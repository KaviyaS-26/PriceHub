package com.pricehub.adapter;

import com.pricehub.dto.CostBreakdownDTO;
import com.pricehub.dto.MarketplaceListingDTO;
import com.pricehub.entity.MarketplaceListing;

import java.util.List;

/**
 * Core Marketplace Adapter Interface demonstrating Java Object-Oriented Polymorphism and Abstraction.
 * Each marketplace (Amazon India, Flipkart, Reliance Digital) provides its own concrete implementation.
 */
public interface MarketplaceAdapter {

    String getMarketplaceCode();

    String getMarketplaceDisplayName();

    String getBaseUrl();

    String getLogoUrl();

    Double getTrustScore();

    boolean isLiveApiAvailable();

    CostBreakdownDTO calculateEffectiveCost(MarketplaceListing listing, String paymentCardType);

    MarketplaceListingDTO mapToDTO(MarketplaceListing listing, String paymentCardType);
}
