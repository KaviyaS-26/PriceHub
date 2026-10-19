package com.pricehub.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarrantyParserTest {

    @Test
    @DisplayName("Should parse 'No Warranty' or zero variations to 0 months")
    void testNoWarranty() {
        assertEquals(0, WarrantyParser.parse("No Warranty"));
        assertEquals(0, WarrantyParser.parse("no warranty"));
        assertEquals(0, WarrantyParser.parse("None"));
        assertEquals(0, WarrantyParser.parse("0"));
        assertEquals(0, WarrantyParser.parse("0 months"));
        assertEquals(0, WarrantyParser.parse("0 years"));
    }

    @Test
    @DisplayName("Should parse year warranties to months (12 * N)")
    void testYearWarranty() {
        assertEquals(12, WarrantyParser.parse("1 Year"));
        assertEquals(12, WarrantyParser.parse("1 year"));
        assertEquals(24, WarrantyParser.parse("2 year"));
        assertEquals(24, WarrantyParser.parse("2 years"));
        assertEquals(36, WarrantyParser.parse("3 Years"));
    }

    @Test
    @DisplayName("Should parse month warranties to months")
    void testMonthWarranty() {
        assertEquals(1, WarrantyParser.parse("1 Month"));
        assertEquals(1, WarrantyParser.parse("1 month"));
        assertEquals(6, WarrantyParser.parse("6 Months"));
        assertEquals(6, WarrantyParser.parse("6 months"));
    }

    @Test
    @DisplayName("Should return null for unknown or empty warranty text")
    void testUnknownOrEmpty() {
        assertNull(WarrantyParser.parse(null));
        assertNull(WarrantyParser.parse(""));
        assertNull(WarrantyParser.parse("   "));
        assertNull(WarrantyParser.parse("Unknown"));
        assertNull(WarrantyParser.parse("Standard"));
    }
}
