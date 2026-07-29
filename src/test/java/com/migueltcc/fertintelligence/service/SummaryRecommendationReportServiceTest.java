package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.DirectRecommendationFertilizerResolver;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.SummaryRecommendationReportService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SummaryRecommendationReportServiceTest {

    @Test
    void buildUsesPersistedMicronutrientLinesFromDirectRecommendation() {
        DirectRecommendationMicronutrientFertilizerLineRepository lineRepository =
                mock(DirectRecommendationMicronutrientFertilizerLineRepository.class);
        SummaryRecommendationReportService service =
                new SummaryRecommendationReportService(null, lineRepository, fertilizerResolver());
        RecommendationModel recommendation = recommendationWithTechnicalReport();
        DirectRecommendationModel directRecommendation = DirectRecommendationModel.builder()
                .id(10L)
                .recommendation(recommendation)
                .documentName(DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport("direta")
                .build();
        recommendation.setDirectRecommendation(directRecommendation);
        when(lineRepository.findAllByDirectRecommendationOrderByIdAsc(directRecommendation))
                .thenReturn(List.of(DirectRecommendationMicronutrientFertilizerLineModel.builder()
                        .micronutrient(AppliedMicronutrient.B)
                        .micronutrientDoseKgHa(1.2)
                        .fertilizerDoseKgHa(10.91)
                        .doseUnitMode("LINEAR_METER")
                        .doseUnitLabel("g/m linear")
                        .gramsPerLinearMeter(0.55)
                        .technicalObservation("Dose calculada pela recomendação direta.")
                        .build()));

        String report = service.build(recommendation);

        assertThat(report).contains("Recomendação de micronutrientes");
        assertThat(report).contains("| Micronutriente | Dose micronutriente | Adubo sólido | Concentração | Dose adubo | Dose operacional | Observação técnica |");
        assertThat(report).contains("| B | 1.20 kg/ha | Borax | 11.00% | 10.91 kg/ha | 0.55 g/m linear | Dose calculada pela recomendação direta. |");
        assertThat(report).doesNotContain("- Boro: Não calculado por falta de dados.");
        assertThat(report)
                .doesNotContain("<!-- formato:")
                .doesNotContain("Endereço:")
                .doesNotContain("CEO:")
                .doesNotContain("Identificação\n\n");
    }

    @Test
    void buildUsesDefaultTechnicalObservationOnlyForMicronutrientsWithoutObservation() {
        DirectRecommendationMicronutrientFertilizerLineRepository lineRepository =
                mock(DirectRecommendationMicronutrientFertilizerLineRepository.class);
        SummaryRecommendationReportService service =
                new SummaryRecommendationReportService(null, lineRepository, fertilizerResolver());
        RecommendationModel recommendation = recommendationWithTechnicalReport();
        DirectRecommendationModel directRecommendation = DirectRecommendationModel.builder()
                .id(10L)
                .recommendation(recommendation)
                .documentName(DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport("direta")
                .build();
        recommendation.setDirectRecommendation(directRecommendation);
        when(lineRepository.findAllByDirectRecommendationOrderByIdAsc(directRecommendation))
                .thenReturn(List.of(
                        DirectRecommendationMicronutrientFertilizerLineModel.builder()
                                .micronutrient(AppliedMicronutrient.B)
                                .micronutrientDoseKgHa(1.2)
                                .fertilizerDoseKgHa(10.91)
                                .doseUnitMode("LINEAR_METER")
                                .doseUnitLabel("g/m linear")
                                .gramsPerLinearMeter(0.55)
                                .technicalObservation(" ")
                                .build(),
                        DirectRecommendationMicronutrientFertilizerLineModel.builder()
                                .micronutrient(AppliedMicronutrient.Zn)
                                .micronutrientDoseKgHa(2.0)
                                .fertilizerDoseKgHa(10.0)
                                .doseUnitMode("LINEAR_METER")
                                .doseUnitLabel("g/m linear")
                                .gramsPerLinearMeter(0.5)
                                .technicalObservation("Não informado.")
                                .build(),
                        DirectRecommendationMicronutrientFertilizerLineModel.builder()
                                .micronutrient(AppliedMicronutrient.Cu)
                                .micronutrientDoseKgHa(1.0)
                                .fertilizerDoseKgHa(4.0)
                                .doseUnitMode("LINEAR_METER")
                                .doseUnitLabel("g/m linear")
                                .gramsPerLinearMeter(0.2)
                                .technicalObservation("Aplicar conforme análise específica.")
                                .build()));

        String report = service.build(recommendation);

        assertThat(report).contains("| B | 1.20 kg/ha | Borax | 11.00% | 10.91 kg/ha | 0.55 g/m linear | Misturar com os demais adubos minerais no plantio. |");
        assertThat(report).contains("| Zn | 2.00 kg/ha | Sulfato de zinco | 20.00% | 10.00 kg/ha | 0.50 g/m linear | Misturar com os demais adubos minerais no plantio. |");
        assertThat(report).contains("| Cu | 1.00 kg/ha | Sulfato de cobre | 25.00% | 4.00 kg/ha | 0.20 g/m linear | Aplicar conforme análise específica. |");
        assertThat(report).doesNotContain("| Não informado. |");
    }

    @Test
    void buildKeepsHonestFallbackWhenNoPersistedMicronutrientLinesExist() {
        SummaryRecommendationReportService service = new SummaryRecommendationReportService(null, null, null);

        String report = service.build(recommendationWithTechnicalReport());

        assertThat(report).contains("- Micronutrientes: Não calculado por falta de dados.");
        assertThat(report).contains("não há linhas estruturadas de micronutrientes persistidas");
    }

    @Test
    void buildKeepsCorrectivePlantingAndCoverageRowsFromGeneralReport() {
        SummaryRecommendationReportService service = new SummaryRecommendationReportService(null, null, null);

        String report = service.build(RecommendationModel.builder()
                .technicalReport("""
                        ## 9. Adubação corretiva

                        | Nutriente/Atributo corrigido | Necessidade | Fonte sugerida | Dose | Memória de cálculo | Aviso técnico |
                        |---|---|---|---:|---|---|
                        | P corretivo | 90 kg/ha P2O5 | Superfosfato triplo | 180 kg/ha | Cálculo preservado. | Aplicar antes do plantio. |

                        ## 10. Adubação de plantio

                        | Fase da Cultura | Nutrientes Necessários | Sugestão de Adubo | Quantidade do Adubo | Época e Modo de Aplicação |
                        |---|---|---|---:|---|
                        | Plantio | N, P2O5 e K2O | 04-14-08 | 300 kg/ha | No sulco; saldo K2O: 0.00 kg/ha. |

                        ## 11. Adubação de cobertura

                        | Fase da Cultura | Nutrientes Necessários | Sugestão de Adubo | Quantidade do Adubo | Época e Modo de Aplicação |
                        |---|---|---|---:|---|
                        | Cobertura 1 | N | Ureia | 120 kg/ha | Aos 30 DAE; saldo N: 0.00 kg/ha. |
                        """)
                .build());

        assertThat(report).contains("Adubação corretiva do solo");
        assertThat(report).contains("| P corretivo | 90 kg/ha P2O5 | Superfosfato triplo | 180 kg/ha | Cálculo preservado. | Aplicar antes do plantio. |");
        assertThat(report).contains("Recomendações de N, P2O5 e K2O - plantio");
        assertThat(report).contains("| Plantio | N, P2O5 e K2O | 04-14-08 | 300 kg/ha | No sulco; saldo K2O: 0.00 kg/ha. |");
        assertThat(report).contains("Recomendações de N, P2O5 e K2O - cobertura");
        assertThat(report).contains("| Cobertura 1 | N | Ureia | 120 kg/ha | Aos 30 DAE; saldo N: 0.00 kg/ha. |");
        assertThat(report).doesNotContain("|---|---|---|---:|---|\n\n");
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

    private DirectRecommendationFertilizerResolver fertilizerResolver() {
        DirectRecommendationFertilizerResolver resolver = mock(DirectRecommendationFertilizerResolver.class);
        when(resolver.simple(null, AppliedMicronutrient.B)).thenReturn(
                new DirectRecommendationFertilizerResolver.SimpleMineralFertilizerData("Borax", 11d));
        when(resolver.simple(null, AppliedMicronutrient.Zn)).thenReturn(
                new DirectRecommendationFertilizerResolver.SimpleMineralFertilizerData("Sulfato de zinco", 20d));
        when(resolver.simple(null, AppliedMicronutrient.Cu)).thenReturn(
                new DirectRecommendationFertilizerResolver.SimpleMineralFertilizerData("Sulfato de cobre", 25d));
        return resolver;
    }
}
