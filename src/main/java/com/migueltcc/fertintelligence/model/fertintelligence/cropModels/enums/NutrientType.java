package com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.Locale;

public enum NutrientType {
    MACRONUTRIENT,
    MICRONUTRIENT;

    @JsonCreator
    public static NutrientType fromValue(String value) {
        if (value == null || value.isBlank()) return null;

        String normalized = value.trim().toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');

        return Arrays.stream(values())
                .filter(item -> item.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Valor inválido para nutrientType: '" + value + "'. Valores aceitos: MACRONUTRIENT, MICRONUTRIENT"));
    }
}
