package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListItemResponseDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListResponseDto;
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
                                .fertilizerId(2L)
                                .coverageOrder(1)
                                .phase("COBERTURA 1ª")
                                .doseKgHa(180.0)
                                .doseUnitMode("PIT")
                                .doseUnitLabel(null)
                                .gramsPerPit(9.0)
                                .build()),
                        resolver("04-14-08", "20-00-20"));

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
        assertThat(item(items, "Gesso agrícola").getSection()).isEqualTo(TechnicalRecommendationDocumentSupport.SECTION_ACIDITY_CORRECTION);
        assertThat(item(items, "Sulfato de amônio 22% S").getKgHa()).isEqualTo(204.55);
        assertThat(item(items, "Sulfato de amônio 22% S").getSection()).isEqualTo(TechnicalRecommendationDocumentSupport.SECTION_ACIDITY_CORRECTION);
        assertThat(item(items, "Sulfato de amônio 22% S").getTypeGroup()).isEqualTo("Alternativa de S para gessagem em dose baixa");
        assertThat(item(items, "Superfosfato simples 11% S").getKgHa()).isEqualTo(409.09);
    }

    @Test
    void collectShoppingItemsClassifiesCorrectiveBlockAndSkipsAutomaticFteComplements() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport("""
                        ## 9. Adubação corretiva

                        | Nutriente/Atributo corrigido | Necessidade | Fonte sugerida | Dose | Memória de cálculo | Aviso técnico |
                        |---|---|---|---:|---|---|
                        | P2O5 corretivo - Superfosfato Simples | 60 kg/ha de P2O5 | Superfosfato Simples | 333.33 kg/ha de produto | Memória |  |
                        | K2O corretivo - Cloreto de Potássio | 40 kg/ha de K2O | Cloreto de Potássio | 66.67 kg/ha de produto | Memória |  |
                        | Formulado 00-P2O5-K2O corretivo | P2O5 e K2O | NPK 00-20-20 | 300.00 kg/ha de produto | Memória |  |
                        | FTE BR 12 corretivo | Zn como nutriente-base: 2 kg/ha | FTE BR-12 | 16.67 kg/ha de produto | Memória |  |
                        | Complemento após FTE BR-12 de B | 1 kg/ha de B | Borax | 9.09 kg/ha de produto | Memória |  |
                        | Complemento corretivo de B | 1 kg/ha de B | Borax | 9.09 kg/ha de produto | Memória |  |
                        """)
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(recommendation);

        assertThat(item(items, "Superfosfato Simples").getTypeGroup()).isEqualTo("Superfosfato Simples");
        assertThat(item(items, "Cloreto de Potássio").getTypeGroup()).isEqualTo("Cloreto de Potássio");
        assertThat(item(items, "NPK 00-20-20").getTypeGroup()).isEqualTo("Formulados");
        assertThat(item(items, "FTE BR-12").getTypeGroup()).isEqualTo("FTE BR-12");
        assertThat(items.stream().filter(item -> "Borax".equals(item.getName()))).hasSize(1);
        assertThat(item(items, "Borax").getItemFlag()).isEqualTo("alternativa");
        assertThat(item(items, "NPK 00-20-20").getOption()).isEqualTo("adubacao_corretiva_opcao_1_formulados");
        assertThat(item(items, "FTE BR-12").getOption()).isEqualTo("adubacao_corretiva_opcao_1_formulados");
        assertThat(item(items, "Superfosfato Simples").getOption()).isEqualTo("adubacao_corretiva_opcao_2_adubos_simples");
        assertThat(item(items, "Cloreto de Potássio").getOption()).isEqualTo("adubacao_corretiva_opcao_2_adubos_simples");
        assertThat(item(items, "Borax").getOption()).isEqualTo("adubacao_corretiva_opcao_2_adubos_simples");
        assertThat(items.stream().map(TechnicalRecommendationDocumentSupport.ShoppingItem::getSection))
                .containsOnly(TechnicalRecommendationDocumentSupport.SECTION_CORRECTIVE_FERTILIZATION);
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
                        | Plantio |  | MAP | 90 kg/ha |
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
                        resolver("04-14-08", "20-00-20"));

        assertThat(items.stream().filter(item -> "Ureia".equals(item.getName()))).hasSize(2);
        assertThat(items.stream().map(TechnicalRecommendationDocumentSupport.ShoppingItem::getName))
                .contains("04-14-08");
        assertThat(items.stream().map(TechnicalRecommendationDocumentSupport.ShoppingItem::getName))
                .doesNotContain("MAP");
        assertThat(items.stream().map(TechnicalRecommendationDocumentSupport.ShoppingItem::getOption))
                .contains("plantio_opcao_1_formulados", "plantio_opcao_2_adubos_simples", "cobertura_opcao_2_adubos_simples");
        assertThat(items.stream().map(TechnicalRecommendationDocumentSupport.ShoppingItem::getPhase))
                .contains("Plantio", "Cobertura 1");
        assertThat(items.stream().map(TechnicalRecommendationDocumentSupport.ShoppingItem::getName))
                .doesNotContain("Sulfato de amônio");
    }

    @Test
    void collectShoppingItemsSkipsRepeatedProductInSameOperationalPhase() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport("""
                        ## Fontes orgânicas, organominerais e micronutrientes

                        | Tipo de fonte | Nutriente/objetivo | Fonte | Dose | Unidade |
                        |---|---|---|---:|---|
                        | MICRONUTRIENTE | B | Borax | 10.00 | kg/ha de produto |

                        ## 10. Adubação de plantio

                        | Fase | Nutriente | Fertilizante | Dose |
                        |---|---|---|---:|
                        | Plantio | B | Borax | 10.00 kg/ha |

                        ## 11. Adubação de cobertura

                        | Fase | Nutriente | Fertilizante | Dose |
                        |---|---|---|---:|
                        | Cobertura 1 | B | Borax | 5.00 kg/ha |
                        """)
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(recommendation);

        assertThat(items.stream()
                .filter(item -> "Borax".equals(item.getName()))
                .filter(item -> "Plantio".equals(item.getPhase())))
                .hasSize(1);
        assertThat(item(items, "Borax").getKgHa()).isEqualTo(10.0);
        assertThat(items.stream()
                .filter(item -> "Borax".equals(item.getName()))
                .filter(item -> "Cobertura 1".equals(item.getPhase())))
                .hasSize(1);
    }

    @Test
    void shoppingBlocksKeepOptionsMutuallyExclusiveAndDoNotSumAlternatives() {
        RecommendationStructuredDataAssembler assembler = new RecommendationStructuredDataAssembler(null, null, null, null, null);
        List<ShoppingListItemResponseDto> items = List.of(
                ShoppingListItemResponseDto.builder()
                        .inputName("NPK 00-20-20")
                        .option("adubacao_corretiva_opcao_1_formulados")
                        .quantityKgHa(300.0)
                        .build(),
                ShoppingListItemResponseDto.builder()
                        .inputName("Superfosfato Simples")
                        .option("adubacao_corretiva_opcao_2_adubos_simples")
                        .quantityKgHa(333.33)
                        .build(),
                ShoppingListItemResponseDto.builder()
                        .inputName("Linha inválida")
                        .option("plantio_opcao_1_formulados")
                        .quantityKgHa(null)
                        .build());

        List<ShoppingListResponseDto.ShoppingListBlockResponseDto> blocks = assembler.shoppingBlocks(items);

        ShoppingListResponseDto.ShoppingListBlockResponseDto corrective = blocks.stream()
                .filter(block -> "adubacao_corretiva".equals(block.getCode()))
                .findFirst()
                .orElseThrow();

        assertThat(corrective.getOptions()).hasSize(2);
        assertThat(corrective.getOptions()).allMatch(ShoppingListResponseDto.ShoppingListOptionResponseDto::getMutuallyExclusive);
        assertThat(corrective.getOptions().get(0).getItems())
                .extracting(ShoppingListItemResponseDto::getInputName)
                .containsExactly("NPK 00-20-20");
        assertThat(corrective.getOptions().get(1).getItems())
                .extracting(ShoppingListItemResponseDto::getInputName)
                .containsExactly("Superfosfato Simples");
        assertThat(blocks.stream()
                .flatMap(block -> block.getOptions().stream())
                .flatMap(option -> option.getItems().stream())
                .map(ShoppingListItemResponseDto::getInputName))
                .doesNotContain("Linha inválida");
    }

    private TechnicalRecommendationDocumentSupport.ShoppingItem item(
            List<TechnicalRecommendationDocumentSupport.ShoppingItem> items,
            String name) {
        return items.stream()
                .filter(item -> name.equals(item.getName()))
                .findFirst()
                .orElseThrow();
    }

    private DirectRecommendationFertilizerResolver resolver(String plantingFormulatedName, String coverageFormulatedName) {
        return new DirectRecommendationFertilizerResolver(null, null) {
            @Override
            public SimpleMineralFertilizerData simple(Long fertilizerId, AppliedMicronutrient micronutrient) {
                return new SimpleMineralFertilizerData("Borax", 10.0);
            }

            @Override
            public FormulatedMineralFertilizerData formulated(Long fertilizerId) {
                String name = fertilizerId == null ? plantingFormulatedName : coverageFormulatedName;
                return new FormulatedMineralFertilizerData(name, 4.0, 14.0, 8.0);
            }
        };
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
