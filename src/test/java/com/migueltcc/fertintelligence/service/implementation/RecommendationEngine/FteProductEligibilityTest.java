package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class FteProductEligibilityTest {
    @Test void onlyBr12IsEligibleForNewRecommendations() {
        assertThat(FteProductEligibility.isBr12EligibleForNewRecommendation("FTE BR-12")).isTrue();
        assertThat(FteProductEligibility.isBr12EligibleForNewRecommendation("FTE BR 12")).isTrue();
        assertThat(FteProductEligibility.isBr12EligibleForNewRecommendation("FTE BR-24")).isFalse();
    }

    @Test void historicalBr24RemainsRecognizable() {
        assertThat(FteProductEligibility.isHistoricalSupportedFte("FTE BR-24")).isTrue();
    }
}
