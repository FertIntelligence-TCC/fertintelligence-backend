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
                                .fertilizerDoseKgHa(10.0)
                                .doseUnitMode("LINEAR_METER")
                                .doseUnitLabel(null)
                                .gramsPerLinearMeter(0.5)
                                .build()),
                        List.of(DirectRecommendationPlantingFormulatedFertilizerLineModel.builder()
                                .phase("Plantio")
                                .doseKgHa(250.0)
                                .doseUnitMode("LINEAR_METER")
                                .doseUnitLabel("g/m linear")
                                .gramsPerLinearMeter(12.5)
                                .build()),
                        List.of(DirectRecommendationCoverageFormulatedFertilizerLineModel.builder()
                                .coverageOrder(1)
                                .phase("COBERTURA 1ª")
                                .doseKgHa(180.0)
                                .doseUnitMode("PIT")
                                .doseUnitLabel(null)
                                .gramsPerPit(9.0)
                                .build()),
                        null);

        assertThat(item(items, "Borax").getTypeGroup()).isEqualTo("Micronutriente - B");
        assertThat(item(items, "Borax").getPhase()).isEqualTo("Plantio");
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

    @Test
    void collectShoppingItemsIncludesGypsumAndLowDoseSulfurAlternatives() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport("""
                        ## 8. Gessagem

                        - Necessidade de gessagem: Sim.
                        - Dose de gesso: 300.00 kg/ha
                        - Enxofre equivalente: 45.00 kg/ha de S
                        - Recomendação de aplicação: Dose baixa: aplicar na adubação de plantio.
                        - Dose comercial: 300.00 kg/ha
                        - Alternativa com sulfato de amônio 22% S: 204.55 kg/ha
                        - Alternativa com superfosfato simples 11% S: 409.09 kg/ha
                        """)
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(recommendation);

        assertThat(item(items, "Gesso agrícola").getKgHa()).isEqualTo(300.0);
        assertThat(item(items, "Sulfato de amônio 22% S").getKgHa()).isEqualTo(204.55);
        assertThat(item(items, "Sulfato de amônio 22% S").getTypeGroup()).isEqualTo("Alternativa de S para gessagem em dose baixa");
        assertThat(item(items, "Superfosfato simples 11% S").getKgHa()).isEqualTo(409.09);
    }

    @Test
    void collectShoppingItemsDoesNotMergeOptionsPhasesOrZeroDoses() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport("""
                        ## 10. Adubação de plantio

                        | Fase | Nutriente | Fertilizante | Dose |
                        |---|---|---|---:|
                        | Plantio | N | Ureia | 100 kg/ha |
                        | Plantio | N | Sulfato de amônio | 0 kg/ha |

                        ## 11. Adubação de cobertura

                        | Fase | Nutriente | Fertilizante | Dose |
                        |---|---|---|---:|
                        | Cobertura 1 | N | Ureia | 50 kg/ha |
                        """)
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(
                        recommendation,
                        List.of(),
                        List.of(DirectRecommendationPlantingFormulatedFertilizerLineModel.builder()
                                .phase("Plantio")
                                .doseKgHa(120.0)
                                .build()),
                        List.of(),
                        null);

        assertThat(items.stream().filter(item -> "Ureia".equals(item.getName()))).hasSize(3);
        assertThat(items.stream().map(TechnicalRecommendationDocumentSupport.ShoppingItem::getOption))
                .contains("opcao_1_formulados", "opcao_2_fontes_simples", "cobertura_opcao_2");
        assertThat(items.stream().map(TechnicalRecommendationDocumentSupport.ShoppingItem::getPhase))
                .contains("Plantio", "Cobertura 1");
        assertThat(items.stream().map(TechnicalRecommendationDocumentSupport.ShoppingItem::getName))
                .doesNotContain("Sulfato de amônio");
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
