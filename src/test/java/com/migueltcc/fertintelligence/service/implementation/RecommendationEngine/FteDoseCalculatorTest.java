package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class FteDoseCalculatorTest {
    @Test void preservesBoronDoseBelowZincLimit() {
        var result = FteDoseCalculator.calculate(1d, 2d, 10d, 7.5d);
        assertThat(result.productDoseKgHa()).isEqualTo(50d);
        assertThat(result.suppliedZincKgHa()).isEqualTo(5d);
        assertThat(result.limitedByZinc()).isFalse();
    }

    @Test void limitsDoseByZinc() {
        var result = FteDoseCalculator.calculate(2d, 2d, 10d, 7.5d);
        assertThat(result.productDoseKgHa()).isEqualTo(75d);
        assertThat(result.suppliedZincKgHa()).isEqualTo(7.5d);
        assertThat(result.limitedByZinc()).isTrue();
    }

    @Test void absentOrInvalidInputsDoNotManufactureDose() {
        assertThat(FteDoseCalculator.calculate(0d, 2d, 10d, 7.5d).available()).isFalse();
        assertThat(FteDoseCalculator.calculate(1d, 0d, 10d, 7.5d).available()).isFalse();
        assertThat(FteDoseCalculator.calculate(1d, 2d, 0d, 7.5d).available()).isFalse();
    }
}
