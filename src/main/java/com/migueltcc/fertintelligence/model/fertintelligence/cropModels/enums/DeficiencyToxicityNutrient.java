package com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;

public enum DeficiencyToxicityNutrient {
    NITROGENIO,
    FOSFORO,
    POTASSIO,
    CALCIO,
    MAGNESIO,
    ENXOFRE,
    BORO,
    COBRE,
    FERRO,
    MANGANES,
    MOLIBDENIO,
    ZINCO,
    NIQUEL;

    @JsonCreator
    public static DeficiencyToxicityNutrient fromValue(String value) {
        if (value == null || value.isBlank()) return null;

        String normalized = normalize(value);

        return Arrays.stream(values())
                .filter(item -> item.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Valor inválido para nutrient: '" + value + "'. Valores aceitos: " + Arrays.toString(values())));
    }

    private static String normalize(String value) {
        String noAccent = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return noAccent.trim().toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
    }
}
