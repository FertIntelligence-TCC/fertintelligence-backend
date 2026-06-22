package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.service.implementation.FertAiClient;
import com.migueltcc.fertintelligence.service.implementation.RecommendationNarrativeService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecommendationNarrativeServiceTest {

    private final FertAiClient fertAiClient = mock(FertAiClient.class);
    private final RecommendationNarrativeService recommendationNarrativeService = new RecommendationNarrativeService(fertAiClient);

    @Test
    void improveNarrativeRetornaImprovedReportQuandoFertAiRespondeComSucesso() {
        String report = "Aplicar 120 kg/ha de N e 45.5 kg/ha de K2O.";
        String improvedReport = "Narrativa melhorada mantendo 120 kg/ha e 45.5 kg/ha.";
        when(fertAiClient.improveNarrative(report)).thenReturn(improvedReport);

        String improved = recommendationNarrativeService.improveNarrative(report);

        assertEquals(improvedReport, improved);
        verify(fertAiClient).improveNarrative(report);
    }

    @Test
    void improveNarrativeRetornaTechnicalReportQuandoFertAiFalha() {
        String report = "Aplicar 120 kg/ha de N e 45.5 kg/ha de K2O.";
        when(fertAiClient.improveNarrative(report)).thenThrow(new RuntimeException("Fert-AI indisponível"));

        String improved = recommendationNarrativeService.improveNarrative(report);

        assertEquals(report, improved);
        verify(fertAiClient).improveNarrative(report);
    }
}
