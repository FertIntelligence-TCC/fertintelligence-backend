package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FteBaseNutrientSelectorTest {
    @Test void lowBoronAlwaysUsesBoronWhenDoseIsValid() {
        assertThat(FteBaseNutrientSelector.select("Baixo", 2d, "Baixo", 3d)).contains(AppliedMicronutrient.B);
        assertThat(FteBaseNutrientSelector.select("Muito baixo", 2d, "Médio", 3d)).contains(AppliedMicronutrient.B);
    }

    @Test void mediumBoronUsesLowZincOrMediumBoron() {
        assertThat(FteBaseNutrientSelector.select("Médio", 2d, "Baixo", 3d)).contains(AppliedMicronutrient.Zn);
        assertThat(FteBaseNutrientSelector.select("Médio", 2d, "Médio", 3d)).contains(AppliedMicronutrient.B);
    }

    @Test void highBoronUsesAnalyzedZinc() {
        assertThat(FteBaseNutrientSelector.select("Alto", 2d, "Médio", 3d)).contains(AppliedMicronutrient.Zn);
        assertThat(FteBaseNutrientSelector.select("Muito alto", 2d, "Alto", 3d)).contains(AppliedMicronutrient.Zn);
    }

    @Test void missingClassificationOrInvalidDoseDoesNotManufactureBase() {
        assertThat(FteBaseNutrientSelector.select(null, 2d, "Baixo", 3d)).isEmpty();
        assertThat(FteBaseNutrientSelector.select("Médio", 2d, null, 3d)).isEmpty();
        assertThat(FteBaseNutrientSelector.select("Baixo", 0d, "Baixo", 3d)).isEmpty();
        assertThat(FteBaseNutrientSelector.select("Alto", 2d, "Médio", null)).isEmpty();
    }
}
