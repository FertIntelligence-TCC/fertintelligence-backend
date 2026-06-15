package com.migueltcc.fertintelligence.composedAttributes.fertilizers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.text.Normalizer;

public enum NaturezaFisica {
    SOLIDO("SÓLIDO"),
    LIQUIDO("LÍQUIDO");

    private final String label;

    NaturezaFisica(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static NaturezaFisica fromValue(String value) {
        if (value == null) {
            return null;
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toUpperCase();

        return NaturezaFisica.valueOf(normalized);
    }
}
