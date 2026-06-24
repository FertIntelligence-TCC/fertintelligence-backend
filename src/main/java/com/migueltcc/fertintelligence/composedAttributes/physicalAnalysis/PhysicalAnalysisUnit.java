package com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum PhysicalAnalysisUnit {
    G_PER_DM3("g/dm3"),
    G_PER_KG("g/kg");

    private final String symbol;

    PhysicalAnalysisUnit(String symbol) {
        this.symbol = symbol;
    }

    @JsonValue
    public String getSymbol() {
        return symbol;
    }

    @JsonCreator
    public static PhysicalAnalysisUnit fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim()
                .toLowerCase(Locale.ROOT)
                .replace("³", "3");

        return switch (normalized) {
            case "g/dm3", "g per dm3", "g-per-dm3", "g_per_dm3", "g/dm³" -> G_PER_DM3;
            case "g/kg", "g per kg", "g-per-kg", "g_per_kg" -> G_PER_KG;
            default -> PhysicalAnalysisUnit.valueOf(value.trim().toUpperCase(Locale.ROOT));
        };
    }

    public PhysicalAnalysisUnit canonicalForPhysicalExtract() {
        return G_PER_DM3;
    }
}
