package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class FteDoseCalculator {

    private FteDoseCalculator() {
    }

    static FteDoseResult calculate(double requiredBaseKgHa,
                                   double baseConcentrationPercent,
                                   double zincConcentrationPercent,
                                   double maximumZincKgHa) {
        if (!Double.isFinite(requiredBaseKgHa) || requiredBaseKgHa <= 0d
                || !Double.isFinite(baseConcentrationPercent) || baseConcentrationPercent <= 0d
                || !Double.isFinite(zincConcentrationPercent) || zincConcentrationPercent <= 0d
                || !Double.isFinite(maximumZincKgHa) || maximumZincKgHa <= 0d) {
            return FteDoseResult.unavailable();
        }
        double theoreticalDose = 100d * requiredBaseKgHa / baseConcentrationPercent;
        double maximumDoseByZinc = 100d * maximumZincKgHa / zincConcentrationPercent;
        double productDose = round2(Math.min(theoreticalDose, maximumDoseByZinc));
        double suppliedZinc = round2(productDose * zincConcentrationPercent / 100d);
        return new FteDoseResult(productDose, round2(theoreticalDose), round2(maximumDoseByZinc), suppliedZinc,
                theoreticalDose > maximumDoseByZinc);
    }

    private static double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    record FteDoseResult(Double productDoseKgHa,
                         Double theoreticalDoseKgHa,
                         Double maximumDoseByZincKgHa,
                         Double suppliedZincKgHa,
                         boolean limitedByZinc) {
        static FteDoseResult unavailable() {
            return new FteDoseResult(null, null, null, null, false);
        }

        boolean available() {
            return productDoseKgHa != null;
        }
    }
}
