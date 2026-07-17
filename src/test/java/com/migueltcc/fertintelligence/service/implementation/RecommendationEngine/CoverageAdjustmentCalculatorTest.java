package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CoverageAdjustmentCalculatorTest {
    @Test void addsPlantingDeficitToOriginalCoverage() {
        assertThat(CoverageAdjustmentCalculator.adjustedRequirement(80d, -20d)).isEqualTo(100d);
    }

    @Test void subtractsPlantingSurplusWithoutProducingNegativeRequirement() {
        assertThat(CoverageAdjustmentCalculator.adjustedRequirement(80d, 20d)).isEqualTo(60d);
        assertThat(CoverageAdjustmentCalculator.adjustedRequirement(10d, 20d)).isZero();
    }

    @Test void transferredDeficitCreatesSecondEffectiveCoverageNutrient() {
        double adjustedK2O = CoverageAdjustmentCalculator.adjustedRequirement(0d, -20d);

        assertThat(adjustedK2O).isEqualTo(20d);
        assertThat(FormulatedFertilizerSelectionService.countEffectiveRequiredNutrients(30d, 0d, adjustedK2O))
                .isEqualTo(2);
    }

    @Test void transferredSurplusCanRemoveSecondEffectiveCoverageNutrient() {
        double adjustedK2O = CoverageAdjustmentCalculator.adjustedRequirement(20d, 20d);

        assertThat(adjustedK2O).isZero();
        assertThat(FormulatedFertilizerSelectionService.countEffectiveRequiredNutrients(30d, 0d, adjustedK2O))
                .isEqualTo(1);
    }
}
