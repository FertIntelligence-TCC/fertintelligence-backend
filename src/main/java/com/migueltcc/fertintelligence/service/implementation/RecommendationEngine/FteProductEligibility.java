package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import java.text.Normalizer;
import java.util.Locale;

final class FteProductEligibility {
    private FteProductEligibility() {}

    static boolean isBr12EligibleForNewRecommendation(String productName) {
        String normalized = normalize(productName);
        return normalized.contains("fte br 12") || normalized.contains("fte br-12");
    }

    static boolean isHistoricalSupportedFte(String productName) {
        String normalized = normalize(productName);
        return isBr12EligibleForNewRecommendation(productName)
                || normalized.contains("fte br 24") || normalized.contains("fte br-24");
    }

    private static String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT).trim();
    }
}
