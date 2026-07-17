package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

final class CoverageAdjustmentCalculator {
    private CoverageAdjustmentCalculator() {}

    static double adjustedRequirement(double originalCoverageRequirement, double plantingBalance) {
        if (!Double.isFinite(originalCoverageRequirement) || !Double.isFinite(plantingBalance)) return 0d;
        return Math.max(0d, originalCoverageRequirement - plantingBalance);
    }
}
