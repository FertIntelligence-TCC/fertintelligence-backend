package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class FertilizationRecommendationOrder {

    private static final Pattern OPTION_NUMBER = Pattern.compile("\\bopcao\\s+(\\d+)\\b");

    private FertilizationRecommendationOrder() {
    }

    static List<RecommendationCalculationService.FertilizationRecommendationRow> sort(
            List<RecommendationCalculationService.FertilizationRecommendationRow> rows) {
        return rows.stream().sorted(comparator()).toList();
    }

    private static Comparator<RecommendationCalculationService.FertilizationRecommendationRow> comparator() {
        return Comparator
                .comparingInt((RecommendationCalculationService.FertilizationRecommendationRow row) -> phaseOrder(row.getPhase()))
                .thenComparingInt(row -> optionNumber(row.getPhase()));
    }

    private static int phaseOrder(String phase) {
        String normalized = normalize(phase);
        if (normalized.contains("plantio")) return 0;
        if (normalized.contains("cobertura")) return 1;
        return 2;
    }

    private static int optionNumber(String phase) {
        Matcher matcher = OPTION_NUMBER.matcher(normalize(phase));
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : Integer.MAX_VALUE;
    }

    private static String normalize(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
