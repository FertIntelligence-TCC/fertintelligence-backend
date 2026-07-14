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
        assertThat(result.technicalWarning()).contains("adicional negativo");
        assertThat(result.scenarios()).allSatisfy(scenario -> {
            assertThat(scenario.additionalCalcium()).isNotNull();
            assertThat(scenario.additionalMagnesium()).isNotNull();
            assertThat(scenario.totalCarbonates()).isNotNull();
        });
    }

    @Test
    void evaluatesTargetsIndependentlyWhenOnlyRatioThreeIsFeasible() {
        var result = calculator.calculate(1d, 25d, 10d);

        assertThat(result.available()).isTrue();
        assertThat(result.scenarios()).extracting(CalciumMagnesiumBalanceCalculator.CalciumMagnesiumBalanceScenario::available)
                .containsExactly(true, false);
        assertThat(result.minimumCalciumOxidePercent()).isEqualTo(result.scenarios().get(0).calciumOxidePercent());
        assertThat(result.technicalWarning()).contains("relação 4:1");
    }

    @Test
    void evaluatesTargetsIndependentlyWhenOnlyRatioFourIsFeasible() {
        var result = calculator.calculate(1d, 50d, 5d);

        assertThat(result.available()).isTrue();
        assertThat(result.scenarios()).extracting(CalciumMagnesiumBalanceCalculator.CalciumMagnesiumBalanceScenario::available)
                .containsExactly(false, true);
        assertThat(result.maximumMagnesiumOxidePercent()).isEqualTo(result.scenarios().get(1).magnesiumOxidePercent());
        assertThat(result.technicalWarning()).contains("relação 3:1");
    }

    @Test
    void victoriaEquivalentKeepsAuditableNegativeAdditionsWithoutFabricatingComposition() {
        var result = calculator.calculate(1.4d, 20.6d, 20d);

        assertThat(result.currentRatio()).isCloseTo(1.03d, within(0.0001d));
        assertThat(result.available()).isFalse();
        assertThat(result.scenarios()).hasSize(2).allMatch(scenario -> !scenario.available());
        assertThat(result.scenarios().get(0).additionalCalcium()).isCloseTo(20.35d, within(1e-12));
        assertThat(result.scenarios().get(0).additionalMagnesium()).isCloseTo(-6.35d, within(1e-12));
        assertThat(result.scenarios().get(1).additionalCalcium()).isCloseTo(23.08d, within(1e-12));
        assertThat(result.scenarios().get(1).additionalMagnesium()).isCloseTo(-9.08d, within(1e-12));
        assertThat(result.scenarios()).allSatisfy(scenario -> {
            assertThat(scenario.calciumOxidePercent()).isNull();
            assertThat(scenario.magnesiumOxidePercent()).isNull();
            assertThat(scenario.technicalWarning()).doesNotContain("NaN", "Infinity");
        });
    }

    private void assertUnavailable(
            CalciumMagnesiumBalanceCalculator.CalciumMagnesiumBalanceRangeResult result,
            String warningFragment) {
        assertThat(result.available()).isFalse();
        assertThat(result.technicalWarning()).contains(warningFragment);
        assertThat(result.scenarios()).isEmpty();
    }
}
