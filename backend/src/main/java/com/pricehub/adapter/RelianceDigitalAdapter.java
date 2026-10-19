package com.pricehub.adapter;

import org.springframework.stereotype.Component;

@Component
public class RelianceDigitalAdapter extends BaseMarketplaceAdapter {

    @Override
    public String getMarketplaceCode() {
        return "RELIANCE_DIGITAL";
    }

    @Override
    public String getMarketplaceDisplayName() {
        return "Reliance Digital";
    }

    @Override
    public String getBaseUrl() {
        return "https://www.reliancedigital.in";
    }

    @Override
    public String getLogoUrl() {
        return "https://www.reliancedigital.in/build/client/images/loaders/rd_logo.svg";
    }

    @Override
    public Double getTrustScore() {
        return 4.7;
    }

    @Override
    public boolean isLiveApiAvailable() {
        return false; // Ready for Reliance Digital Merchant API integration
    }
}
