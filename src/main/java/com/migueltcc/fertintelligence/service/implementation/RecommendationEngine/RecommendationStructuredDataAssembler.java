package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.dto.recommendation.RecommendationTableSectionDto;
import com.migueltcc.fertintelligence.dto.purchaseList.PurchaseListBlockResponseDto;
import com.migueltcc.fertintelligence.dto.purchaseList.PurchaseListItemResponseDto;
import com.migueltcc.fertintelligence.dto.purchaseList.PurchaseListOptionResponseDto;
import com.migueltcc.fertintelligence.dto.purchaseList.PurchaseListResponseDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListResponseDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListItemResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RecommendationStructuredDataAssembler {

    private static final String SOURCE_TECHNICAL_REPORT = "technical_report";
    private static final String SOURCE_DIRECT_LINES = "direct_recommendation_lines";
    private static final String CHEMICAL_DIAGNOSIS_TITLE = "Diagnóstico químico";
    private static final String FOLIAR_DIAGNOSIS_TITLE = "Diagnóstico foliar";
    private static final String OPTION_CORRECTIVE_FORMULATED = "adubacao_corretiva_opcao_1_formulados";
    private static final String OPTION_CORRECTIVE_SIMPLE_SOURCES = "adubacao_corretiva_opcao_2_adubos_simples";
    private static final String OPTION_PLANTING_FORMULATED = "plantio_opcao_1_formulados";
    private static final String OPTION_PLANTING_SIMPLE_SOURCES = "plantio_opcao_2_adubos_simples";
    private static final String OPTION_COVERAGE_FORMULATED = "cobertura_opcao_1_formulados";
    private static final String OPTION_COVERAGE_SIMPLE_SOURCES = "cobertura_opcao_2_adubos_simples";
    private static final String OPTION_ACIDITY_CORRECTION = "correcao_acidez_recomendacao_unica";
    private static final String BLOCK_ACIDITY_CORRECTION = "ACIDITY_CORRECTION";
    private static final String BLOCK_CORRECTIVE_FERTILIZATION = "CORRECTIVE_FERTILIZATION";
    private static final String BLOCK_PLANTING = "PLANTING";
    private static final String BLOCK_TOPDRESSING = "TOPDRESSING";

    private final DirectRecommendationRepository directRecommendationRepository;
    private final DirectRecommendationMicronutrientFertilizerLineRepository micronutrientFertilizerLineRepository;
    private final DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingFormulatedFertilizerLineRepository;
    private final DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageFormulatedFertilizerLineRepository;
    private final DirectRecommendationFertilizerResolver fertilizerResolver;

    public List<RecommendationTableSectionDto> generalSections(RecommendationModel recommendation) {
        return generalSections(report(recommendation));
    }

    public List<RecommendationTableSectionDto> generalSections(String report) {
        List<RecommendationTableSectionDto> sections = new ArrayList<>();
        sections.add(section(report, "2. Dados utilizados", "Dados utilizados", "dados_utilizados", null, null));
        sections.add(section(report, "3. Diagnóstico químico", CHEMICAL_DIAGNOSIS_TITLE, "diagnostico_quimico", null, null));
        sections.add(section(report, "4. Diagnóstico físico", "Diagnóstico físico", "diagnostico_fisico", null, null));
        sections.add(section(report, "5. Diagnóstico de salinidade/sodicidade", "Diagnóstico de salinidade/sodicidade", "diagnostico_salinidade_sodicidade", null, null));
        sections.add(section(report, "6. Diagnóstico foliar", FOLIAR_DIAGNOSIS_TITLE, "diagnostico_foliar", null, null));
        sections.add(correctiveSection(report, "Adubação corretiva - Opção 1 - formulados", "adubacao_corretiva_opcao_1", OPTION_CORRECTIVE_FORMULATED, true));
        sections.add(correctiveSection(report, "Adubação corretiva - Opção 2 - adubos simples", "adubacao_corretiva_opcao_2", OPTION_CORRECTIVE_SIMPLE_SOURCES, false));
        sections.add(section(report, "10. Adubação de plantio", "Opção 2 - adubos simples no plantio", "plantio_opcao_2", OPTION_PLANTING_SIMPLE_SOURCES, "alternativa"));
        sections.add(section(report, "11. Adubação de cobertura", "Cobertura opção 2 - adubos simples", "cobertura_opcao_2", OPTION_COVERAGE_SIMPLE_SOURCES, "alternativa"));
        sections.add(section(report, "13. Fertilizantes recomendados", "Fontes selecionadas", "fertilizantes_recomendados", null, null));
        sections.add(section(report, "13.2. Comparativo de custo de oportunidade", "Comparativo de custo de oportunidade", "custo_oportunidade", null, null));
        sections.add(section(report, "15. Memória de cálculo", "Memória de cálculo", "memoria_calculo", null, null));
        return nonEmpty(sections);
    }

    public List<RecommendationTableSectionDto> summarySections(RecommendationModel recommendation) {
        String report = report(recommendation);
        List<RecommendationTableSectionDto> sections = new ArrayList<>();
        sections.add(section(report, "10. Adubação de plantio", "Opção 2 - adubos simples no plantio", "plantio_opcao_2", OPTION_PLANTING_SIMPLE_SOURCES, "alternativa"));
        sections.add(section(report, "11. Adubação de cobertura", "Cobertura opção 2 - adubos simples", "cobertura_opcao_2", OPTION_COVERAGE_SIMPLE_SOURCES, "alternativa"));
        sections.add(section(report, "13. Fertilizantes recomendados", "Adubos simples e formulados", "fertilizantes_recomendados", null, null));
        sections.add(section(report, "13.2. Comparativo de custo de oportunidade", "Comparativo de custo de oportunidade", "custo_oportunidade", null, null));
        sections.add(section(TechnicalRecommendationDocumentSupport.subsection(report, "Fontes orgânicas, organominerais e micronutrientes"),
                null,
                "Fontes orgânicas, organominerais e micronutrientes",
                "fontes_alternativas",
                "complemento",
                "complemento"));
        sections.add(section(report, "12. Balanço nutricional", "Balanço nutricional", "balanco_nutricional", null, null));
        return nonEmpty(sections);
    }

    public List<RecommendationTableSectionDto> directSections(RecommendationModel recommendation, String directReport) {
        List<RecommendationTableSectionDto> sections = new ArrayList<>();
        sections.add(micronutrientLineSection(recommendation));
        sections.add(directPlantingFormulatedLineSection(recommendation));
        sections.add(directCoverageFormulatedLineSection(recommendation));
        sections.add(section(directReport, "Comparativo de custo de oportunidade", "Comparativo de custo de oportunidade"));
        return nonEmpty(sections);
    }

    public List<ShoppingListItemResponseDto> shoppingItems(RecommendationModel recommendation) {
        Double area = resolveArea(recommendation);
        return TechnicalRecommendationDocumentSupport.collectShoppingItems(
                        recommendation,
                        micronutrientFertilizerLines(recommendation),
                        plantingFormulatedFertilizerLines(recommendation),
                        coverageFormulatedFertilizerLines(recommendation),
                        fertilizerResolver)
                .stream()
                .map(item -> ShoppingListItemResponseDto.builder()
                        .inputName(item.getName())
                        .typeGroup(item.getTypeGroup())
                        .phase(item.getPhase())
                        .section(item.getSection())
                        .option(item.getOption())
                        .itemFlag(item.getItemFlag())
                        .quantityKgHa(item.getKgHa())
                        .quantityPerHectare(TechnicalRecommendationDocumentSupport.formatKgHa(item.getKgHa()))
                        .localizedUnit(item.getLocalizedDose())
                        .totalForArea(TechnicalRecommendationDocumentSupport.formatTotal(item.getKgHa(), area))
                        .opportunityCostDecision(item.getOpportunityCostDecision())
                        .build())
                .toList();
    }

    public List<ShoppingListResponseDto.ShoppingListBlockResponseDto> shoppingBlocks(RecommendationModel recommendation) {
        return shoppingBlocks(shoppingItems(recommendation));
    }

    public PurchaseListResponseDto purchaseList(RecommendationModel recommendation) {
        return purchaseList(shoppingItems(recommendation));
    }

    public PurchaseListResponseDto purchaseList(List<ShoppingListItemResponseDto> items) {
        List<ShoppingListItemResponseDto> validItems = validShoppingItems(items);
        return PurchaseListResponseDto.builder()
                .blocks(List.of(
                        purchaseBlock(
                                BLOCK_ACIDITY_CORRECTION,
                                "Correção da acidez",
                                "Calcário, gesso e alternativas de enxofre classificadas para correção da acidez.",
                                List.of(purchaseOption(OPTION_ACIDITY_CORRECTION, "Calcário e gesso",
                                        itemsByOption(validItems, OPTION_ACIDITY_CORRECTION)))),
                        purchaseBlock(
                                BLOCK_CORRECTIVE_FERTILIZATION,
                                "Adubação corretiva",
                                "Alternativas corretivas de P2O5, K2O, formulados, FTE e micronutrientes simples já calculadas.",
                                List.of(
                                        purchaseOption(OPTION_CORRECTIVE_FORMULATED, "SSP, formulado corretivo e FTE",
                                                itemsByOption(validItems, OPTION_CORRECTIVE_FORMULATED)),
                                        purchaseOption(OPTION_CORRECTIVE_SIMPLE_SOURCES, "KCl e micronutrientes simples",
                                                itemsByOption(validItems, OPTION_CORRECTIVE_SIMPLE_SOURCES)))),
                        purchaseBlock(
                                BLOCK_PLANTING,
                                "Plantio",
                                "Opções de plantio agrupadas em formulado com complementos ou adubos simples.",
                                List.of(
                                        purchaseOption(OPTION_PLANTING_FORMULATED, "Opção 1 - formulado",
                                                itemsByOption(validItems, OPTION_PLANTING_FORMULATED)),
                                        purchaseOption(OPTION_PLANTING_SIMPLE_SOURCES, "Opção 2 - adubos simples",
                                                itemsByOption(validItems, OPTION_PLANTING_SIMPLE_SOURCES)))),
                        purchaseBlock(
                                BLOCK_TOPDRESSING,
                                "Cobertura",
                                "Opções de cobertura agrupadas em formulado ou adubos simples.",
                                List.of(
                                        purchaseOption(OPTION_COVERAGE_FORMULATED, "Opção 1 - formulado",
                                                itemsByOption(validItems, OPTION_COVERAGE_FORMULATED)),
                                        purchaseOption(OPTION_COVERAGE_SIMPLE_SOURCES, "Opção 2 - adubos simples",
                                                itemsByOption(validItems, OPTION_COVERAGE_SIMPLE_SOURCES))))
                ))
                .build();
    }

    public List<ShoppingListResponseDto.ShoppingListBlockResponseDto> shoppingBlocks(List<ShoppingListItemResponseDto> items) {
        List<ShoppingListItemResponseDto> validItems = validShoppingItems(items);
        return List.of(
                block(
                        "correcao_acidez",
                        "Correção da Acidez",
                        List.of(option(OPTION_ACIDITY_CORRECTION, "Recomendação única", false,
                                itemsByOption(validItems, OPTION_ACIDITY_CORRECTION)))),
                block(
                        "adubacao_corretiva",
                        "Adubação Corretiva",
                        List.of(
                                option(OPTION_CORRECTIVE_FORMULATED, "Opção 1 - formulados", true,
                                        itemsByOption(validItems, OPTION_CORRECTIVE_FORMULATED)),
                                option(OPTION_CORRECTIVE_SIMPLE_SOURCES, "Opção 2 - adubos simples", true,
                                        itemsByOption(validItems, OPTION_CORRECTIVE_SIMPLE_SOURCES)))),
                block(
                        "plantio",
                        "Plantio",
                        List.of(
                                option(OPTION_PLANTING_FORMULATED, "Opção 1 - formulados", true,
                                        itemsByOption(validItems, OPTION_PLANTING_FORMULATED)),
                                option(OPTION_PLANTING_SIMPLE_SOURCES, "Opção 2 - adubos simples", true,
                                        itemsByOption(validItems, OPTION_PLANTING_SIMPLE_SOURCES)))),
                block(
                        "cobertura",
                        "Cobertura",
                        List.of(
                                option(OPTION_COVERAGE_FORMULATED, "Opção 1 - formulados", true,
                                        itemsByOption(validItems, OPTION_COVERAGE_FORMULATED)),
                                option(OPTION_COVERAGE_SIMPLE_SOURCES, "Opção 2 - adubos simples", true,
                                        itemsByOption(validItems, OPTION_COVERAGE_SIMPLE_SOURCES))))
        );
    }

    public List<String> observations(String text) {
        List<String> observations = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return observations;
        }
        for (String line : text.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("- ")) {
                observations.add(trimmed.substring(2).trim());
            }
        }
        return deduplicate(observations);
    }

    private RecommendationTableSectionDto directPlantingFormulatedLineSection(RecommendationModel recommendation) {
        List<DirectRecommendationPlantingFormulatedFertilizerLineModel> planting = plantingFormulatedFertilizerLines(recommendation);
        if (planting.isEmpty()) {
            return null;
        }

        List<List<String>> rows = new ArrayList<>();
        for (DirectRecommendationPlantingFormulatedFertilizerLineModel line : planting) {
            DirectRecommendationFertilizerResolver.FormulatedMineralFertilizerData fertilizer =
                    fertilizerResolver.formulated(line.getFertilizerId());
            rows.add(List.of(
                    value(line.getPhase()),
                    value(fertilizer.name()),
                    value(line.getRelationUsed()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getDoseKgHa()),
                    displayText(shortText(line.getTechnicalObservation()))));
        }

        return RecommendationTableSectionDto.builder()
                .title("Plantio - Opção 1 - formulados")
                .sectionKey("plantio_opcao_1")
                .option(OPTION_PLANTING_FORMULATED)
                .itemType("alternativa")
                .source(SOURCE_DIRECT_LINES)
                .columns(List.of("Fase", "Formulado", "Relação N-P2O5-K2O", "Dose", "Observação técnica"))
                .rows(rows)
                .technicalObservations(deduplicate(extractLastColumn(rows)))
                .technicalWarnings(deduplicate(extractLastColumn(rows)))
                .build();
    }

    private ShoppingListResponseDto.ShoppingListBlockResponseDto block(
            String code,
            String name,
            List<ShoppingListResponseDto.ShoppingListOptionResponseDto> options) {
        boolean empty = options.stream().allMatch(option -> option.getItems() == null || option.getItems().isEmpty());
        return ShoppingListResponseDto.ShoppingListBlockResponseDto.builder()
                .code(code)
                .name(name)
                .options(options)
                .technicalObservation(empty ? "Nenhum item com dose operacional foi classificado para este bloco." : null)
                .build();
    }

    private PurchaseListBlockResponseDto purchaseBlock(
            String key,
            String title,
            String description,
            List<PurchaseListOptionResponseDto> options) {
        return PurchaseListBlockResponseDto.builder()
                .key(key)
                .title(title)
                .description(description)
                .mutuallyExclusiveOptions(!BLOCK_ACIDITY_CORRECTION.equals(key))
                .options(options)
                .build();
    }

    private PurchaseListOptionResponseDto purchaseOption(
            String key,
            String title,
            List<ShoppingListItemResponseDto> items) {
        return PurchaseListOptionResponseDto.builder()
                .key(key)
                .title(title)
                .items(items.stream().map(this::purchaseItem).toList())
                .build();
    }

    private PurchaseListItemResponseDto purchaseItem(ShoppingListItemResponseDto item) {
        return PurchaseListItemResponseDto.builder()
                .sourceName(item.getInputName())
                .sourceType(displayText(item.getTypeGroup()))
                .nutrientTarget(displayText(item.getTypeGroup()))
                .doseKgHa(item.getQuantityKgHa())
                .areaQuantity(displayText(item.getTotalForArea()))
                .unit("kg/ha")
                .calculationMemory(purchaseCalculationMemory(item))
                .technicalNote(purchaseTechnicalNote(item))
                .build();
    }

    private String purchaseCalculationMemory(ShoppingListItemResponseDto item) {
        List<String> parts = new ArrayList<>();
        parts.add("Dose operacional consolidada: " + TechnicalRecommendationDocumentSupport.formatKgHa(item.getQuantityKgHa()));
        if (item.getTotalForArea() != null && !item.getTotalForArea().isBlank()) {
            parts.add("Total para área: " + item.getTotalForArea());
        }
        if (item.getLocalizedUnit() != null && !item.getLocalizedUnit().isBlank()) {
            parts.add("Dose localizada: " + item.getLocalizedUnit());
        }
        return String.join("; ", parts) + ".";
    }

    private String purchaseTechnicalNote(ShoppingListItemResponseDto item) {
        List<String> parts = new ArrayList<>();
        addIfPresent(parts, "Seção: ", item.getSection());
        addIfPresent(parts, "Opção: ", item.getOption());
        addIfPresent(parts, "Fase: ", item.getPhase());
        addIfPresent(parts, "Classificação: ", item.getItemFlag());
        addIfPresent(parts, "Custo de oportunidade: ", item.getOpportunityCostDecision());
        if (parts.isEmpty()) {
            return "Item estruturado a partir da lista de compras consolidada já calculada.";
        }
        return String.join("; ", parts) + ".";
    }

    private void addIfPresent(List<String> parts, String label, String value) {
        if (value != null && !value.isBlank()) {
            parts.add(label + value.trim());
        }
    }

    private ShoppingListResponseDto.ShoppingListOptionResponseDto option(
            String code,
            String name,
            boolean mutuallyExclusive,
            List<ShoppingListItemResponseDto> items) {
        return ShoppingListResponseDto.ShoppingListOptionResponseDto.builder()
                .code(code)
                .name(name)
                .mutuallyExclusive(mutuallyExclusive)
                .items(items)
                .technicalObservation(items.isEmpty() ? "Nenhum item com dose operacional foi classificado para esta opção." : null)
                .build();
    }

    private List<ShoppingListItemResponseDto> itemsByOption(List<ShoppingListItemResponseDto> items, String option) {
        return items.stream()
                .filter(item -> option.equals(item.getOption()))
                .toList();
    }

    private List<ShoppingListItemResponseDto> validShoppingItems(List<ShoppingListItemResponseDto> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream()
                .filter(item -> item != null
                        && item.getInputName() != null
                        && !item.getInputName().isBlank()
                        && item.getQuantityKgHa() != null
                        && item.getQuantityKgHa() > 0
                        && item.getOption() != null
                        && !item.getOption().isBlank())
                .toList();
    }

    private RecommendationTableSectionDto directCoverageFormulatedLineSection(RecommendationModel recommendation) {
        List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverage = coverageFormulatedFertilizerLines(recommendation);
        if (coverage.isEmpty()) {
            return null;
        }

        List<List<String>> rows = new ArrayList<>();
        for (DirectRecommendationCoverageFormulatedFertilizerLineModel line : coverage) {
            DirectRecommendationFertilizerResolver.FormulatedMineralFertilizerData fertilizer =
                    fertilizerResolver.formulated(line.getFertilizerId());
            rows.add(List.of(
                    value(line.getPhase()),
                    value(fertilizer.name()),
                    value(line.getRelationUsed()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getDoseKgHa()),
                    displayText(shortText(line.getTechnicalObservation()))));
        }

        return RecommendationTableSectionDto.builder()
                .title("Cobertura - Opção 1 - formulados")
                .sectionKey("cobertura_opcao_1")
                .option(OPTION_COVERAGE_FORMULATED)
                .itemType("alternativa")
                .source(SOURCE_DIRECT_LINES)
                .columns(List.of("Fase", "Formulado", "Relação N-P2O5-K2O", "Dose", "Observação técnica"))
                .rows(rows)
                .technicalObservations(deduplicate(extractLastColumn(rows)))
                .technicalWarnings(deduplicate(extractLastColumn(rows)))
                .build();
    }

    private RecommendationTableSectionDto micronutrientLineSection(RecommendationModel recommendation) {
        List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrients = micronutrientFertilizerLines(recommendation);
        if (micronutrients.isEmpty()) {
            return null;
        }

        List<List<String>> rows = new ArrayList<>();
        for (DirectRecommendationMicronutrientFertilizerLineModel line : micronutrients) {
            DirectRecommendationFertilizerResolver.SimpleMineralFertilizerData fertilizer =
                    fertilizerResolver.simple(line.getFertilizerId(), line.getMicronutrient());
            rows.add(List.of(
                    value(line.getMicronutrient()),
                    value(fertilizer.name()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getMicronutrientDoseKgHa()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getFertilizerDoseKgHa()),
                    displayText(shortText(line.getTechnicalObservation()))));
        }

        return RecommendationTableSectionDto.builder()
                .title("Recomendação Direta - micronutrientes")
                .sectionKey("micronutrientes")
                .option(OPTION_PLANTING_FORMULATED)
                .itemType("complemento")
                .source(SOURCE_DIRECT_LINES)
                .columns(List.of("Micronutriente", "Adubo", "Dose micronutriente", "Dose adubo", "Observação técnica"))
                .rows(rows)
                .technicalObservations(deduplicate(extractLastColumn(rows)))
                .technicalWarnings(deduplicate(extractLastColumn(rows)))
                .build();
    }

    private RecommendationTableSectionDto correctiveSection(String report,
                                                            String title,
                                                            String sectionKey,
                                                            String option,
                                                            boolean formulated) {
        String section = TechnicalRecommendationDocumentSupport.section(report, "9. Adubação corretiva");
        List<List<String>> tableRows = TechnicalRecommendationDocumentSupport.tableRows(section).stream()
                .filter(row -> isCorrectiveFormulatedRow(row) == formulated)
                .toList();
        if (tableRows.isEmpty()) {
            return null;
        }
        List<String> observations = deduplicate(extractTechnicalObservations(tableRows));
        return RecommendationTableSectionDto.builder()
                .title(title)
                .sectionKey(sectionKey)
                .option(option)
                .itemType("alternativa")
                .source(SOURCE_TECHNICAL_REPORT)
                .columns(tableHeader(section))
                .rows(tableRows)
                .technicalObservations(observations)
                .technicalWarnings(observations)
                .build();
    }

    private boolean isCorrectiveFormulatedRow(List<String> row) {
        String attribute = row == null || row.isEmpty() ? "" : row.get(0);
        String sourceName = row == null || row.size() < 3 ? "" : row.get(2);
        String normalized = normalize(attribute + " " + sourceName);
        return normalized.contains("formulado")
                || normalized.contains("npk")
                || normalized.contains("fte");
    }

    private RecommendationTableSectionDto section(String report, String heading, String title) {
        return section(report, heading, title, null, null, null);
    }

    private RecommendationTableSectionDto section(String report,
                                                  String heading,
                                                  String title,
                                                  String sectionKey,
                                                  String option,
                                                  String itemType) {
        String section = heading == null ? report : TechnicalRecommendationDocumentSupport.section(report, heading);
        List<List<String>> tableRows = TechnicalRecommendationDocumentSupport.tableRows(section);
        if (tableRows.isEmpty()) {
            return null;
        }
        List<String> observations = deduplicate(extractTechnicalObservations(tableRows));
        List<String> calculationMemory = "memoria_calculo".equals(sectionKey)
                ? rowsAsMemory(tableRows)
                : List.of();
        return RecommendationTableSectionDto.builder()
                .title(title)
                .sectionKey(sectionKey)
                .option(option)
                .itemType(itemType)
                .source(SOURCE_TECHNICAL_REPORT)
                .columns(tableHeader(section))
                .rows(tableRows)
                .technicalObservations(observations)
                .technicalWarnings(observations)
                .calculationMemory(calculationMemory)
                .build();
    }

    private List<String> tableHeader(String section) {
        if (section == null || section.isBlank()) {
            return List.of();
        }
        for (String line : section.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.startsWith("|") || isSeparatorRow(trimmed)) {
                continue;
            }
            return splitRow(trimmed);
        }
        return List.of();
    }

    private List<String> splitRow(String line) {
        String body = line.substring(1, line.endsWith("|") ? line.length() - 1 : line.length());
        List<String> cells = new ArrayList<>();
        for (String cell : body.split("\\|", -1)) {
            cells.add(cell.trim());
        }
        return cells;
    }

    private List<String> extractTechnicalObservations(List<List<String>> rows) {
        List<String> observations = new ArrayList<>();
        for (List<String> row : rows) {
            for (String cell : row) {
                if (cell != null && cell.toLowerCase().contains("observ")) {
                    observations.add(shortText(cell));
                    break;
                }
            }
        }
        return observations;
    }

    private List<String> extractLastColumn(List<List<String>> rows) {
        List<String> observations = new ArrayList<>();
        for (List<String> row : rows) {
            if (!row.isEmpty()) {
                observations.add(shortText(row.get(row.size() - 1)));
            }
        }
        return observations;
    }

    private List<RecommendationTableSectionDto> nonEmpty(List<RecommendationTableSectionDto> sections) {
        return sections.stream()
                .filter(section -> section != null && section.getRows() != null && !section.getRows().isEmpty())
                .toList();
    }

    private String report(RecommendationModel recommendation) {
        return recommendation != null ? recommendation.getTechnicalReport() : null;
    }

    private String value(Object value) {
        return TechnicalRecommendationDocumentSupport.safe(value);
    }

    private String displayText(String value) {
        return value == null ? "" : value;
    }

    private String shortText(String value) {
        if (value == null || value.isBlank() || TechnicalRecommendationDocumentSupport.looksUnavailable(value)) {
            return null;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        int end = normalized.indexOf(". ");
        if (end > 0 && end < 160) {
            normalized = normalized.substring(0, end + 1);
        }
        return normalized.length() <= 220 ? normalized : normalized.substring(0, 217).trim() + "...";
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private List<String> deduplicate(List<String> values) {
        Set<String> unique = new LinkedHashSet<>();
        for (String value : values) {
            if (value == null || value.isBlank() || TechnicalRecommendationDocumentSupport.looksUnavailable(value)) {
                continue;
            }
            unique.add(value.trim());
        }
        return new ArrayList<>(unique);
    }

    private List<String> rowsAsMemory(List<List<String>> rows) {
        List<String> memory = new ArrayList<>();
        for (List<String> row : rows) {
            List<String> cells = row.stream()
                    .filter(cell -> cell != null && !cell.isBlank() && !TechnicalRecommendationDocumentSupport.looksUnavailable(cell))
                    .toList();
            if (!cells.isEmpty()) {
                memory.add(String.join(" | ", cells));
            }
        }
        return deduplicate(memory);
    }

    private boolean isSeparatorRow(String line) {
        return line.replace("|", "").replace(":", "").replace("-", "").trim().isBlank();
    }

    private Double resolveArea(RecommendationModel recommendation) {
        if (recommendation != null && recommendation.getCropUsedAreaInThePlot() != null
                && recommendation.getCropUsedAreaInThePlot() > 0) {
            return recommendation.getCropUsedAreaInThePlot();
        }
        if (recommendation != null && recommendation.getPlot() != null
                && recommendation.getPlot().getArea() != null && recommendation.getPlot().getArea() > 0) {
            return recommendation.getPlot().getArea();
        }
        return null;
    }

    private Optional<DirectRecommendationModel> directRecommendation(RecommendationModel recommendation) {
        if (recommendation == null) {
            return Optional.empty();
        }
        if (recommendation.getDirectRecommendation() != null) {
            return Optional.of(recommendation.getDirectRecommendation());
        }
        if (recommendation.getId() == null) {
            return Optional.empty();
        }
        return directRecommendationRepository.findByRecommendation(recommendation);
    }

    private List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientFertilizerLines(RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(micronutrientFertilizerLineRepository::findAllByDirectRecommendationOrderByIdAsc)
                .orElseGet(List::of);
    }

    private List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines(RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(plantingFormulatedFertilizerLineRepository::findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc)
                .orElseGet(List::of);
    }

    private List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines(RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(coverageFormulatedFertilizerLineRepository::findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc)
                .orElseGet(List::of);
    }

}
