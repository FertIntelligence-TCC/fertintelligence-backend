package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.service.implementation.RecommendationNarrativeService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationNarrativeServiceTest {

    private final RecommendationNarrativeService recommendationNarrativeService = new RecommendationNarrativeService();

    @Test
    void improveNarrativePreservaNumeros() {
        String report = "Aplicar 120 kg/ha de N e 45.5 kg/ha de K2O.";

        String improved = recommendationNarrativeService.improveNarrative(report);

        assertTrue(improved.contains("120 kg/ha"));
        assertTrue(improved.contains("45.5 kg/ha"));
        assertTrue(improved.contains("Texto revisado para maior clareza. Os cálculos técnicos permanecem inalterados."));
    }
}
