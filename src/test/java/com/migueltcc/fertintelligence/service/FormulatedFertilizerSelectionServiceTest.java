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
    void returnsEmptySelectionWhileNewFormulatedStrategyIsPending() {
        FormulatedMineralFertilizerModel formula041608 = formulated(1L, 4d, 16d, 8d);

        FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult result =
                service.selectCandidates(List.of(formula041608), 20d, 80d, 40d);

        assertThat(result.candidates()).isEmpty();
        assertThat(result.fallbackUsed()).isFalse();
        assertThat(result.technicalMessage())
                .contains("estratégia antiga foi removida")
                .contains("nova estratégia ainda não foi implementada");
    }

    @Test
    void topAndAllCandidateHelpersDoNotExposeLegacyCandidates() {
        FormulatedMineralFertilizerModel formula041608 = formulated(1L, 4d, 16d, 8d);

        assertThat(service.selectAllCandidates(List.of(formula041608), 20d, 80d, 40d)).isEmpty();
        assertThat(service.selectTopCandidates(List.of(formula041608), 20d, 80d, 40d)).isEmpty();
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
