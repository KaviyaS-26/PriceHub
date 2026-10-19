package com.pricehub.adapter;

import org.springframework.stereotype.Component;

@Component
public class AmazonAdapter extends BaseMarketplaceAdapter {

    @Override
    public String getMarketplaceCode() {
        return "AMAZON";
    }

    @Override
    public String getMarketplaceDisplayName() {
        return "Amazon India";
    }

    @Override
    public String getBaseUrl() {
        return "https://www.amazon.in";
    }

    @Override
    public String getLogoUrl() {
        return "https://upload.wikimedia.org/wikipedia/commons/a/a9/Amazon_logo.svg";
    }

    @Override
    public Double getTrustScore() {
        return 4.9;
    }

    @Override
    public boolean isLiveApiAvailable() {
        return false; // Ready for Amazon Product Advertising API integration when credentials provided
    }
}
