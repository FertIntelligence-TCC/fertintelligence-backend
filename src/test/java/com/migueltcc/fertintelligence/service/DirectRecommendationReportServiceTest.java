package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
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
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.DirectRecommendationFertilizerResolver;
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
    void buildDoesNotRenderEmptyOpportunityCostDecisionTable() {
        DirectRecommendationReportService service = newService();

        String report = service.build(RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .cropYear(2026)
                .technicalReport("""
                        ## 13.2. Comparativo de custo de oportunidade

                        | Categoria | Fertilizante | Dose | Preço comercial | Custo comercial | Razão PC/PO | Decisão | Observação |
                        |---|---|---:|---:|---:|---:|---|---|
                        """)
                .build(), null);

        assertThat(report).doesNotContain("| Categoria | Fertilizante | Dose | Preço comercial | Custo comercial | Razão PC/PO | Decisão | Observação |");
    }

    @Test
    void buildUsesPersistedStructuredLinesWhenTheyExist() {
        DirectRecommendationMicronutrientFertilizerLineRepository micronutrientRepository =
                mock(DirectRecommendationMicronutrientFertilizerLineRepository.class);
        DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingRepository =
                mock(DirectRecommendationPlantingFormulatedFertilizerLineRepository.class);
        DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageRepository =
                mock(DirectRecommendationCoverageFormulatedFertilizerLineRepository.class);
        DirectRecommendationFertilizerResolver fertilizerResolver = mock(DirectRecommendationFertilizerResolver.class);
        DirectRecommendationReportService service = new DirectRecommendationReportService(
                new CropSpacingCalculationService(),
                null,
                null,
                null,
                null,
                micronutrientRepository,
                plantingRepository,
                coverageRepository,
                fertilizerResolver
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
                        .relationUsed("1-3.5-2")
                        .doseKgHa(250.0)
                        .doseUnitMode("LINEAR_METER")
                        .doseUnitLabel("g/m linear")
                        .gramsPerLinearMeter(12.5)
                        .technicalObservation("Formulado de plantio selecionado.")
                        .build()));
        when(coverageRepository.findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of());
        when(fertilizerResolver.formulated(null)).thenReturn(
                new DirectRecommendationFertilizerResolver.FormulatedMineralFertilizerData("04-14-08", 4d, 14d, 8d));
        when(fertilizerResolver.simple(null, AppliedMicronutrient.B)).thenReturn(
                new DirectRecommendationFertilizerResolver.SimpleMineralFertilizerData("Borax", 11d));

        String report = service.build(recommendation);

        assertThat(report).contains("| Micronutriente | Adubo sólido | Dose micronutriente | Dose adubo | g/m linear | Observação técnica |");
        assertThat(report).contains("| B | Borax | 1.20 kg/ha | 10.00 kg/ha | 0.50 | Dose calculada por B. |");
        assertThat(report).contains("| Adubação | Formulado | Relação N-P2O5-K2O | kg/ha | g/m linear | Observação técnica |");
        assertThat(report).contains("| Plantio | 04-14-08 | 1-3.5-2 | 250.00 kg/ha | 12.50 | Formulado de plantio selecionado. |");
        assertThat(report).doesNotContain("g/cova");
    }

    @Test
    void buildIncludesSimpleCoverageOptionWhenStructuredPlantingExistsWithoutStructuredCoverage() {
        DirectRecommendationMicronutrientFertilizerLineRepository micronutrientRepository =
                mock(DirectRecommendationMicronutrientFertilizerLineRepository.class);
        DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingRepository =
                mock(DirectRecommendationPlantingFormulatedFertilizerLineRepository.class);
        DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageRepository =
                mock(DirectRecommendationCoverageFormulatedFertilizerLineRepository.class);
        DirectRecommendationFertilizerResolver fertilizerResolver = mock(DirectRecommendationFertilizerResolver.class);
        DirectRecommendationReportService service = new DirectRecommendationReportService(
                new CropSpacingCalculationService(),
                null,
                null,
                null,
                null,
                micronutrientRepository,
                plantingRepository,
                coverageRepository,
                fertilizerResolver
        );
        RecommendationModel recommendation = recommendationWithPlantingAndCoverageTechnicalReport();
        recommendation.setId(1L);
        DirectRecommendationModel directRecommendation = DirectRecommendationModel.builder()
                .id(2L)
                .recommendation(recommendation)
                .documentName(DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport("direta")
                .build();
        recommendation.setDirectRecommendation(directRecommendation);
        when(micronutrientRepository.findAllByDirectRecommendationOrderByIdAsc(directRecommendation))
                .thenReturn(List.of());
        when(plantingRepository.findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of(DirectRecommendationPlantingFormulatedFertilizerLineModel.builder()
                        .phase("Plantio")
                        .relationUsed("1-3.5-2")
                        .doseKgHa(250.0)
                        .doseUnitMode("LINEAR_METER")
                        .doseUnitLabel("g/m linear")
                        .gramsPerLinearMeter(12.5)
                        .technicalObservation("Formulado de plantio selecionado.")
                        .build()));
        when(coverageRepository.findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of());
        when(fertilizerResolver.formulated(null)).thenReturn(
                new DirectRecommendationFertilizerResolver.FormulatedMineralFertilizerData("04-14-08", 4d, 14d, 8d));

        String report = service.build(recommendation);

        assertThat(report).contains("| Adubação | Formulado | Relação N-P2O5-K2O | kg/ha | g/m linear | Observação técnica |");
        assertThat(report).contains("| Plantio | 04-14-08 | 1-3.5-2 | 250.00 kg/ha | 12.50 | Formulado de plantio selecionado. |");
        assertThat(report).contains("| Cobertura | Não estruturado | Não aplicável com os dados disponíveis. | Não calculado por falta de dados. | Não calculado por falta de dados. | Aviso técnico: não houve linha estruturada de formulado NPK para cobertura; a recomendação direta não propagou a cobertura textual do laudo. |");
        assertThat(report).contains("| Opção 2 - Cobertura com adubos simples - N | Ureia |");
        assertThat(report).contains("| Opção 2 - Cobertura com adubos simples - K2O | Cloreto de potássio |");
    }

    @Test
    void buildKeepsStructuredCoveragePhaseWithoutDuplicatingCoverageOrder() {
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
                coverageRepository,
                null
        );
        RecommendationModel recommendation = recommendationWithPlantingAndCoverageTechnicalReport();
        recommendation.setId(1L);
        DirectRecommendationModel directRecommendation = DirectRecommendationModel.builder()
                .id(2L)
                .recommendation(recommendation)
                .documentName(DirectRecommendationModel.DOCUMENT_NAME)
                .technicalReport("direta")
                .build();
        recommendation.setDirectRecommendation(directRecommendation);
        when(micronutrientRepository.findAllByDirectRecommendationOrderByIdAsc(directRecommendation))
                .thenReturn(List.of());
        when(plantingRepository.findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of());
        when(coverageRepository.findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of(DirectRecommendationCoverageFormulatedFertilizerLineModel.builder()
                        .coverageOrder(1)
                        .phase("COBERTURA 1ª")
                        .relationUsed("1.00-0.00-1.00")
                        .doseKgHa(180.0)
                        .doseUnitMode("LINEAR_METER")
                        .doseUnitLabel("g/m linear")
                        .gramsPerLinearMeter(9.0)
                        .technicalObservation("Formulado de cobertura selecionado.")
                        .build()));

        String report = service.build(recommendation);

        assertThat(report).contains("| COBERTURA 1ª | 20-00-20 | 1.00-0.00-1.00 | 180.00 kg/ha | 9.00 | Formulado de cobertura selecionado. |");
        assertThat(report).doesNotContain("COBERTURA 1ª 1");
        assertThat(report).doesNotContain("Cobertura propagada do laudo técnico");
    }

    @Test
    void buildUsesDefaultTechnicalObservationOnlyForMicronutrientsWithoutObservation() {
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
                coverageRepository,
                null
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
                .thenReturn(List.of(
                        DirectRecommendationMicronutrientFertilizerLineModel.builder()
                                .micronutrient(AppliedMicronutrient.B)
                                .micronutrientDoseKgHa(1.2)
                                .fertilizerDoseKgHa(10.0)
                                .doseUnitMode("LINEAR_METER")
                                .doseUnitLabel("g/m linear")
                                .gramsPerLinearMeter(0.5)
                                .technicalObservation(null)
                                .build(),
                        DirectRecommendationMicronutrientFertilizerLineModel.builder()
                                .micronutrient(AppliedMicronutrient.Zn)
                                .micronutrientDoseKgHa(2.0)
                                .fertilizerDoseKgHa(8.0)
                                .doseUnitMode("LINEAR_METER")
                                .doseUnitLabel("g/m linear")
                                .gramsPerLinearMeter(0.4)
                                .technicalObservation("Não informado")
                                .build(),
                        DirectRecommendationMicronutrientFertilizerLineModel.builder()
                                .micronutrient(AppliedMicronutrient.Cu)
                                .micronutrientDoseKgHa(1.0)
                                .fertilizerDoseKgHa(5.0)
                                .doseUnitMode("LINEAR_METER")
                                .doseUnitLabel("g/m linear")
                                .gramsPerLinearMeter(0.25)
                                .technicalObservation("Aplicar conforme análise específica.")
                                .build()));
        when(plantingRepository.findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of());
        when(coverageRepository.findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc(directRecommendation))
                .thenReturn(List.of());

        String report = service.build(recommendation);

        assertThat(report).contains("| B | Borax | 1.20 kg/ha | 10.00 kg/ha | 0.50 | Misturar com os demais adubos minerais no plantio. |");
        assertThat(report).contains("| Zn | Sulfato de zinco | 2.00 kg/ha | 8.00 kg/ha | 0.40 | Misturar com os demais adubos minerais no plantio. |");
        assertThat(report).contains("| Cu | Sulfato de cobre | 1.00 kg/ha | 5.00 kg/ha | 0.25 | Aplicar conforme análise específica. |");
        assertThat(report).doesNotContain("| Não informado |");
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

    private RecommendationModel recommendationWithPlantingAndCoverageTechnicalReport() {
        return RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .cropYear(2026)
                .technicalReport("""
                        ## 10. Adubação de plantio

                        | Fase | Nutriente | Fertilizante | Quantidade |
                        |---|---|---|---:|
                        | Plantio | NPK | 04-14-08 | 250 kg/ha |

                        ## 11. Adubação de cobertura

                        | Fase | Nutriente | Fertilizante | Quantidade |
                        |---|---|---|---:|
                        | Opção 2 - Cobertura com adubos simples - N | N | Ureia | 60 kg/ha |
                        | Opção 2 - Cobertura com adubos simples - K2O | K2O | Cloreto de potássio | 40 kg/ha |
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
