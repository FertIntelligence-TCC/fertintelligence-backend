package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;

import java.util.Optional;

final class FteBaseNutrientSelector {
    private FteBaseNutrientSelector() {}

    static Optional<AppliedMicronutrient> select(String boronRange,
                                                 Double boronDose,
                                                 String zincRange,
                                                 Double zincDose) {
        Level boron = Level.from(boronRange);
        Level zinc = Level.from(zincRange);
        if (boron == null) return Optional.empty();
        if (boron == Level.LOW) return positive(boronDose) ? Optional.of(AppliedMicronutrient.B) : Optional.empty();
        if (boron == Level.MEDIUM && zinc == Level.LOW) {
            return positive(zincDose) ? Optional.of(AppliedMicronutrient.Zn) : Optional.empty();
        }
        if (boron == Level.MEDIUM && zinc == Level.MEDIUM) {
            return positive(boronDose) ? Optional.of(AppliedMicronutrient.B) : Optional.empty();
        }
        if (boron == Level.HIGH && zinc != null) {
            return positive(zincDose) ? Optional.of(AppliedMicronutrient.Zn) : Optional.empty();
        }
        return Optional.empty();
    }

    private static boolean positive(Double value) {
        return value != null && Double.isFinite(value) && value > 0d;
    }

    private enum Level {
        LOW, MEDIUM, HIGH;

        static Level from(String value) {
            if (value == null) return null;
            return switch (value.trim().toLowerCase(java.util.Locale.ROOT)) {
                case "baixo", "muito baixo" -> LOW;
                case "médio", "medio" -> MEDIUM;
                case "alto", "muito alto" -> HIGH;
                default -> null;
            };
        }
    }
}
