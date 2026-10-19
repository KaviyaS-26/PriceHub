package com.pricehub.adapter;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registry Pattern implementation managing Marketplace Adapters.
 * Demonstrates Dependency Injection, Inversion of Control, and Open/Closed Principle.
 */
@Component
public class MarketplaceAdapterRegistry {

    private final Map<String, MarketplaceAdapter> adapterMap = new HashMap<>();

    public MarketplaceAdapterRegistry(List<MarketplaceAdapter> adapters) {
        for (MarketplaceAdapter adapter : adapters) {
            adapterMap.put(adapter.getMarketplaceCode().toUpperCase(), adapter);
        }
    }

    public MarketplaceAdapter getAdapter(String marketplaceCode) {
        if (marketplaceCode == null) return null;
        return adapterMap.get(marketplaceCode.toUpperCase());
    }

    public Optional<MarketplaceAdapter> findAdapter(String marketplaceCode) {
        return Optional.ofNullable(getAdapter(marketplaceCode));
    }

    public Map<String, MarketplaceAdapter> getAllAdapters() {
        return adapterMap;
    }
}
