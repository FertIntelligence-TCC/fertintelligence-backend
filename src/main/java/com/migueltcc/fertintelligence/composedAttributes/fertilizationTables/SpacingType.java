package com.migueltcc.fertintelligence.composedAttributes.fertilizationTables;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.text.Normalizer;
import java.util.Locale;

public enum SpacingType {
    BETWEEN_LINES_IN_METERS("Entre Linhas (m)"),
    BETWEEN_PLANTS_OR_HOLES_IN_METERS("Entre Plantas/Covas (m)"),
    PLANTS_PER_LINEAR_METER("Plantas por metro linear (m)");

    private final String description;

    SpacingType(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static SpacingType fromJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = normalize(value);
        for (SpacingType type : values()) {
            if (normalize(type.name()).equals(normalized) || normalize(type.description).equals(normalized)) {
                return type;
            }
        }
        if (normalized.equals("plantas por metro") || normalized.equals("plantas por metro m")) {
            return PLANTS_PER_LINEAR_METER;
        }
        if (normalized.equals("entre plantas") || normalized.equals("entre plantas m")
                || normalized.equals("plantas covas") || normalized.equals("plantas covas m")
                || normalized.equals("entre plantas covas") || normalized.equals("entre plantas covas m")) {
            return BETWEEN_PLANTS_OR_HOLES_IN_METERS;
        }
        throw new IllegalArgumentException("Tipo de espaçamento inválido: " + value);
    }

    private static String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]", " ")
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }
}
