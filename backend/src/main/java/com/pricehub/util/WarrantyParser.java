package com.pricehub.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WarrantyParser {

    private static final Pattern YEAR_PATTERN = Pattern.compile("^(\\d+)\\s*years?$", Pattern.CASE_INSENSITIVE);
    private static final Pattern MONTH_PATTERN = Pattern.compile("^(\\d+)\\s*months?$", Pattern.CASE_INSENSITIVE);

    /**
     * Parses warranty text to months (case-insensitive):
     * - "No Warranty" (or "None", "0") -> 0
     * - "N year(s)" -> 12 * N
     * - "N month(s)" -> N
     * - Unknown or empty -> null
     */
    public static Integer parse(String warrantyText) {
        if (warrantyText == null) {
            return null;
        }
        String trimmed = warrantyText.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.equalsIgnoreCase("no warranty")
                || trimmed.equalsIgnoreCase("none")
                || trimmed.equalsIgnoreCase("0")
                || trimmed.equalsIgnoreCase("0 months")
                || trimmed.equalsIgnoreCase("0 years")) {
            return 0;
        }
        Matcher yearMatcher = YEAR_PATTERN.matcher(trimmed);
        if (yearMatcher.matches()) {
            try {
                return Integer.parseInt(yearMatcher.group(1)) * 12;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        Matcher monthMatcher = MONTH_PATTERN.matcher(trimmed);
        if (monthMatcher.matches()) {
            try {
                return Integer.parseInt(monthMatcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
