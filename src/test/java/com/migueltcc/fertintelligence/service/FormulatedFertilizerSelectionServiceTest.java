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
    void selectsFormulatedFertilizersByCompleteRecommendedRatio() {
        FormulatedMineralFertilizerModel formula041608 = formulated(1L, 4d, 16d, 8d);
        FormulatedMineralFertilizerModel formula083216 = formulated(2L, 8d, 32d, 16d);
        FormulatedMineralFertilizerModel formula041408 = formulated(3L, 4d, 14d, 8d);

        List<FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate> candidates =
                service.selectAllCandidates(List.of(formula041608, formula083216, formula041408), 20d, 80d, 40d);

        assertThat(candidates)
                .extracting(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate::formulated)
                .containsExactly(formula041608, formula083216);
        assertThat(candidates.get(0).relation().getN()).isEqualTo(1d);
        assertThat(candidates.get(0).relation().getP()).isEqualTo(4d);
        assertThat(candidates.get(0).relation().getK()).isEqualTo(2d);
        assertThat(candidates.get(0).concentrationSum()).isEqualTo(28d);
        assertThat(candidates.get(0).fertilizerDoseKgHa()).isEqualTo(500d);
        assertThat(candidates.get(1).concentrationSum()).isEqualTo(56d);
        assertThat(candidates.get(1).fertilizerDoseKgHa()).isEqualTo(250d);
        assertThat(candidates).noneMatch(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate::approximateFallback);
    }

    @Test
    void directRatioMatchHasPriorityOverApproximationFallback() {
        FormulatedMineralFertilizerModel approximate = formulated(1L, 7d, 28d, 7d);
        FormulatedMineralFertilizerModel direct = formulated(2L, 4d, 16d, 8d);

        FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult result =
                service.selectCandidates(List.of(approximate, direct), 20d, 80d, 40d);

        assertThat(result.fallbackUsed()).isFalse();
        assertThat(result.technicalMessage()).isNull();
        assertThat(result.candidates())
                .extracting(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate::formulated)
                .containsExactly(direct);
    }

    @Test
    void filtersFormulatedFertilizersWithMissingNpkConcentration() {
        FormulatedMineralFertilizerModel noNitrogen = formulated(1L, 0d, 16d, 8d);
        FormulatedMineralFertilizerModel matching = formulated(2L, 4d, 16d, 8d);

        List<FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate> candidates =
                service.selectAllCandidates(List.of(noNitrogen, matching), 20d, 80d, 40d);

        assertThat(candidates)
                .extracting(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate::formulated)
                .containsExactly(matching);
    }

    @Test
    void limitsTopCandidatesToTwoButKeepsCompleteSelectionAvailable() {
        FormulatedMineralFertilizerModel formula041608 = formulated(1L, 4d, 16d, 8d);
        FormulatedMineralFertilizerModel formula083216 = formulated(2L, 8d, 32d, 16d);
        FormulatedMineralFertilizerModel formula124824 = formulated(3L, 12d, 48d, 24d);

        List<FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate> allCandidates =
                service.selectAllCandidates(List.of(formula041608, formula083216, formula124824), 20d, 80d, 40d);
        List<FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate> topCandidates =
                service.selectTopCandidates(List.of(formula041608, formula083216, formula124824), 20d, 80d, 40d);

        assertThat(allCandidates).hasSize(3);
        assertThat(topCandidates)
                .extracting(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate::formulated)
                .containsExactly(formula041608, formula083216);
    }

    @Test
    void returnsEmptySelectionWhenRecommendedRatioCannotBeCalculated() {
        FormulatedMineralFertilizerModel formula041608 = formulated(1L, 4d, 16d, 8d);

        List<FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate> candidates =
                service.selectAllCandidates(List.of(formula041608), null, 0d, null);

        assertThat(candidates).isEmpty();
    }

    @Test
    void selectsUpToTwoClosestFormulatedFertilizersByRatioSumFallback() {
        FormulatedMineralFertilizerModel closestLowerDose = formulated(1L, 4d, 24d, 12d);
        FormulatedMineralFertilizerModel closestHigherDose = formulated(2L, 2d, 12d, 6d);
        FormulatedMineralFertilizerModel farthest = formulated(3L, 4d, 8d, 4d);

        FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult result =
                service.selectCandidates(List.of(farthest, closestLowerDose, closestHigherDose), 20d, 80d, 40d);

        assertThat(result.fallbackUsed()).isTrue();
        assertThat(result.technicalMessage()).contains("fallback por aproximação");
        assertThat(result.candidates())
                .extracting(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate::formulated)
                .containsExactly(closestHigherDose, closestLowerDose);
        assertThat(result.candidates())
                .allMatch(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate::approximateFallback);
        assertThat(result.candidates().get(0).fertilizerDoseKgHa()).isEqualTo(700d);
        assertThat(result.candidates().get(1).fertilizerDoseKgHa()).isEqualTo(350d);
    }

    @Test
    void ordersFallbackTieByConcentrationSumBeforeDoseOrdering() {
        FormulatedMineralFertilizerModel lowerConcentration = formulated(1L, 1d, 6d, 2d);
        FormulatedMineralFertilizerModel higherConcentration = formulated(2L, 2d, 12d, 4d);
        FormulatedMineralFertilizerModel sameDistance = formulated(3L, 1d, 5d, 3d);

        List<FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate> candidates =
                service.selectAllCandidates(List.of(lowerConcentration, higherConcentration, sameDistance), 20d, 80d, 40d);

        assertThat(candidates)
                .extracting(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate::formulated)
                .containsExactly(lowerConcentration, higherConcentration);
    }

    @Test
    void returnsTechnicalMessageWhenNoFormulatedFertilizerIsApplicable() {
        FormulatedMineralFertilizerModel invalid = formulated(1L, 0d, 0d, 0d);

        FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult result =
                service.selectCandidates(List.of(invalid), 20d, 80d, 40d);

        assertThat(result.candidates()).isEmpty();
        assertThat(result.fallbackUsed()).isTrue();
        assertThat(result.technicalMessage()).contains("Nenhum adubo formulado aplicável");
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
