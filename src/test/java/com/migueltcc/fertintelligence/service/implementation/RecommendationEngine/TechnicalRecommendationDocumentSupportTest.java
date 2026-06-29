package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TechnicalRecommendationDocumentSupportTest {

    @Test
    void collectShoppingItemsPropagatesPhaseAndLocalizedUnitFromStructuredRecommendationLines() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport("")
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(
                        recommendation,
                        List.of(DirectRecommendationMicronutrientFertilizerLineModel.builder()
                                .micronutrient(AppliedMicronutrient.B)
                                .fertilizerName("Borax")
                                .fertilizerDoseKgHa(10.0)
                                .doseUnitMode("LINEAR_METER")
                                .doseUnitLabel(null)
                                .gramsPerLinearMeter(0.5)
                                .build()),
                        List.of(DirectRecommendationPlantingFormulatedFertilizerLineModel.builder()
                                .phase("Plantio")
                                .fertilizerName("04-14-08")
                                .nitrogenPercent(4.0)
                                .p2o5Percent(14.0)
                                .k2oPercent(8.0)
                                .doseKgHa(250.0)
                                .doseUnitMode("LINEAR_METER")
                                .doseUnitLabel("g/m linear")
                                .gramsPerLinearMeter(12.5)
                                .build()),
                        List.of(DirectRecommendationCoverageFormulatedFertilizerLineModel.builder()
                                .coverageOrder(1)
                                .phase("COBERTURA 1ª")
                                .fertilizerName("20-00-20")
                                .nitrogenPercent(20.0)
                                .p2o5Percent(0.0)
                                .k2oPercent(20.0)
                                .doseKgHa(180.0)
                                .doseUnitMode("PIT")
                                .doseUnitLabel(null)
                                .gramsPerPit(9.0)
                                .build()));

        assertThat(item(items, "Borax").getTypeGroup()).isEqualTo("Micronutriente - B");
        assertThat(item(items, "Borax").getPhase()).isEqualTo(TechnicalRecommendationDocumentSupport.NOT_APPLICABLE);
        assertThat(item(items, "Borax").getLocalizedDose()).isEqualTo("0.50 g/m linear");
        assertThat(item(items, "04-14-08").getPhase()).isEqualTo("Plantio");
        assertThat(item(items, "04-14-08").getLocalizedDose()).isEqualTo("12.50 g/m linear");
        assertThat(item(items, "20-00-20").getPhase()).isEqualTo("COBERTURA 1ª");
        assertThat(item(items, "20-00-20").getLocalizedDose()).isEqualTo("9.00 g/cova");
    }

    @Test
    void collectShoppingItemsPropagatesPhaseFromLegacyTechnicalReportRows() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport(technicalReport())
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(recommendation);

        assertThat(item(items, "Ureia").getPhase()).isEqualTo("Plantio");
        assertThat(item(items, "Cloreto de potássio").getPhase()).isEqualTo("Cobertura 1 - K");
    }

    private TechnicalRecommendationDocumentSupport.ShoppingItem item(
            List<TechnicalRecommendationDocumentSupport.ShoppingItem> items,
            String name) {
        return items.stream()
                .filter(item -> name.equals(item.getName()))
                .findFirst()
                .orElseThrow();
    }

    private String technicalReport() {
        return """
                ## 10. Adubação de plantio

                | Fase | Nutriente | Fertilizante | Dose |
                |---|---|---|---:|
                | Plantio | N | Ureia | 100 kg/ha |

                ## 11. Adubação de cobertura

                | Fase | Nutriente | Fertilizante | Dose |
                |---|---|---|---:|
                | Cobertura 1 - K | K2O | Cloreto de potássio | 40 kg/ha |
                """;
    }
}
