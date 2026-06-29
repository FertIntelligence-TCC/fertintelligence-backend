package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.CropSpacingCalculationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.DirectRecommendationReportService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DirectRecommendationReportServiceTest {

    @Test
    void resolveDoseUnitMetadataReturnsInsufficientDataForLegacyRecommendationWithoutResolvableCrop() {
        DirectRecommendationReportService service = newService();

        DirectRecommendationReportService.DirectDoseUnitMetadata metadata =
                service.resolveDoseUnitMetadata(null);

        assertThat(metadata.doseUnitMode()).isEqualTo("INSUFFICIENT_DATA");
        assertThat(metadata.doseUnitLabel()).isNull();
        assertThat(metadata.applicableDoseColumn()).isNull();
    }

    @Test
    void buildKeepsApplicableLinearUnitWhenSpacingDataExists() {
        DirectRecommendationReportService service = newService();
        RecommendationModel recommendation = recommendationWithTechnicalReport();
        CropModel crop = CropModel.builder()
                .spacingMode(CropSpacingMode.PLANTS_PER_LINEAR_METER)
                .distanceBetweenLines(0.5d)
                .plantsPerMeter(10d)
                .build();

        String report = service.build(recommendation, crop);

        assertThat(report).contains("| Adubação | Adubos simples/formulados | kg/ha | g/m linear |");
        assertThat(report).contains("| Plantio | Ureia | 100 kg/ha | 5.00 |");
        assertThat(report).doesNotStartWith("#");
        assertThat(report).doesNotContain("## ");
        assertThat(report).doesNotContain("### ");
        assertThat(report).doesNotContain("| Adubação | Adubos simples/formulados | kg/ha | g/m linear | g/cova |");
        assertThat(report).doesNotContain("g/cova");
    }

    @Test
    void buildKeepsApplicablePitUnitWhenSpacingDataExists() {
        DirectRecommendationReportService service = newService();
        RecommendationModel recommendation = recommendationWithTechnicalReport();
        CropModel crop = CropModel.builder()
                .spacingMode(CropSpacingMode.PIT)
                .distanceBetweenLines(0.5d)
                .distanceBetweenPits(0.25d)
                .plantsPerPit(2d)
                .build();

        String report = service.build(recommendation, crop);

        assertThat(report).contains("| Adubação | Adubos simples/formulados | kg/ha | g/cova |");
        assertThat(report).contains("| Plantio | Ureia | 100 kg/ha | 1.25 |");
        assertThat(report).doesNotContain("| Adubação | Adubos simples/formulados | kg/ha | g/m linear | g/cova |");
        assertThat(report).doesNotContain("g/m linear");
    }

    @Test
    void buildUsesHonestSpacingWarningWhenApplicableUnitIsUnknown() {
        DirectRecommendationReportService service = newService();
        RecommendationModel recommendation = recommendationWithTechnicalReport();

        String report = service.build(recommendation, null);

        assertThat(report).contains("| Adubação | Adubos simples/formulados | kg/ha | Conversão por espaçamento |");
        assertThat(report).contains("| Plantio | Ureia | 100 kg/ha | Não calculado por falta de dados. |");
        assertThat(report).contains("Não foi possível determinar uma unidade aplicável com segurança");
        assertThat(report).doesNotContain("g/m linear");
        assertThat(report).doesNotContain("g/cova");
    }

    @Test
    void buildDoesNotGenerateMapObservationSection() {
        DirectRecommendationReportService service = newService();
        RecommendationModel recommendation = recommendationWithTechnicalReportContainingMap();

        String report = service.build(recommendation, null);

        assertThat(report).doesNotContain("Observação sobre MAP");
        assertThat(report).doesNotContain("Observações sobre MAP");
        assertThat(report).doesNotContain("MAP aparece nas fontes/fertilizantes recomendados");
        assertThat(report).doesNotContain("Observações finais");
        assertThat(report).contains("Observações sobre adubação");
    }

    @Test
    void buildUsesPersistedStructuredLinesWhenTheyExist() {
        DirectRecommendationMicronutrientFertilizerLineRepository micronutrientRepository =
                mock(DirectRecommendationMicronutrientFertilizerLineRepository.class);
        DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingRepository =
                mock(DirectRecommendationPlantingFormulatedFertilizerLineRepository.class);
        DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageRepository =
                mock(DirectRecommendationCoverageFormulatedFertilizerLineRepository.class);
        DirectRecommendationReportService service = new DirectRecommendationReportService(
                new CropSpacingCalculationService(),
                null,
                null,
                null,
                null,
                micronutrientRepository,
                plantingRepository,
                coverageRepository
        );
        RecommendationModel recommendation = recommendationWithTechnicalReport();
        recommendation.setId(1L);
        DirectRecommendationModel directRecommendation = DirectRecommendationModel.builder()
                .id(2L)
                .recommendation(recommendation)
                .documentName(DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport("direta")
                .build();
        recommendation.setDirectRecommendation(directRecommendation);
        when(micronutrientRepository.findAllByDirectRecommendationOrderByIdAsc(directRecommendation))
                .thenReturn(List.of(DirectRecommendationMicronutrientFertilizerLineModel.builder()
                        .micronutrient(AppliedMicronutrient.B)
                        .fertilizerName("Borax")
                        .micronutrientDoseKgHa(1.2)
                        .fertilizerDoseKgHa(10.0)
                        .doseUnitMode("LINEAR_METER")
                        .doseUnitLabel("g/m linear")
                        .gramsPerLinearMeter(0.5)
                        .technicalObservation("Dose calculada por B.")
                        .build()));
        when(plantingRepository.findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of(DirectRecommendationPlantingFormulatedFertilizerLineModel.builder()
                        .phase("Plantio")
                        .fertilizerName("04-14-08")
                        .relationUsed("1-3.5-2")
                        .doseKgHa(250.0)
                        .doseUnitMode("LINEAR_METER")
                        .doseUnitLabel("g/m linear")
                        .gramsPerLinearMeter(12.5)
                        .technicalObservation("Formulado de plantio selecionado.")
                        .build()));
        when(coverageRepository.findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of());

        String report = service.build(recommendation);

        assertThat(report).contains("| Micronutriente | Adubo sólido | Dose micronutriente | Dose adubo | g/m linear | Observação técnica |");
        assertThat(report).contains("| B | Borax | 1.20 kg/ha | 10.00 kg/ha | 0.50 | Dose calculada por B. |");
        assertThat(report).contains("| Adubação | Formulado | Relação N-P2O5-K2O | kg/ha | g/m linear | Observação técnica |");
        assertThat(report).contains("| Plantio | 04-14-08 | 1-3.5-2 | 250.00 kg/ha | 12.50 | Formulado de plantio selecionado. |");
        assertThat(report).doesNotContain("g/cova");
    }

    private DirectRecommendationReportService newService() {
        return new DirectRecommendationReportService(
                new CropSpacingCalculationService(),
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private RecommendationModel recommendationWithTechnicalReport() {
        return RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .cropYear(2026)
                .technicalReport("""
                        ## 10. Adubação de plantio

                        | Fase | Nutriente | Fertilizante | Quantidade |
                        |---|---|---|---:|
                        | Plantio | N | Ureia | 100 kg/ha |
                        """)
                .build();
    }

    private RecommendationModel recommendationWithTechnicalReportContainingMap() {
        return RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .cropYear(2026)
                .technicalReport("""
                        ## 10. Adubação de plantio

                        | Fase | Nutriente | Fertilizante | Quantidade |
                        |---|---|---|---:|
                        | Plantio | P2O5 | MAP | 100 kg/ha |

                        ## 14. Limitações e alertas

                        Dados conforme laudo técnico.
                        """)
                .build();
    }
}
