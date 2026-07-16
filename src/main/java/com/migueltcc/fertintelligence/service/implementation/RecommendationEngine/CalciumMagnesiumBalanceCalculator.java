package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class CalciumMagnesiumBalanceCalculator {

    static final double LOWER_TARGET_RATIO = 3d;
    static final double UPPER_TARGET_RATIO = 4d;

    public CalciumMagnesiumBalanceRangeResult calculate(Double theoreticalLimingRequirement,
                                                        Double calcium,
                                                        Double magnesium) {
        String invalidInput = validateInputs(theoreticalLimingRequirement, calcium, magnesium);
        Double currentRatio = currentRatio(calcium, magnesium);
        if (invalidInput != null) {
            return CalciumMagnesiumBalanceRangeResult.unavailable(currentRatio, invalidInput);
        }

        CalciumMagnesiumBalanceScenario lower = calculateScenario(
                theoreticalLimingRequirement, calcium, magnesium, LOWER_TARGET_RATIO);
        CalciumMagnesiumBalanceScenario upper = calculateScenario(
                theoreticalLimingRequirement, calcium, magnesium, UPPER_TARGET_RATIO);
        List<CalciumMagnesiumBalanceScenario> scenarios = List.of(lower, upper);
        List<CalciumMagnesiumBalanceScenario> availableScenarios = scenarios.stream()
                .filter(CalciumMagnesiumBalanceScenario::available)
                .toList();
        String warning = scenarios.stream()
                .filter(scenario -> !scenario.available())
                .map(CalciumMagnesiumBalanceScenario::technicalWarning)
                .reduce((first, second) -> first + " " + second)
                .orElse(null);

        return new CalciumMagnesiumBalanceRangeResult(
                !availableScenarios.isEmpty(),
                currentRatio,
                theoreticalLimingRequirement,
                calcium,
                magnesium,
                scenarios,
                minimum(availableScenarios.stream().map(CalciumMagnesiumBalanceScenario::calciumOxidePercent)),
                maximum(availableScenarios.stream().map(CalciumMagnesiumBalanceScenario::calciumOxidePercent)),
                minimum(availableScenarios.stream().map(CalciumMagnesiumBalanceScenario::magnesiumOxidePercent)),
                maximum(availableScenarios.stream().map(CalciumMagnesiumBalanceScenario::magnesiumOxidePercent)),
                warning);
    }

    private Double minimum(Stream<Double> values) {
        return values.min(Double::compareTo).orElse(null);
    }

    private Double maximum(Stream<Double> values) {
        return values.max(Double::compareTo).orElse(null);
    }

    private CalciumMagnesiumBalanceScenario calculateScenario(double theoreticalLimingRequirement,
                                                              double calcium,
                                                              double magnesium,
                                                              double desiredRatio) {
        double totalAdditionalBases = 10d * theoreticalLimingRequirement;
        double denominator = desiredRatio + 1d;
        double additionalCalcium = (desiredRatio * totalAdditionalBases
                + desiredRatio * magnesium - calcium) / denominator;
        double additionalMagnesium = (totalAdditionalBases
                - desiredRatio * magnesium + calcium) / denominator;

        if (!finiteNonNegative(additionalCalcium) || !finiteNonNegative(additionalMagnesium)) {
            double calciumCarbonate = 50d * additionalCalcium;
            double magnesiumCarbonate = 42.15d * additionalMagnesium;
            String reason;
            if (Double.isFinite(additionalMagnesium) && additionalMagnesium < 0d) {
                reason = "Não é possível atingir a relação Ca/Mg de " + (int) desiredRatio
                        + ":1 apenas pela adição do calcário, porque esse alvo exigiria redução do teor atual de Mg no solo";
            } else if (Double.isFinite(additionalCalcium) && additionalCalcium < 0d) {
                reason = "Não é possível atingir a relação Ca/Mg de " + (int) desiredRatio
                        + ":1 apenas pela adição do calcário, porque esse alvo exigiria redução do teor atual de Ca no solo";
            } else {
                reason = "Composição teórica indisponível: a relação " + (int) desiredRatio
                        + ":1 produziu adicional não finito de Ca ou Mg";
            }
            return CalciumMagnesiumBalanceScenario.unavailable(desiredRatio,
                    additionalCalcium, additionalMagnesium, calciumCarbonate, magnesiumCarbonate,
                    calciumCarbonate + magnesiumCarbonate,
                    reason + " (T=" + totalAdditionalBases + ", DCa=" + additionalCalcium
                            + ", DMg=" + additionalMagnesium + ").");
        }

        double calciumCarbonate = 50d * additionalCalcium;
        double magnesiumCarbonate = 42.15d * additionalMagnesium;
        double totalCarbonates = calciumCarbonate + magnesiumCarbonate;
        if (!Double.isFinite(totalCarbonates) || totalCarbonates <= 0d) {
            return CalciumMagnesiumBalanceScenario.unavailable(desiredRatio,
                    "Composição teórica indisponível: o total calculado de carbonatos é nulo, negativo ou não finito.");
        }

        double calciumOxidePercent = calciumCarbonate * 56d / totalCarbonates;
        double magnesiumOxidePercent = magnesiumCarbonate * 47.8d / totalCarbonates;
        if (!validPercent(calciumOxidePercent) || !validPercent(magnesiumOxidePercent)) {
            return CalciumMagnesiumBalanceScenario.unavailable(desiredRatio,
                    "Composição teórica indisponível: o teor calculado de CaO ou MgO está fora do intervalo percentual válido.");
        }

        return new CalciumMagnesiumBalanceScenario(
                true,
                desiredRatio,
                additionalCalcium,
                additionalMagnesium,
                calciumCarbonate,
                magnesiumCarbonate,
                totalCarbonates,
                calciumOxidePercent,
                magnesiumOxidePercent,
                classify(magnesiumOxidePercent),
                null);
    }

    private String validateInputs(Double theoreticalLimingRequirement, Double calcium, Double magnesium) {
        if (!finitePositive(theoreticalLimingRequirement)) {
            return "Composição teórica indisponível: a necessidade de calagem teórica com PRNT 100% deve ser positiva.";
        }
        if (!finiteNonNegative(calcium)) {
            return "Composição teórica indisponível: o teor de Ca da camada 0-20 cm está ausente, negativo ou não finito.";
        }
        if (!finiteNonNegative(magnesium)) {
            return "Composição teórica indisponível: o teor de Mg da camada 0-20 cm está ausente, negativo ou não finito.";
        }
        return null;
    }

    private Double currentRatio(Double calcium, Double magnesium) {
        if (!finiteNonNegative(calcium) || !finitePositive(magnesium)) {
            return null;
        }
        double ratio = calcium / magnesium;
        return Double.isFinite(ratio) ? ratio : null;
    }

    String classify(double magnesiumOxidePercent) {
        return magnesiumOxidePercent < 5d ? "Calcário calcítico" : "Calcário dolomítico";
    }

    private boolean validPercent(double value) {
        return Double.isFinite(value) && value >= 0d && value <= 100d;
    }

    private boolean finitePositive(Double value) {
        return value != null && Double.isFinite(value) && value > 0d;
    }

    private boolean finiteNonNegative(Double value) {
        return value != null && Double.isFinite(value) && value >= 0d;
    }

    public record CalciumMagnesiumBalanceRangeResult(
            boolean available,
            Double currentRatio,
            Double theoreticalLimingRequirement,
            Double currentCalcium,
            Double currentMagnesium,
            List<CalciumMagnesiumBalanceScenario> scenarios,
            Double minimumCalciumOxidePercent,
            Double maximumCalciumOxidePercent,
            Double minimumMagnesiumOxidePercent,
            Double maximumMagnesiumOxidePercent,
            String technicalWarning) {

        static CalciumMagnesiumBalanceRangeResult unavailable(Double currentRatio, String warning) {
            return new CalciumMagnesiumBalanceRangeResult(
                    false, currentRatio, null, null, null, List.of(), null, null, null, null, warning);
        }
    }

    public record CalciumMagnesiumBalanceScenario(
            boolean available,
            double desiredRatio,
            Double additionalCalcium,
            Double additionalMagnesium,
            Double calciumCarbonate,
            Double magnesiumCarbonate,
            Double totalCarbonates,
            Double calciumOxidePercent,
            Double magnesiumOxidePercent,
            String limestoneClassification,
            String technicalWarning) {

        static CalciumMagnesiumBalanceScenario unavailable(double desiredRatio, String warning) {
            return new CalciumMagnesiumBalanceScenario(
                    false, desiredRatio, null, null, null, null, null, null, null, null, warning);
        }

        static CalciumMagnesiumBalanceScenario unavailable(double desiredRatio,
                                                            Double additionalCalcium,
                                                            Double additionalMagnesium,
                                                            Double calciumCarbonate,
                                                            Double magnesiumCarbonate,
                                                            Double totalCarbonates,
                                                            String warning) {
            return new CalciumMagnesiumBalanceScenario(
                    false, desiredRatio, additionalCalcium, additionalMagnesium,
                    calciumCarbonate, magnesiumCarbonate, totalCarbonates,
                    null, null, null, warning);
        }
    }
}
