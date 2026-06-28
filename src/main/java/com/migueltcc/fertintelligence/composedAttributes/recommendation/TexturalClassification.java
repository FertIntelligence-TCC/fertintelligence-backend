package com.migueltcc.fertintelligence.composedAttributes.recommendation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TexturalClassification {
    BRASILEIRO("Brasileiro"),
    AMERICANO("Americano");

    private final String label;

    TexturalClassification(String label) {
        this.label = label;
    }

    @JsonCreator
    public static TexturalClassification fromJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return switch (value.trim().toUpperCase()) {
            case "BRASILEIRO", "BRAZILIAN" -> BRASILEIRO;
            case "AMERICANO", "AMERICAN" -> AMERICANO;
            default -> TexturalClassification.valueOf(value.trim().toUpperCase());
        };
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}
