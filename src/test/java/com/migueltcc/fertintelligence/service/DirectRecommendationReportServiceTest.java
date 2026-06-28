package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.CropSpacingCalculationService;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.DirectRecommendationReportService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

    private DirectRecommendationReportService newService() {
        return new DirectRecommendationReportService(
                new CropSpacingCalculationService(),
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
}
