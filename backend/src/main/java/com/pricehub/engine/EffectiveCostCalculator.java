package com.pricehub.engine;

import com.pricehub.adapter.MarketplaceAdapter;
import com.pricehub.adapter.MarketplaceAdapterRegistry;
import com.pricehub.dto.CostBreakdownDTO;
import com.pricehub.entity.MarketplaceListing;
import org.springframework.stereotype.Component;

/**
 * Section 9: Effective Cost Calculator Engine.
 * Formula: Effective Cost = Listed Price + Delivery Charges + Mandatory Charges - Applicable Discounts.
 */
@Component
public class EffectiveCostCalculator {

    private final MarketplaceAdapterRegistry adapterRegistry;

    public EffectiveCostCalculator(MarketplaceAdapterRegistry adapterRegistry) {
        this.adapterRegistry = adapterRegistry;
    }

    public CostBreakdownDTO computeEffectiveCost(MarketplaceListing listing, String paymentCardType) {
        if (listing == null) return new CostBreakdownDTO();

        String code = listing.getMarketplace() != null ? listing.getMarketplace().getCode() : "AMAZON";
        MarketplaceAdapter adapter = adapterRegistry.getAdapter(code);

        if (adapter != null) {
            return adapter.calculateEffectiveCost(listing, paymentCardType);
        }

        // Fallback calculation
        double listed = listing.getListedPrice() != null ? listing.getListedPrice() : 0.0;
        double delivery = listing.getDeliveryCharge() != null ? listing.getDeliveryCharge() : 0.0;
        double mandatory = listing.getMandatoryCharges() != null ? listing.getMandatoryCharges() : 0.0;

        CostBreakdownDTO breakdown = new CostBreakdownDTO();
        breakdown.setListedPrice(listed);
        breakdown.setDeliveryCharge(delivery);
        breakdown.setMandatoryCharges(mandatory);
        breakdown.setGrossPrice(listed + delivery + mandatory);
        breakdown.setTotalDiscountAmount(0.0);
        breakdown.setEffectiveCost(listed + delivery + mandatory);
        breakdown.setSavingsPercentage(0.0);
        breakdown.setCalculationExplanation(String.format("Standard listing cost: ₹%,.0f", listed + delivery + mandatory));
        return breakdown;
    }
}
