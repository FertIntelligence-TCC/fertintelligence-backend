package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class CalciumMagnesiumBalanceCalculatorTest {

    private final CalciumMagnesiumBalanceCalculator calculator = new CalciumMagnesiumBalanceCalculator();

    @Test
    void calculatesAuditableRangeForRatiosThreeAndFour() {
        var result = calculator.calculate(4d, 20d, 10d);

        assertThat(result.available()).isTrue();
        assertThat(result.currentRatio()).isEqualTo(2d);
        assertThat(result.scenarios()).hasSize(2);

        var ratioThree = result.scenarios().get(0);
        assertThat(ratioThree.desiredRatio()).isEqualTo(3d);
        assertThat(ratioThree.additionalCalcium()).isEqualTo(32.5d);
        assertThat(ratioThree.additionalMagnesium()).isEqualTo(7.5d);
        assertThat(ratioThree.calciumCarbonate()).isEqualTo(1625d);
        assertThat(ratioThree.magnesiumCarbonate()).isEqualTo(316.125d);
        assertThat(ratioThree.totalCarbonates()).isEqualTo(1941.125d);
        assertThat(ratioThree.calciumOxidePercent()).isCloseTo(46.880d, within(0.001d));
        assertThat(ratioThree.magnesiumOxidePercent()).isCloseTo(7.785d, within(0.001d));
        assertThat(ratioThree.limestoneClassification()).isEqualTo("Calcário dolomítico");

        var ratioFour = result.scenarios().get(1);
        assertThat(ratioFour.desiredRatio()).isEqualTo(4d);
        assertThat(ratioFour.additionalCalcium()).isEqualTo(36d);
        assertThat(ratioFour.additionalMagnesium()).isEqualTo(4d);
        assertThat(ratioFour.calciumCarbonate()).isEqualTo(1800d);
        assertThat(ratioFour.magnesiumCarbonate()).isEqualTo(168.6d);
        assertThat(ratioFour.totalCarbonates()).isEqualTo(1968.6d);
        assertThat(ratioFour.calciumOxidePercent()).isCloseTo(51.204d, within(0.001d));
        assertThat(ratioFour.magnesiumOxidePercent()).isCloseTo(4.094d, within(0.001d));
        assertThat(ratioFour.limestoneClassification()).isEqualTo("Calcário calcítico");

        assertThat(result.minimumCalciumOxidePercent()).isEqualTo(ratioThree.calciumOxidePercent());
        assertThat(result.maximumCalciumOxidePercent()).isEqualTo(ratioFour.calciumOxidePercent());
        assertThat(result.minimumMagnesiumOxidePercent()).isEqualTo(ratioFour.magnesiumOxidePercent());
        assertThat(result.maximumMagnesiumOxidePercent()).isEqualTo(ratioThree.magnesiumOxidePercent());
    }

    @Test
    void preservesTargetRatioAndTheoreticalLimingIdentity() {
        var result = calculator.calculate(4d, 20d, 10d);

        for (var scenario : result.scenarios()) {
            double finalRatio = (20d + scenario.additionalCalcium())
                    / (10d + scenario.additionalMagnesium());
            assertThat(finalRatio).isCloseTo(scenario.desiredRatio(), within(1e-12));
            assertThat(scenario.additionalCalcium() + scenario.additionalMagnesium())
                    .isCloseTo(40d, within(1e-12));
        }
    }

    @Test
    void classifiesExactlyFivePercentAsDolomitic() {
        assertThat(calculator.classify(4.999999d)).isEqualTo("Calcário calcítico");
        assertThat(calculator.classify(5d)).isEqualTo("Calcário dolomítico");
    }

    @Test
    void reportsCurrentRatioAsUnavailableWhenMagnesiumIsZero() {
        var result = calculator.calculate(4d, 20d, 0d);

        assertThat(result.available()).isTrue();
        assertThat(result.currentRatio()).isNull();
    }

    @Test
    void rejectsMissingZeroNegativeAndNonFiniteInputs() {
        assertUnavailable(calculator.calculate(null, 20d, 10d), "necessidade de calagem");
        assertUnavailable(calculator.calculate(0d, 20d, 10d), "necessidade de calagem");
        assertUnavailable(calculator.calculate(-1d, 20d, 10d), "necessidade de calagem");
        assertUnavailable(calculator.calculate(4d, null, 10d), "teor de Ca");
        assertUnavailable(calculator.calculate(4d, -1d, 10d), "teor de Ca");
        assertUnavailable(calculator.calculate(4d, 20d, null), "teor de Mg");
        assertUnavailable(calculator.calculate(4d, 20d, -1d), "teor de Mg");
        assertUnavailable(calculator.calculate(Double.POSITIVE_INFINITY, 20d, 10d), "necessidade de calagem");
    }

    @Test
    void rejectsScenarioThatWouldRequireNegativeAdditionalBase() {
        var result = calculator.calculate(1d, 100d, 1d);

        assertThat(result.available()).isFalse();
        assertThat(result.scenarios()).hasSize(2).allMatch(scenario -> !scenario.available());
        assertThat(result.technicalWarning()).contains("exigiria redução do teor atual de Ca");
    }

    @Test
    void realVictoriaValuesReturnMaximumRatioWithZeroMagnesium() {
        var result = calculator.calculate(1.4d, 7.5d, 7.3d);

        assertThat(result.currentRatio()).isCloseTo(1.0273972603d, within(1e-10));
        assertThat(result.available()).isTrue();
        assertThat(result.scenarios()).hasSize(2).allMatch(CalciumMagnesiumBalanceCalculator.CalciumMagnesiumBalanceScenario::available);
        assertThat(result.scenarios()).allSatisfy(scenario -> {
            assertThat(scenario.limitedByZeroMagnesium()).isTrue();
            assertThat(scenario.achievableRatio()).isCloseTo(21.5d / 7.3d, within(1e-12));
            assertThat(scenario.additionalCalcium()).isEqualTo(14d);
            assertThat(scenario.additionalMagnesium()).isZero();
            assertThat(scenario.calciumOxidePercent()).isEqualTo(56d);
            assertThat(scenario.magnesiumOxidePercent()).isZero();
            assertThat(scenario.technicalWarning()).contains("maior relação estimada alcançável");
            assertThat(scenario.technicalWarning()).doesNotContain("NaN", "Infinity");
        });
    }

    @Test
    void preservesViableThreeToOneAndLimitsOnlyFourToOne() {
        var result = calculator.calculate(1d, 1d, 3d);

        assertThat(result.scenarios().get(0).limitedByZeroMagnesium()).isFalse();
        assertThat(result.scenarios().get(0).achievableRatio()).isEqualTo(3d);
        assertThat(result.scenarios().get(1).limitedByZeroMagnesium()).isTrue();
        assertThat(result.scenarios().get(1).achievableRatio()).isCloseTo(11d / 3d, within(1e-12));
        assertThat(result.scenarios().get(1).magnesiumOxidePercent()).isZero();
    }

    private void assertUnavailable(
            CalciumMagnesiumBalanceCalculator.CalciumMagnesiumBalanceRangeResult result,
            String warningFragment) {
        assertThat(result.available()).isFalse();
        assertThat(result.technicalWarning()).contains(warningFragment);
        assertThat(result.scenarios()).isEmpty();
    }
}
