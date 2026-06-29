package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.FormulatedFertilizerRatioService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.FormulatedFertilizerSelectionService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FormulatedFertilizerSelectionServiceTest {

    private final FormulatedFertilizerSelectionService service = new FormulatedFertilizerSelectionService(
            null,
            new FormulatedFertilizerRatioService());

    @Test
    void returnsDirectMatchesWithIdenticalNpkRatio() {
        FormulatedMineralFertilizerModel formula041608 = formulated(1L, 4d, 16d, 8d);
        FormulatedMineralFertilizerModel formula101010 = formulated(2L, 10d, 10d, 10d);

        FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult result =
                service.selectCandidates(List.of(formula101010, formula041608), 20d, 80d, 40d);

        assertThat(result.candidates()).hasSize(1);
        assertThat(result.fallbackUsed()).isFalse();
        assertThat(result.technicalMessage()).isNull();
        assertThat(result.candidates().get(0).formulated()).isSameAs(formula041608);
        assertThat(result.candidates().get(0).relation().getN()).isEqualTo(1d);
        assertThat(result.candidates().get(0).relation().getP()).isEqualTo(4d);
        assertThat(result.candidates().get(0).relation().getK()).isEqualTo(2d);
        assertThat(result.candidates().get(0).ratioSum()).isEqualTo(7d);
        assertThat(result.candidates().get(0).formulatedRatioSum()).isEqualTo(7d);
        assertThat(result.candidates().get(0).concentrationSum()).isEqualTo(28d);
        assertThat(result.candidates().get(0).fertilizerDoseKgHa()).isEqualTo(500d);
        assertThat(result.candidates().get(0).approximateFallback()).isFalse();
    }

    @Test
    void sortsMultipleDirectMatchesByDoseConcentrationAndId() {
        FormulatedMineralFertilizerModel formula041608 = formulated(1L, 4d, 16d, 8d);
        FormulatedMineralFertilizerModel formula083216 = formulated(2L, 8d, 32d, 16d);
        FormulatedMineralFertilizerModel formula124824 = formulated(3L, 12d, 48d, 24d);
        FormulatedMineralFertilizerModel formula083216Later = formulated(4L, 8d, 32d, 16d);

        List<FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate> candidates =
                service.selectAllCandidates(
                        List.of(formula083216Later, formula041608, formula124824, formula083216),
                        20d,
                        80d,
                        40d);

        assertThat(candidates)
                .extracting(candidate -> candidate.formulated().getId())
                .containsExactly(1L, 2L, 4L, 3L);
    }

    @Test
    void topCandidateHelperLimitsDirectMatchesForPresentation() {
        FormulatedMineralFertilizerModel formula041608 = formulated(1L, 4d, 16d, 8d);
        FormulatedMineralFertilizerModel formula083216 = formulated(2L, 8d, 32d, 16d);
        FormulatedMineralFertilizerModel formula124824 = formulated(3L, 12d, 48d, 24d);

        List<FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate> candidates =
                service.selectTopCandidates(List.of(formula041608, formula083216, formula124824), 20d, 80d, 40d);

        assertThat(candidates)
                .extracting(candidate -> candidate.formulated().getId())
                .containsExactly(1L, 2L);
    }

    @Test
    void returnsWarningWhenRecommendationRatioCannotBeCalculated() {
        FormulatedMineralFertilizerModel formula041608 = formulated(1L, 4d, 16d, 8d);

        FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult result =
                service.selectCandidates(List.of(formula041608), null, 0d, null);

        assertThat(result.candidates()).isEmpty();
        assertThat(result.fallbackUsed()).isFalse();
        assertThat(result.technicalMessage()).contains("relacao nao calculada");
    }

    @Test
    void returnsWarningWhenNoIdenticalRatioExistsWithoutFallback() {
        FormulatedMineralFertilizerModel formula101010 = formulated(1L, 10d, 10d, 10d);

        FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult result =
                service.selectCandidates(List.of(formula101010), 20d, 80d, 40d);

        assertThat(result.candidates()).isEmpty();
        assertThat(result.fallbackUsed()).isFalse();
        assertThat(result.technicalMessage()).contains("relação N-P2O5-K2O idêntica");
    }

    private FormulatedMineralFertilizerModel formulated(Long id, double n, double p2o5, double k2o) {
        return FormulatedMineralFertilizerModel.builder()
                .id(id)
                .N(n)
                .P2O5(p2o5)
                .K2O(k2o)
                .build();
    }
}
