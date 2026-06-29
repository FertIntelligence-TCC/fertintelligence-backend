package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.SummaryRecommendationReportService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SummaryRecommendationReportServiceTest {

    @Test
    void buildUsesPersistedMicronutrientLinesFromDirectRecommendation() {
        SummaryRecommendationReportService service =
                new SummaryRecommendationReportService(null, null);
        RecommendationModel recommendation = recommendationWithTechnicalReport();
        DirectRecommendationModel directRecommendation = DirectRecommendationModel.builder()
                .id(10L)
                .recommendation(recommendation)
                .documentName(DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport("direta")
                .micronutrientFertilizerLines(List.of(DirectRecommendationMicronutrientFertilizerLineModel.builder()
                        .micronutrient(AppliedMicronutrient.B)
                        .micronutrientDoseKgHa(1.2)
                        .fertilizerName("Borax")
                        .micronutrientConcentrationPercent(11.0)
                        .fertilizerDoseKgHa(10.91)
                        .doseUnitMode("LINEAR_METER")
                        .doseUnitLabel("g/m linear")
                        .gramsPerLinearMeter(0.55)
                        .technicalObservation("Dose calculada pela recomendação direta.")
                        .build()))
                .build();
        recommendation.setDirectRecommendation(directRecommendation);

        String report = service.build(recommendation);

        assertThat(report).contains("Recomendação de micronutrientes");
        assertThat(report).contains("| Micronutriente | Dose micronutriente | Adubo sólido | Concentração | Dose adubo | Dose operacional | Observação técnica |");
        assertThat(report).contains("| B | 1.20 kg/ha | Borax | 11.00% | 10.91 kg/ha | 0.55 g/m linear | Dose calculada pela recomendação direta. |");
        assertThat(report).doesNotContain("- Boro: Não calculado por falta de dados.");
    }

    @Test
    void buildKeepsHonestFallbackWhenNoPersistedMicronutrientLinesExist() {
        SummaryRecommendationReportService service = new SummaryRecommendationReportService(null, null);

        String report = service.build(recommendationWithTechnicalReport());

        assertThat(report).contains("- Micronutrientes: Não calculado por falta de dados.");
        assertThat(report).contains("não há linhas estruturadas de micronutrientes persistidas");
    }

    private RecommendationModel recommendationWithTechnicalReport() {
        return RecommendationModel.builder()
                .technicalReport("""
                        ## 3. Diagnóstico químico

                        | Atributo | Valor | Classe |
                        |---|---:|---|
                        | pH | 5.5 | Médio |

                        ## 13. Fertilizantes recomendados

                        ### Fontes orgânicas, organominerais e micronutrientes

                        | Tipo | Nutriente | Fonte | Dose | Unidade | Observação |
                        |---|---|---|---:|---|---|
                        | Micronutriente | B | Borax | 10.91 | kg/ha | Calculado |
                        """)
                .build();
    }
}
