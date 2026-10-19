package com.pricehub.adapter;

import org.springframework.stereotype.Component;

@Component
public class FlipkartAdapter extends BaseMarketplaceAdapter {

    @Override
    public String getMarketplaceCode() {
        return "FLIPKART";
    }

    @Override
    public String getMarketplaceDisplayName() {
        return "Flipkart";
    }

    @Override
    public String getBaseUrl() {
        return "https://www.flipkart.com";
    }

    @Override
    public String getLogoUrl() {
        return "https://static-assets-web.flixcart.com/batman-returns/batman-returns/p/images/fkheaderlogo_exploreplus-44005d.svg";
    }

    @Override
    public Double getTrustScore() {
        return 4.8;
    }

    @Override
    public boolean isLiveApiAvailable() {
        return false; // Ready for Flipkart Affiliate / Marketplace API integration
    }
}
