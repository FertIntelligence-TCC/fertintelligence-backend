package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.service.implementation.CropFertilizationTableServiceImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CropFertilizationTableLimingCriterionTest {

    private final CropFertilizationTableServiceImpl service = new CropFertilizationTableServiceImpl(
            null, null, null, null, null, null, null, null, null, null, null);

    @Test
    void resolvesUndefinedCriterionWithoutFertilityAnalysis() {
        assertThat(service.resolveIndicatedLimingCriterion(null, null))
                .isEqualTo("Não é possível definir um critério de calagem");
    }

    @Test
    void resolvesBaseSaturationWithOnlyFertilityAnalysis() {
        FertilityAnalysisExtractModel fertility = FertilityAnalysisExtractModel.builder().build();

        assertThat(service.resolveIndicatedLimingCriterion(null, fertility))
                .isEqualTo("SATURAÇÃO POR BASES TROCÁVEIS");
    }

    @Test
    void resolvesAluminumNeutralizationWhenFirstCalculationIsGreaterOrTied() {
        PhysicalAnalysisExtractModel physical = PhysicalAnalysisExtractModel.builder()
                .teorArgila(200.0)
                .build();
        FertilityAnalysisExtractModel fertility = FertilityAnalysisExtractModel.builder()
                .aluminio(2.0)
                .calcio(1.0)
                .magnesio(0.5)
                .build();

        assertThat(service.resolveIndicatedLimingCriterion(physical, fertility))
                .isEqualTo("Neutralização do Al trocável");
    }

    @Test
    void resolvesCalciumMagnesiumElevationWhenSecondCalculationIsGreater() {
        PhysicalAnalysisExtractModel physical = PhysicalAnalysisExtractModel.builder()
                .teorArgila(200.0)
                .build();
        FertilityAnalysisExtractModel fertility = FertilityAnalysisExtractModel.builder()
                .aluminio(0.1)
                .calcio(0.2)
                .magnesio(0.3)
                .build();

        assertThat(service.resolveIndicatedLimingCriterion(physical, fertility))
                .isEqualTo("Elevação dos teores de Ca + Mg");
    }
}
