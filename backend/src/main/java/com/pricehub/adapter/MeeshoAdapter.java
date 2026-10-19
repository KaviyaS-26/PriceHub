package com.pricehub.adapter;

import org.springframework.stereotype.Component;

@Component
public class MeeshoAdapter extends BaseMarketplaceAdapter {

    @Override
    public String getMarketplaceCode() {
        return "MEESHO";
    }

    @Override
    public String getMarketplaceDisplayName() {
        return "Meesho";
    }

    @Override
    public String getBaseUrl() {
        return "https://www.meesho.com";
    }

    @Override
    public String getLogoUrl() {
        return "https://images.meesho.com/images/meesho_logo.svg";
    }

    @Override
    public Double getTrustScore() {
        return 4.6;
    }

    @Override
    public boolean isLiveApiAvailable() {
        return false;
    }
}
