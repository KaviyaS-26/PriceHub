package com.pricehub.entity;

public enum MarketplaceCode {
    AMAZON("Amazon", "https://www.amazon.in", "https://upload.wikimedia.org/wikipedia/commons/a/a9/Amazon_logo.svg", 4.9),
    FLIPKART("Flipkart", "https://www.flipkart.com", "https://static-assets-web.flixcart.com/batman-returns/batman-returns/p/images/fkheaderlogo_exploreplus-44005d.svg", 4.8),
    MEESHO("Meesho", "https://www.meesho.com", "https://images.meesho.com/images/meesho_logo.svg", 4.6),
    RELIANCE_DIGITAL("Reliance Digital", "https://www.reliancedigital.in", "https://www.reliancedigital.in/build/client/images/loaders/rd_logo.svg", 4.7);

    private final String displayName;
    private final String baseUrl;
    private final String logoUrl;
    private final double defaultTrustScore;

    MarketplaceCode(String displayName, String baseUrl, String logoUrl, double defaultTrustScore) {
        this.displayName = displayName;
        this.baseUrl = baseUrl;
        this.logoUrl = logoUrl;
        this.defaultTrustScore = defaultTrustScore;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public double getDefaultTrustScore() {
        return defaultTrustScore;
    }

    public static MarketplaceCode fromString(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        for (MarketplaceCode code : values()) {
            if (code.name().equalsIgnoreCase(trimmed) || code.displayName.equalsIgnoreCase(trimmed)) {
                return code;
            }
        }
        return null;
    }
}
