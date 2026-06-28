package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis.PhysicalAnalysisUnit;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TexturalClassification;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.SoilTextureClassificationService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class SoilTextureClassificationServiceTest {

    private final SoilTextureClassificationService service = new SoilTextureClassificationService();

    @ParameterizedTest
    @CsvSource({
            "900,50,50,Areia",
            "800,120,80,Areia franca",
            "600,300,100,Franco arenosa",
            "400,400,200,Franca",
            "200,700,100,Franco siltosa",
            "50,850,100,Silte",
            "500,250,250,Franco argilo arenosa",
            "300,400,300,Franco argilosa",
            "100,600,300,Franco argilo siltosa",
            "500,100,400,Argilo arenosa",
            "300,250,450,Argila",
            "100,450,450,Argilo siltosa",
            "200,150,650,Muito argilosa"
    })
    void classifyAmericanCoversExpectedTexturalClasses(double sand,
                                                       double silt,
                                                       double clay,
                                                       String expectedClass) {
        SoilTextureClassificationService.SoilTextureClassificationResult result =
                service.classifyAmerican(physicalAnalysis(sand, silt, clay, PhysicalAnalysisUnit.G_PER_KG));

        assertThat(result.classified()).isTrue();
        assertThat(result.strategy()).isEqualTo(TexturalClassification.AMERICANO);
        assertThat(result.texturalClass()).isEqualTo(expectedClass);
    }

    @ParameterizedTest
    @CsvSource({
            ",300,100",
            "600,,100",
            "600,300,"
    })
    void classifyAmericanDoesNotClassifyWhenFractionsAreMissing(Double sand,
                                                                Double silt,
                                                                Double clay) {
        SoilTextureClassificationService.SoilTextureClassificationResult result =
                service.classifyAmerican(physicalAnalysis(sand, silt, clay, PhysicalAnalysisUnit.G_PER_KG));

        assertThat(result.classified()).isFalse();
        assertThat(result.texturalClass()).isNull();
        assertThat(result.warnings()).isNotEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "G_PER_DM3",
            "G_PER_KG"
    })
    void classifyAmericanUsesGranulometryAsGramsPerKg(PhysicalAnalysisUnit unit) {
        SoilTextureClassificationService.SoilTextureClassificationResult result =
                service.classifyAmerican(physicalAnalysis(600d, 300d, 100d, unit));

        assertThat(result.classified()).isTrue();
        assertThat(result.texturalClass()).isEqualTo("Franco arenosa");
        assertThat(result.warnings()).isEmpty();
    }

    private PhysicalAnalysisExtractModel physicalAnalysis(Double sand,
                                                         Double silt,
                                                         Double clay,
                                                         PhysicalAnalysisUnit unit) {
        return PhysicalAnalysisExtractModel.builder()
                .teorAreia(sand)
                .unidadeTeorAreia(unit)
                .teorSilte(silt)
                .unidadeTeorSilte(unit)
                .teorArgila(clay)
                .unidadeTeorArgila(unit)
                .build();
    }
}
