package com.migueltcc.fertintelligence.composedAttributes.recommendation;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TechnicalTableGroup {
    MINHAS,
    PRIVADAS,
    PUBLICAS,
    PADRAO;

    @JsonCreator
    public static TechnicalTableGroup fromJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return switch (value.trim().toUpperCase()) {
            case "PRIVATE" -> PRIVADAS;
            case "PUBLIC" -> PUBLICAS;
            case "DEFAULT" -> PADRAO;
            default -> TechnicalTableGroup.valueOf(value.trim().toUpperCase());
        };
    }
}
