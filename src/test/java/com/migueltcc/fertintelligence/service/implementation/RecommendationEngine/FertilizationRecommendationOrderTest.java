package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FertilizationRecommendationOrderTest {

    @Test
    void sortsNumericallyByPhaseAndOptionWhileKeepingComplementSequenceStable() {
        List<RecommendationCalculationService.FertilizationRecommendationRow> sorted =
                FertilizationRecommendationOrder.sort(List.of(
                        row("Opção 2 - Plantio - N"),
                        row("Opção 1 - Cobertura - formulado"),
                        row("Opção 10 - Plantio - formulado"),
                        row("Opção 1 - Plantio - formulado"),
                        row("Opção 2 - Plantio - S"),
                        row("Opção 1 - Plantio - complemento de S"),
                        row("Opção 2 - Cobertura - simples")));

        assertThat(sorted).extracting(RecommendationCalculationService.FertilizationRecommendationRow::getPhase)
                .containsExactly(
                        "Opção 1 - Plantio - formulado",
                        "Opção 1 - Plantio - complemento de S",
                        "Opção 2 - Plantio - N",
                        "Opção 2 - Plantio - S",
                        "Opção 10 - Plantio - formulado",
                        "Opção 1 - Cobertura - formulado",
                        "Opção 2 - Cobertura - simples");
    }

    private RecommendationCalculationService.FertilizationRecommendationRow row(String phase) {
        return RecommendationCalculationService.FertilizationRecommendationRow.builder().phase(phase).build();
    }
}
