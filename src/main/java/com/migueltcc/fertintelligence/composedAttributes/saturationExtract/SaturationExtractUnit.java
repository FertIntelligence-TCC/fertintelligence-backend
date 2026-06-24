package com.migueltcc.fertintelligence.composedAttributes.saturationExtract;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum SaturationExtractUnit {
    MMOLC_POWER_HALF("(mmolc)**0.5");

    private final String symbol;

    SaturationExtractUnit(String symbol) {
        this.symbol = symbol;
    }

    @JsonValue
    public String getSymbol() {
        return symbol;
    }

    @JsonCreator
    public static SaturationExtractUnit fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim()
                .toLowerCase(Locale.ROOT)
                .replace(" ", "")
                .replace("^", "**");

        return switch (normalized) {
            case "(mmolc)**0.5", "mmolc**0.5", "mmolc^0.5", "mmolc/mmolc**0.5", "mmolc/mmolc^0.5" -> MMOLC_POWER_HALF;
            default -> SaturationExtractUnit.valueOf(value.trim().toUpperCase(Locale.ROOT));
        };
    }

    public SaturationExtractUnit canonicalForSaturationExtract() {
        return MMOLC_POWER_HALF;
    }
}
