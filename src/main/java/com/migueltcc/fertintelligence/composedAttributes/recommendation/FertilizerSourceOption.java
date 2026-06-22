package com.migueltcc.fertintelligence.composedAttributes.recommendation;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FertilizerSourceOption {
    PRIVATE,
    PUBLIC,
    DEFAULT,
    BOTH;

    @JsonCreator
    public static FertilizerSourceOption fromJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return fromPersistedValue(value);
    }

    public static FertilizerSourceOption fromPersistedValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return switch (value.trim().toUpperCase()) {
            case "PRIVADAS", "PRIVATE" -> PRIVATE;
            case "PUBLICAS", "PUBLIC" -> PUBLIC;
            case "PADRAO", "DEFAULT" -> DEFAULT;
            case "AMBAS", "BOTH" -> BOTH;
            default -> FertilizerSourceOption.valueOf(value.trim().toUpperCase());
        };
    }

    public String toPersistedValue() {
        return switch (this) {
            case PRIVATE -> "PRIVADAS";
            case PUBLIC -> "PUBLICAS";
            case DEFAULT -> "PADRAO";
            case BOTH -> "AMBAS";
        };
    }
}
