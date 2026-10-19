package com.pricehub.adapter;

import com.pricehub.dto.CostBreakdownDTO;
import com.pricehub.dto.MarketplaceDTO;
import com.pricehub.dto.MarketplaceListingDTO;
import com.pricehub.dto.OfferDTO;
import com.pricehub.entity.MarketplaceListing;
import com.pricehub.entity.Offer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base adapter providing shared calculation logic, inheritance and encapsulation.
 */
public abstract class BaseMarketplaceAdapter implements MarketplaceAdapter {

    @Override
    public CostBreakdownDTO calculateEffectiveCost(MarketplaceListing listing, String paymentCardType) {
        CostBreakdownDTO breakdown = new CostBreakdownDTO();
        double listedPrice = listing.getListedPrice() != null ? listing.getListedPrice() : 0.0;
        double delivery = listing.getDeliveryCharge() != null ? listing.getDeliveryCharge() : 0.0;
        double mandatory = listing.getMandatoryCharges() != null ? listing.getMandatoryCharges() : 0.0;
        double grossPrice = listedPrice + delivery + mandatory;

        breakdown.setListedPrice(listedPrice);
        breakdown.setDeliveryCharge(delivery);
        breakdown.setMandatoryCharges(mandatory);
        breakdown.setGrossPrice(grossPrice);
        breakdown.setIsDemoData("DEMO_DATA".equalsIgnoreCase(listing.getDataSource()));
        breakdown.setPriceFreshness("Verified freshly from " + getMarketplaceDisplayName());

        double totalDiscounts = 0.0;
        List<OfferDTO> appliedOffers = new ArrayList<>();
        StringBuilder explanation = new StringBuilder();
        explanation.append(String.format("Base price ₹%,.0f on %s", listedPrice, getMarketplaceDisplayName()));

        if (delivery > 0) {
            explanation.append(String.format(" + ₹%,.0f delivery", delivery));
        } else {
            explanation.append(" + Free Delivery");
        }

        if (mandatory > 0) {
            explanation.append(String.format(" + ₹%,.0f mandatory handling fee", mandatory));
        }

        if (listing.getOffers() != null && !listing.getOffers().isEmpty()) {
            for (Offer offer : listing.getOffers()) {
                boolean isApplicable = false;

                // Check minimum purchase amount
                if (offer.getMinimumPurchaseAmount() != null && listedPrice < offer.getMinimumPurchaseAmount()) {
                    continue;
                }

                if ("PUBLIC".equalsIgnoreCase(offer.getOfferType()) || "COUPON".equalsIgnoreCase(offer.getOfferType())) {
                    isApplicable = true;
                } else if ("BANK_CARD".equalsIgnoreCase(offer.getOfferType())) {
                    if (paymentCardType == null || paymentCardType.isEmpty() || "ANY".equalsIgnoreCase(paymentCardType)) {
                        isApplicable = true; // Best available scenario
                    } else if (offer.getBankName() != null && paymentCardType.toUpperCase().contains(offer.getBankName().toUpperCase())) {
                        isApplicable = true;
                    }
                } else if ("MEMBERSHIP".equalsIgnoreCase(offer.getOfferType())) {
                    isApplicable = true;
                }

                if (isApplicable) {
                    double discountVal = 0.0;
                    if (offer.getDiscountAmount() != null && offer.getDiscountAmount() > 0) {
                        discountVal = offer.getDiscountAmount();
                    } else if (offer.getDiscountPercentage() != null && offer.getDiscountPercentage() > 0) {
                        discountVal = (listedPrice * offer.getDiscountPercentage()) / 100.0;
                    }

                    if (discountVal > 0) {
                        totalDiscounts += discountVal;
                        appliedOffers.add(mapOfferToDTO(offer));
                        explanation.append(String.format(" - ₹%,.0f (%s)", discountVal, offer.getTitle()));
                    }
                }
            }
        }

        double finalEffectiveCost = Math.max(0.0, grossPrice - totalDiscounts);
        double savingsPercent = grossPrice > 0 ? (totalDiscounts / grossPrice) * 100.0 : 0.0;

        breakdown.setTotalDiscountAmount(totalDiscounts);
        breakdown.setEffectiveCost(finalEffectiveCost);
        breakdown.setSavingsPercentage(Math.round(savingsPercent * 10.0) / 10.0);
        breakdown.setAppliedOffers(appliedOffers);
        breakdown.setCalculationExplanation(explanation.toString() + String.format(" = Final Effective Cost ₹%,.0f", finalEffectiveCost));

        return breakdown;
    }

    @Override
    public MarketplaceListingDTO mapToDTO(MarketplaceListing listing, String paymentCardType) {
        MarketplaceListingDTO dto = new MarketplaceListingDTO();
        dto.setId(listing.getId());
        dto.setProductId(listing.getProduct() != null ? listing.getProduct().getId() : null);
        dto.setProductTitle(listing.getProduct() != null ? listing.getProduct().getTitle() : null);

        MarketplaceDTO mDto = new MarketplaceDTO(
                listing.getMarketplace().getId(),
                listing.getMarketplace().getName(),
                listing.getMarketplace().getCode(),
                listing.getMarketplace().getBaseUrl(),
                listing.getMarketplace().getLogoUrl(),
                listing.getMarketplace().getTrustScore()
        );
        dto.setMarketplace(mDto);

        dto.setExternalListingId(listing.getExternalListingId());
        dto.setTitle(listing.getTitle());
        dto.setListedPrice(listing.getListedPrice());
        dto.setDeliveryCharge(listing.getDeliveryCharge());
        dto.setMandatoryCharges(listing.getMandatoryCharges());
        dto.setRating(listing.getRating());
        dto.setReviewCount(listing.getReviewCount());
        dto.setWarrantyMonths(listing.getWarrantyMonths());
        dto.setDeliveryEstimateDays(listing.getDeliveryEstimateDays());
        dto.setStockStatus(listing.getStockStatus());
        dto.setIsVerified(listing.getIsVerified());
        dto.setProductUrl(listing.getProductUrl());
        dto.setDataSource(listing.getDataSource());
        dto.setLastUpdated(listing.getLastUpdated());

        List<OfferDTO> offerDTOs = new ArrayList<>();
        if (listing.getOffers() != null) {
            for (Offer o : listing.getOffers()) {
                offerDTOs.add(mapOfferToDTO(o));
            }
        }
        dto.setOffers(offerDTOs);

        CostBreakdownDTO costBreakdown = calculateEffectiveCost(listing, paymentCardType);
        dto.setCostBreakdown(costBreakdown);

        return dto;
    }

    protected OfferDTO mapOfferToDTO(Offer offer) {
        OfferDTO dto = new OfferDTO();
        dto.setId(offer.getId());
        dto.setOfferType(offer.getOfferType());
        dto.setTitle(offer.getTitle());
        dto.setDescription(offer.getDescription());
        dto.setDiscountAmount(offer.getDiscountAmount());
        dto.setDiscountPercentage(offer.getDiscountPercentage());
        dto.setMinimumPurchaseAmount(offer.getMinimumPurchaseAmount());
        dto.setBankName(offer.getBankName());
        dto.setCardType(offer.getCardType());
        dto.setCouponCode(offer.getCouponCode());
        dto.setTermsConditions(offer.getTermsConditions());
        dto.setIsVerified(offer.getIsVerified());
        dto.setValidUntil(offer.getValidUntil());
        return dto;
    }
}
