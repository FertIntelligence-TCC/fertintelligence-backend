package com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum FertilityAnalysisUnit {
    MMOLC_PER_DM3("mmolc/dm³"),
    CMOLC_PER_DM3("cmolc/dm³");

    private final String symbol;

    FertilityAnalysisUnit(String symbol) {
        this.symbol = symbol;
    }

    @JsonValue
    public String getSymbol() {
        return symbol;
    }

    @JsonCreator
    public static FertilityAnalysisUnit fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim()
                .toLowerCase(Locale.ROOT)
                .replace("³", "3");

        return switch (normalized) {
            case "mmolc/dm3", "mmolc per dm3", "mmolc-per-dm3", "mmolc_per_dm3", "mmolc/dm³" -> MMOLC_PER_DM3;
            case "cmolc/dm3", "cmolc per dm3", "cmolc-per-dm3", "cmolc_per_dm3", "cmolc/dm³" -> CMOLC_PER_DM3;
            default -> FertilityAnalysisUnit.valueOf(value.trim().toUpperCase(Locale.ROOT));
        };
    }

    public FertilityAnalysisUnit canonicalForFertilityExtract() {
        return MMOLC_PER_DM3;
    }
}
