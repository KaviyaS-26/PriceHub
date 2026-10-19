package com.pricehub.dto;

public enum MatchConfidence {
    EXACT_MATCH("Exact Match", "100% matched brand, model, and core specifications", "#16A34A"),
    LIKELY_MATCH("Likely Match", "High similarity match with minor non-critical spec differences", "#F59E0B"),
    DIFFERENT_PRODUCT("Different Product", "Significant differences in model, storage, processor or generation", "#EF4444");

    private final String label;
    private final String description;
    private final String colorCode;

    MatchConfidence(String label, String description, String colorCode) {
        this.label = label;
        this.description = description;
        this.colorCode = colorCode;
    }

    public String getLabel() { return label; }
    public String getDescription() { return description; }
    public String getColorCode() { return colorCode; }
}
