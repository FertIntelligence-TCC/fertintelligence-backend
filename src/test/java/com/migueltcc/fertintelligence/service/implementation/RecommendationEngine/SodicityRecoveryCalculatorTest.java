package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SodicityRecoveryCalculatorTest {

    @Test
    void calculatesEachLayerAndSumsOnlyPositiveDoses() {
        var result = SodicityRecoveryCalculator.calculate(List.of(
                layer(1L, 0, 20, 10d, 100d, 10d),
                layer(2L, 21, 40, 10d, 100d, 10d)));

        assertThat(result.layers()).extracting(SodicityRecoveryCalculator.LayerResult::gypsumKgHa)
                .containsExactly(860d, 860d);
        assertThat(result.totalGypsumKgHa()).isEqualTo(1720d);
        assertThat(result.hasHighPst()).isTrue();
    }

    @Test
    void treatsExactlySixPercentAsHighButNeverUsesAbsoluteValueForNegativeDose() {
        var result = SodicityRecoveryCalculator.calculate(List.of(
                layer(1L, 0, 20, 4d, 100d, 6d),
                layer(2L, 21, 40, 2d, 100d, 4d)));

        assertThat(result.layers().get(0).highPst()).isTrue();
        assertThat(result.layers().get(0).gypsumKgHa()).isEqualTo(172d);
        assertThat(result.layers().get(1).highPst()).isFalse();
        assertThat(result.layers().get(1).gypsumKgHa()).isZero();
        assertThat(result.totalGypsumKgHa()).isEqualTo(172d);
    }

    @Test
    void supportsEitherStructuredLayerWithoutMixingTheirValues() {
        var result = SodicityRecoveryCalculator.calculate(List.of(layer(2L, 21, 40, 8d, 80d, 10d)));

        assertThat(result.layers()).singleElement().satisfies(layer -> {
            assertThat(layer.label()).isEqualTo("21–40 cm");
            assertThat(layer.sodium()).isEqualTo(8d);
            assertThat(layer.ctc()).isEqualTo(80d);
            assertThat(layer.pst()).isEqualTo(10d);
            assertThat(layer.gypsumKgHa()).isEqualTo(688d);
        });
    }

    @Test
    void reportsMissingNaCtcAndPstInsteadOfConvertingThemToZero() {
        var result = SodicityRecoveryCalculator.calculate(List.of(layer(1L, 0, 20, null, null, null)));

        assertThat(result.hasCalculatedLayer()).isFalse();
        assertThat(result.layers()).singleElement().satisfies(layer -> {
            assertThat(layer.missing()).containsExactly("Na", "CTC(T)", "PST");
            assertThat(layer.gypsumKgHa()).isZero();
        });
    }

    @Test
    void ignoresUnrelatedDepthsAndNeverProducesNanOrInfinity() {
        var result = SodicityRecoveryCalculator.calculate(List.of(
                layer(1L, 10, 30, 5d, 100d, 10d),
                layer(2L, 0, 20, 5d, 0d, 10d)));

        assertThat(result.layers()).singleElement();
        assertThat(result.hasCalculatedLayer()).isFalse();
        assertThat(Double.isFinite(result.totalGypsumKgHa())).isTrue();
    }

    private FertilityAnalysisExtractModel layer(Long id, int start, int end, Double sodium, Double ctc, Double pst) {
        return FertilityAnalysisExtractModel.builder()
                .id(id)
                .rangeExtract(RangeExtractModel.builder().id(id).profundidade_inicial(start).profundidade_final(end).build())
                .sodio(sodium).ctcPh7(ctc).pst(pst)
                .build();
    }
}
