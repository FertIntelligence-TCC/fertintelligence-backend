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

    private FormulatedMineralFertilizerModel formulated(Long id, double n, double p2o5, double k2o) {
        return FormulatedMineralFertilizerModel.builder()
                .id(id)
                .N(n)
                .P2O5(p2o5)
                .K2O(k2o)
                .build();
    }
}
