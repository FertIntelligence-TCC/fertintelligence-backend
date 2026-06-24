package com.migueltcc.fertintelligence.composedAttributes.fertilizationTables;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum FertilizationTableUnit {
    KG_PER_HA("kg/ha"),
    MG_PER_DM3("mg/dm³"),
    MMOLC_PER_DM3("mmolc/dm³");

    private final String symbol;

    FertilizationTableUnit(String symbol) {
        this.symbol = symbol;
    }

    @JsonValue
    public String getSymbol() {
        return symbol;
    }

    @JsonCreator
    public static FertilizationTableUnit fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim()
                .toLowerCase(Locale.ROOT)
                .replace("³", "3");

        return switch (normalized) {
            case "kg/ha", "kg per ha", "kg-per-ha", "kg_per_ha" -> KG_PER_HA;
            case "mg/dm3", "mg per dm3", "mg-per-dm3", "mg_per_dm3", "mg/dm³" -> MG_PER_DM3;
            case "mmolc/dm3", "mmolc per dm3", "mmolc-per-dm3", "mmolc_per_dm3", "mmolc/dm³" -> MMOLC_PER_DM3;
            default -> FertilizationTableUnit.valueOf(value.trim().toUpperCase(Locale.ROOT));
        };
    }
}
