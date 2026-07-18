package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.migueltcc.fertintelligence.dto.purchaseList.PurchaseListItemResponseDto;
import com.migueltcc.fertintelligence.dto.purchaseList.PurchaseListResponseDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListItemResponseDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListResponseDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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
    void collectShoppingItemsClassifiesAllCorrectiveItemsIncludingFteComplements() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport("""
                        ## 9. Adubação corretiva

                        | Nutriente/Atributo corrigido | Necessidade | Fonte sugerida | Dose | Memória de cálculo | Aviso técnico |
                        |---|---|---|---:|---|---|
                        | P2O5 corretivo - Superfosfato Simples | 60 kg/ha de P2O5 | Superfosfato Simples | 333.33 kg/ha de produto | Memória |  |
                        | P2O5 corretivo - Superfosfato Triplo | 60 kg/ha de P2O5 | Superfosfato Triplo | 133.33 kg/ha de produto | Memória |  |
                        | P2O5 corretivo - Termofosfato Magnesiano | 60 kg/ha de P2O5 | Termofosfato Magnesiano | 300.00 kg/ha de produto | Memória |  |
                        | K2O corretivo - Cloreto de Potássio | 40 kg/ha de K2O | Cloreto de Potássio | 66.67 kg/ha de produto | Memória |  |
                        | Formulado 00-P2O5-K2O corretivo | P2O5 e K2O | NPK 00-20-20 | 300.00 kg/ha de produto | Memória |  |
                        | FTE BR 12 corretivo | Zn como nutriente-base: 2 kg/ha | FTE BR-12 | 16.67 kg/ha de produto | Memória |  |
                        | FTE BR 24 corretivo | Zn como nutriente-base: 2 kg/ha | FTE BR-24 | 16.67 kg/ha de produto | Memória |  |
                        | Complemento após FTE BR-12 de B | 1 kg/ha de B | Borax | 9.09 kg/ha de produto | Memória |  |
                        | Complemento corretivo de B | 1 kg/ha de B | Borax | 9.09 kg/ha de produto | Memória |  |
                        """)
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(recommendation);

        assertThat(item(items, "Superfosfato Simples").getTypeGroup()).isEqualTo("SSP - Superfosfato Simples");
        assertThat(item(items, "Superfosfato Triplo").getTypeGroup()).isEqualTo("Superfosfato Triplo");
        assertThat(item(items, "Termofosfato Magnesiano").getTypeGroup()).isEqualTo("Termofosfato Magnesiano");
        assertThat(item(items, "Cloreto de Potássio").getTypeGroup()).isEqualTo("KCl - Cloreto de Potássio");
        assertThat(item(items, "NPK 00-20-20").getTypeGroup()).isEqualTo("Formulado corretivo");
        assertThat(item(items, "FTE BR-12").getTypeGroup()).isEqualTo("FTE BR-12");
        assertThat(item(items, "FTE BR-24").getTypeGroup()).isEqualTo("FTE BR-24");
        assertThat(items.stream().filter(item -> "Borax".equals(item.getName())))
                .extracting(TechnicalRecommendationDocumentSupport.ShoppingItem::getTypeGroup)
                .containsExactlyInAnyOrder("Complemento após FTE", "Complemento corretivo simples");
        assertThat(item(items, "NPK 00-20-20").getOption()).isEqualTo("adubacao_corretiva_opcao_1_formulados");
        assertThat(item(items, "FTE BR-12").getOption()).isEqualTo("adubacao_corretiva_opcao_1_formulados");
        assertThat(item(items, "FTE BR-24").getOption()).isEqualTo("adubacao_corretiva_opcao_1_formulados");
        assertThat(item(items, "Superfosfato Simples").getOption()).isEqualTo("adubacao_corretiva_opcao_2_adubos_simples");
        assertThat(item(items, "Superfosfato Triplo").getOption()).isEqualTo("adubacao_corretiva_opcao_2_adubos_simples");
        assertThat(item(items, "Termofosfato Magnesiano").getOption()).isEqualTo("adubacao_corretiva_opcao_2_adubos_simples");
        assertThat(item(items, "Cloreto de Potássio").getOption()).isEqualTo("adubacao_corretiva_opcao_2_adubos_simples");
        assertThat(items.stream().filter(item -> "Borax".equals(item.getName())))
                .extracting(TechnicalRecommendationDocumentSupport.ShoppingItem::getOption)
                .containsExactlyInAnyOrder(
                        "adubacao_corretiva_opcao_1_formulados",
                        "adubacao_corretiva_opcao_2_adubos_simples");
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
    void collectShoppingItemsKeepsTopdressingOptionOneSeparateFromOptionTwo() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport("""
                        ## 11. Adubação de cobertura

                        | Fase | Nutriente | Fertilizante | Dose |
                        |---|---|---|---:|
                        | Opção 1 - Cobertura com formulado | N, K2O | NPK 10.00-0.00-20.00 | 200.00 kg/ha |
                        | Opção 1 - Complemento de cobertura - N | N | Ureia | 30.00 kg/ha |
                        | Opção 2 - Cobertura com adubos simples - N | N | Ureia | 100.00 kg/ha |
                        | Opção 2 - Cobertura com adubos simples - K2O | K2O | Cloreto de potássio | 80.00 kg/ha |
                        """)
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(recommendation);

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> optionOne = items.stream()
                .filter(item -> "cobertura_opcao_1_formulados".equals(item.getOption()))
                .toList();
        List<TechnicalRecommendationDocumentSupport.ShoppingItem> optionTwo = items.stream()
                .filter(item -> "cobertura_opcao_2_adubos_simples".equals(item.getOption()))
                .toList();

        assertThat(optionOne)
                .extracting(TechnicalRecommendationDocumentSupport.ShoppingItem::getName)
                .containsExactlyInAnyOrder("NPK 10.00-0.00-20.00", "Ureia");
        assertThat(optionTwo)
                .extracting(TechnicalRecommendationDocumentSupport.ShoppingItem::getName)
                .containsExactlyInAnyOrder("Ureia", "Cloreto de potássio");
        assertThat(optionTwo)
                .extracting(TechnicalRecommendationDocumentSupport.ShoppingItem::getName)
                .doesNotContain("NPK 10.00-0.00-20.00");
        assertThat(optionOne)
                .extracting(TechnicalRecommendationDocumentSupport.ShoppingItem::getPhase)
                .noneMatch(phase -> phase.contains("Opção 2"));
        assertThat(items.stream().filter(item -> "Ureia".equals(item.getName())))
                .hasSize(2);
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
    void collectShoppingItemsIncludesLimestoneFromPositiveLimingDoseInTonsPerHectare() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport("""
                        ## 7. Calagem

                        - Necessidade de calagem ajustada: 1.90 t/ha
                        - Dose corrigida por PRNT: 1.90 t/ha
                        - Dose efetiva registrada pelo cálculo: 1.90 t/ha
                        """)
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(recommendation);

        TechnicalRecommendationDocumentSupport.ShoppingItem limestone = item(items, "Calcário");
        assertThat(limestone.getKgHa()).isEqualTo(1900.0);
        assertThat(limestone.getSection()).isEqualTo(TechnicalRecommendationDocumentSupport.SECTION_ACIDITY_CORRECTION);
        assertThat(limestone.getOption()).isEqualTo("correcao_acidez_recomendacao_unica");
        assertThat(limestone.getItemFlag()).isEqualTo("obrigatorio");
    }

    @Test
    void collectShoppingItemsPrefersStructuredPlantingFormulatedLineOverEquivalentTextFallback() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport("""
                        ## 10. Adubação de plantio

                        | Fase | Nutriente | Fertilizante | Dose |
                        |---|---|---|---:|
                        | Opção 1 - Plantio com formulado | N, P2O5, K2O | NPK 6.00-24.00-24.00 | 250.00 kg/ha |
                        """)
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(
                        recommendation,
                        List.of(),
                        List.of(DirectRecommendationPlantingFormulatedFertilizerLineModel.builder()
                                .phase("Plantio")
                                .doseKgHa(250.0)
                                .build()),
                        List.of(),
                        resolver("NPK 6.00-24.00-24.00", "NPK 10.00-0.00-20.00"));

        assertThat(items.stream()
                .filter(item -> "NPK 6.00-24.00-24.00".equals(item.getName()))
                .filter(item -> TechnicalRecommendationDocumentSupport.SECTION_PLANTING_OPTION_1.equals(item.getSection())))
                .hasSize(1);
        assertThat(item(items, "NPK 6.00-24.00-24.00").getPhase()).isEqualTo("Plantio");
    }

    @Test
    void collectShoppingItemsPrefersStructuredCoverageFormulatedLineOverEquivalentTextFallback() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropName(NomeComum.MILHO)
                .technicalReport("""
                        ## 11. Adubação de cobertura

                        | Fase | Nutriente | Fertilizante | Dose |
                        |---|---|---|---:|
                        | Opção 1 - Cobertura com formulado | N, K2O | NPK 10.00-0.00-20.00 | 180.00 kg/ha |
                        """)
                .build();

        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(
                        recommendation,
                        List.of(),
                        List.of(),
                        List.of(DirectRecommendationCoverageFormulatedFertilizerLineModel.builder()
                                .fertilizerId(2L)
                                .coverageOrder(1)
                                .phase("Cobertura")
                                .doseKgHa(180.0)
                                .build()),
                        resolver("NPK 6.00-24.00-24.00", "NPK 10.00-0.00-20.00"));

        assertThat(items.stream()
                .filter(item -> "NPK 10.00-0.00-20.00".equals(item.getName()))
                .filter(item -> TechnicalRecommendationDocumentSupport.SECTION_COVERAGE_OPTION_1.equals(item.getSection())))
                .hasSize(1);
        assertThat(item(items, "NPK 10.00-0.00-20.00").getPhase()).isEqualTo("Cobertura 1");
    }

    @Test
    void shoppingBlocksKeepOptionsMutuallyExclusiveAndDoNotSumAlternatives() {
        RecommendationStructuredDataAssembler assembler = new RecommendationStructuredDataAssembler(
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationRepository.class),
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository.class),
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository.class),
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository.class),
                null, null);
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

        assertThat(blocks).extracting(ShoppingListResponseDto.ShoppingListBlockResponseDto::getName)
                .containsExactly("Corretivos", "Adubação Corretiva", "Adubação de plantio e cobertura");

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

        PurchaseListResponseDto purchaseList = assembler.purchaseList(items);

        assertThat(purchaseList.getBlocks())
                .extracting("key")
                .containsExactly("ACIDITY_CORRECTION", "CORRECTIVE_FERTILIZATION", "PLANTING", "TOPDRESSING");
        assertThat(purchaseList.getBlocks())
                .extracting("mutuallyExclusiveOptions")
                .containsExactly(false, true, true, true);
        assertThat(purchaseList.getBlocks().get(1).getDescription())
                .isEqualTo("Fontes corretivas já calculadas, separando formulados/FTE das fontes simples de P2O5, K2O e micronutrientes.");
        assertThat(purchaseList.getBlocks().get(1).getOptions())
                .extracting("title")
                .containsExactly(
                        "Formulado corretivo, FTE BR-12 e FTE BR-24",
                        "SSP, superfosfato triplo, termofosfato, KCl e complementos");
        assertThat(purchaseList.getBlocks().get(2).getOptions())
                .extracting("key")
                .containsExactly("plantio_opcao_1_formulados", "plantio_opcao_2_adubos_simples");
        assertThat(purchaseList.getBlocks().get(3).getOptions())
                .extracting("key")
                .containsExactly("cobertura_opcao_1_formulados", "cobertura_opcao_2_adubos_simples");
        assertThat(purchaseList.getBlocks().get(1).getOptions().get(0).getItems().get(0).getSourceName())
                .isEqualTo("NPK 00-20-20");
        assertThat(purchaseList.getBlocks().get(1).getOptions().get(0).getItems().get(0).getShortTechnicalNote())
                .isEqualTo("Item consolidado");
        assertThat(purchaseList.getBlocks().stream()
                .flatMap(block -> block.getOptions().stream())
                .flatMap(option -> option.getItems().stream())
                .map(PurchaseListItemResponseDto::getShortTechnicalNote))
                .noneMatch(text -> text.contains(": 0.00 kg/ha"));
        assertThat(purchaseList.getCalculationDetails())
                .extracting("sourceName")
                .containsExactly("NPK 00-20-20", "Superfosfato Simples");
        assertThat(purchaseList.getCalculationDetails())
                .extracting("blockKey")
                .containsExactly("CORRECTIVE_FERTILIZATION", "CORRECTIVE_FERTILIZATION");
        assertThat(purchaseList.getCalculationDetails())
                .extracting("calculationMemory")
                .noneMatch(text -> ((String) text).contains(": 0.00 kg/ha"));
    }

    @Test
    void summaryAndDirectSectionsContainEveryCorrectiveRowWithOriginalDoseAndUnit() {
        RecommendationModel recommendation = RecommendationModel.builder()
                .technicalReport("""
                        ## 9. Adubação corretiva

                        | Nutriente/Atributo corrigido | Necessidade | Fonte sugerida | Dose | Memória de cálculo | Aviso técnico |
                        |---|---|---|---:|---|---|
                        | Formulado 00-P2O5-K2O corretivo | P2O5 e K2O | NPK 00-20-20 | 300.00 kg/ha de produto | Memória formulado | Observação formulado |
                        | P2O5 corretivo | 60 kg/ha | Superfosfato Simples | 333.33 kg/ha de produto | Memória P | Observação P |
                        | K2O corretivo | 40 kg/ha | Cloreto de Potássio | 66.67 kg/ha de produto | Memória K | Observação K |
                        | FTE BR 12 corretivo | 2 kg/ha Zn | FTE BR-12 | 16.67 kg/ha de produto | Memória FTE | Observação FTE |
                        | Complemento após FTE BR-12 de Cu | 1 kg/ha Cu | Sulfato de Cobre | 4.00 kg/ha de produto | Memória Cu | Observação Cu |
                        | Complemento após FTE BR-12 de Mn | 1 kg/ha Mn | Sulfato Manganoso | 5.00 kg/ha de produto | Memória Mn | Observação Mn |
                        """)
                .build();
        RecommendationStructuredDataAssembler assembler = new RecommendationStructuredDataAssembler(
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationRepository.class),
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository.class),
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository.class),
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository.class),
                null, null);

        assertCorrectiveSections(assembler.summarySections(recommendation));
        assertCorrectiveSections(assembler.directSections(recommendation, ""));
    }

    @Test
    void shoppingItemsUseCultivatedAreaForTotalAndDoNotFallbackWhenAreaIsInvalid() {
        RecommendationStructuredDataAssembler assembler = new RecommendationStructuredDataAssembler(
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationRepository.class),
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository.class),
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository.class),
                mock(com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository.class),
                null, null);
        RecommendationModel recommendation = RecommendationModel.builder()
                .cropUsedAreaInThePlot(50.0)
                .technicalReport("""
                        ## 9. Adubação corretiva

                        | Nutriente/Atributo corrigido | Necessidade | Fonte sugerida | Dose |
                        |---|---|---|---:|
                        | Formulado corretivo | P2O5 e K2O | NPK 00-20-20 | 500.00 kg/ha de produto |
                        """)
                .build();

        ShoppingListItemResponseDto item = assembler.shoppingItems(recommendation).get(0);
        assertThat(item.getQuantityPerHectare()).isEqualTo("500.00 kg/ha");
        assertThat(item.getTotalForArea()).isEqualTo("25000.00 kg");

        recommendation.setCropUsedAreaInThePlot(0.0);
        assertThat(assembler.shoppingItems(recommendation).get(0).getTotalForArea()).isEqualTo("Não calculado por falta de dados.");
        recommendation.setCropUsedAreaInThePlot(null);
        assertThat(assembler.shoppingItems(recommendation).get(0).getTotalForArea()).isEqualTo("Não calculado por falta de dados.");
    }

    private void assertCorrectiveSections(List<com.migueltcc.fertintelligence.dto.recommendation.RecommendationTableSectionDto> sections) {
        List<List<String>> rows = sections.stream()
                .filter(section -> section.getSectionKey().startsWith("adubacao_corretiva_"))
                .flatMap(section -> section.getRows().stream())
                .toList();
        assertThat(rows).extracting(row -> row.get(2))
                .containsExactlyInAnyOrder("NPK 00-20-20", "Superfosfato Simples", "Cloreto de Potássio",
                        "FTE BR-12", "Sulfato de Cobre", "Sulfato Manganoso");
        assertThat(rows).extracting(row -> row.get(3))
                .contains("300.00 kg/ha de produto", "4.00 kg/ha de produto", "5.00 kg/ha de produto");
    }

    @Test
    void purchaseListJsonKeepsCalculationMemoryOutsidePurchaseItems() throws Exception {
        RecommendationStructuredDataAssembler assembler = new RecommendationStructuredDataAssembler(null, null, null, null, null, null);
        PurchaseListResponseDto purchaseList = assembler.purchaseList(List.of(
                ShoppingListItemResponseDto.builder()
                        .inputName("NPK 00-20-20")
                        .typeGroup("Formulado")
                        .option("adubacao_corretiva_opcao_1_formulados")
                        .quantityKgHa(300.0)
                        .totalForArea("1.500 kg")
                        .phase("Correção")
                        .build()));

        JsonNode root = new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(purchaseList));
        JsonNode item = root.at("/blocks/1/options/0/items/0");

        assertThat(item.size()).isEqualTo(7);
        assertThat(item.has("sourceName")).isTrue();
        assertThat(item.has("sourceType")).isTrue();
        assertThat(item.has("nutrientTarget")).isTrue();
        assertThat(item.has("doseKgHa")).isTrue();
        assertThat(item.has("areaQuantity")).isTrue();
        assertThat(item.has("unit")).isTrue();
        assertThat(item.has("shortTechnicalNote")).isTrue();
        assertThat(item.has("calculationMemory")).isFalse();
        assertThat(item.has("technicalNote")).isFalse();
        assertThat(root.at("/calculationDetails/0/calculationMemory").asText()).contains("Dose operacional consolidada");
        assertThat(root.at("/calculationDetails/0/technicalNote").asText()).contains("Opção:");
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
