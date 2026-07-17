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
}
