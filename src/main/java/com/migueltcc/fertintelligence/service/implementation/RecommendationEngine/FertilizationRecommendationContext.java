package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import java.util.List;

record FertilizationRecommendationContext(
        List<RecommendationCalculationService.FertilizationRecommendationRow> recommendationRows,
        List<RecommendationCalculationService.FertilizerSuggestion> fertilizerSuggestions,
        List<RecommendationCalculationService.NutrientBalanceRow> nutrientBalanceRows,
        List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> alternativeFertilizationRows,
        Double requiredN,
        Double requiredP2O5,
        Double requiredK2O,
        Long nRangeId,
        Long pRangeId,
        Long kRangeId) {
}
