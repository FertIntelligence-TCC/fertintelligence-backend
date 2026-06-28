package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class FormulatedFertilizerRatioService {

    private static final double COMPARISON_TOLERANCE = 0.01d;

    public RatioCalculationResult calculateRecommendedRatio(Double requiredN,
                                                            Double requiredP2O5,
                                                            Double requiredK2O) {
        return calculateRatio(requiredN, requiredP2O5, requiredK2O, "Dose recomendada");
    }

    public RatioCalculationResult calculateFormulatedRatio(FormulatedMineralFertilizerModel fertilizer) {
        if (fertilizer == null) {
            return new RatioCalculationResult(null, "Adubo formulado ausente; relacao N-P2O5-K2O nao calculada.");
        }
        return calculateFormulatedRatio(fertilizer.getN(), fertilizer.getP2O5(), fertilizer.getK2O());
    }

    public RatioCalculationResult calculateFormulatedRatio(Double nPercent,
                                                           Double p2o5Percent,
                                                           Double k2oPercent) {
        return calculateRatio(nPercent, p2o5Percent, k2oPercent, "Concentracao do adubo formulado");
    }

    public boolean hasCompleteRatioMatch(NPKrelation recommendedRatio, NPKrelation formulatedRatio) {
        return recommendedRatio != null
                && formulatedRatio != null
                && approximatelyEquals(recommendedRatio.getN(), formulatedRatio.getN())
                && approximatelyEquals(recommendedRatio.getP(), formulatedRatio.getP())
                && approximatelyEquals(recommendedRatio.getK(), formulatedRatio.getK());
    }

    public Double calculateRatioSum(NPKrelation ratio) {
        if (ratio == null) {
            return null;
        }
        return round2(ratio.getN() + ratio.getP() + ratio.getK());
    }

    public Double calculateFormulatedConcentrationSum(FormulatedMineralFertilizerModel fertilizer) {
        if (fertilizer == null) {
            return null;
        }
        return calculateFormulatedConcentrationSum(fertilizer.getN(), fertilizer.getP2O5(), fertilizer.getK2O());
    }

    public Double calculateFormulatedConcentrationSum(Double nPercent,
                                                      Double p2o5Percent,
                                                      Double k2oPercent) {
        return round2(normalize(nPercent) + normalize(p2o5Percent) + normalize(k2oPercent));
    }

    private RatioCalculationResult calculateRatio(Double n, Double p2o5, Double k2o, String context) {
        List<String> warnings = invalidValueWarnings(n, p2o5, k2o, context);
        double normalizedN = normalize(n);
        double normalizedP2O5 = normalize(p2o5);
        double normalizedK2O = normalize(k2o);
        double minimum = smallestPositiveValue(normalizedN, normalizedP2O5, normalizedK2O);

        if (minimum <= 0d) {
            warnings.add(context + " sem valor positivo de N, P2O5 ou K2O; relacao nao calculada.");
            return new RatioCalculationResult(null, String.join(" ", warnings));
        }

        return new RatioCalculationResult(
                new NPKrelation(
                        divideByMinimum(normalizedN, minimum),
                        divideByMinimum(normalizedP2O5, minimum),
                        divideByMinimum(normalizedK2O, minimum)),
                warnings.isEmpty() ? null : String.join(" ", warnings)
        );
    }

    private List<String> invalidValueWarnings(Double n, Double p2o5, Double k2o, String context) {
        List<String> warnings = new ArrayList<>();
        addInvalidValueWarning(warnings, n, "N", context);
        addInvalidValueWarning(warnings, p2o5, "P2O5", context);
        addInvalidValueWarning(warnings, k2o, "K2O", context);
        return warnings;
    }

    private void addInvalidValueWarning(List<String> warnings, Double value, String nutrient, String context) {
        if (value == null) {
            warnings.add(context + " de " + nutrient + " ausente; valor tratado como zero.");
        } else if (!Double.isFinite(value) || value < 0d) {
            warnings.add(context + " de " + nutrient + " invalida; valor tratado como zero.");
        }
    }

    private double smallestPositiveValue(double n, double p2o5, double k2o) {
        double minimum = Double.MAX_VALUE;
        if (n > 0d) minimum = Math.min(minimum, n);
        if (p2o5 > 0d) minimum = Math.min(minimum, p2o5);
        if (k2o > 0d) minimum = Math.min(minimum, k2o);
        return minimum == Double.MAX_VALUE ? 0d : minimum;
    }

    private double divideByMinimum(double value, double minimum) {
        return round2(value / minimum);
    }

    private boolean approximatelyEquals(double first, double second) {
        return Math.abs(first - second) <= COMPARISON_TOLERANCE;
    }

    private double normalize(Double value) {
        if (value == null || !Double.isFinite(value) || value < 0d) {
            return 0d;
        }
        return value;
    }

    private double round2(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .doubleValue();
    }

    public record RatioCalculationResult(NPKrelation ratio, String technicalMessage) {

        public boolean calculated() {
            return ratio != null;
        }
    }
}
