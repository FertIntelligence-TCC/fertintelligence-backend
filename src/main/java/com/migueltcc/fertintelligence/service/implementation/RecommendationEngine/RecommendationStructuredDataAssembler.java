package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.dto.recommendation.RecommendationTableSectionDto;
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RecommendationStructuredDataAssembler {

    private static final String SOURCE_TECHNICAL_REPORT = "technical_report";
    private static final String SOURCE_DIRECT_LINES = "direct_recommendation_lines";
    private static final String SOURCE_SHOPPING_ITEMS = "shopping_items";
    private static final String CHEMICAL_DIAGNOSIS_TITLE = "Diagnóstico químico";
    private static final String FOLIAR_DIAGNOSIS_TITLE = "Diagnóstico foliar";

    private final DirectRecommendationRepository directRecommendationRepository;
    private final DirectRecommendationMicronutrientFertilizerLineRepository micronutrientFertilizerLineRepository;
    private final DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingFormulatedFertilizerLineRepository;
    private final DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageFormulatedFertilizerLineRepository;

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
        sections.add(section(report, "9. Adubação corretiva", "Adubação corretiva", "adubacao_corretiva", "adubacao_corretiva", "opcional"));
        sections.add(section(report, "10. Adubação de plantio", "Opção 2 - fontes simples no plantio", "plantio_opcao_2", "opcao_2_fontes_simples", "alternativa"));
        sections.add(section(report, "11. Adubação de cobertura", "Cobertura opção 2 - fontes simples", "cobertura_opcao_2", "cobertura_opcao_2", "alternativa"));
        sections.add(section(report, "13. Fertilizantes recomendados", "Fontes selecionadas", "fertilizantes_recomendados", null, null));
        sections.add(section(report, "13.2. Comparativo de custo de oportunidade", "Comparativo de custo de oportunidade", "custo_oportunidade", null, null));
        sections.add(section(report, "15. Memória de cálculo", "Memória de cálculo", "memoria_calculo", null, null));
        return nonEmpty(sections);
    }

    public List<RecommendationTableSectionDto> summarySections(RecommendationModel recommendation) {
        String report = report(recommendation);
        List<RecommendationTableSectionDto> sections = new ArrayList<>();
        sections.add(section(report, "10. Adubação de plantio", "Opção 2 - fontes simples no plantio", "plantio_opcao_2", "opcao_2_fontes_simples", "alternativa"));
        sections.add(section(report, "11. Adubação de cobertura", "Cobertura opção 2 - fontes simples", "cobertura_opcao_2", "cobertura_opcao_2", "alternativa"));
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
        sections.add(directLineSection(recommendation));
        sections.add(section(directReport, "Comparativo de custo de oportunidade", "Comparativo de custo de oportunidade"));
        return nonEmpty(sections);
    }

    public List<ShoppingListItemResponseDto> shoppingItems(RecommendationModel recommendation) {
        Double area = resolveArea(recommendation);
        return TechnicalRecommendationDocumentSupport.collectShoppingItems(
                        recommendation,
                        micronutrientFertilizerLines(recommendation),
                        plantingFormulatedFertilizerLines(recommendation),
                        coverageFormulatedFertilizerLines(recommendation))
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

    private RecommendationTableSectionDto directLineSection(RecommendationModel recommendation) {
        List<DirectRecommendationPlantingFormulatedFertilizerLineModel> planting = plantingFormulatedFertilizerLines(recommendation);
        List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverage = coverageFormulatedFertilizerLines(recommendation);
        if (planting.isEmpty() && coverage.isEmpty()) {
            return null;
        }

        List<List<String>> rows = new ArrayList<>();
        for (DirectRecommendationPlantingFormulatedFertilizerLineModel line : planting) {
            rows.add(List.of(
                    value(line.getPhase()),
                    value(line.getFertilizerName()),
                    value(line.getRelationUsed()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getDoseKgHa()),
                    displayText(shortText(line.getTechnicalObservation()))));
        }
        for (DirectRecommendationCoverageFormulatedFertilizerLineModel line : coverage) {
            rows.add(List.of(
                    value(line.getPhase()),
                    value(line.getFertilizerName()),
                    value(line.getRelationUsed()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getDoseKgHa()),
                    displayText(shortText(line.getTechnicalObservation()))));
        }

        return RecommendationTableSectionDto.builder()
                .title("Recomendação Direta - formulados")
                .sectionKey("plantio_cobertura_opcao_1")
                .option("opcao_1_formulados")
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
            rows.add(List.of(
                    value(line.getMicronutrient()),
                    value(line.getFertilizerName()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getMicronutrientDoseKgHa()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getFertilizerDoseKgHa()),
                    displayText(shortText(line.getTechnicalObservation()))));
        }

        return RecommendationTableSectionDto.builder()
                .title("Recomendação Direta - micronutrientes")
                .sectionKey("micronutrientes")
                .option("complemento")
                .itemType("complemento")
                .source(SOURCE_DIRECT_LINES)
                .columns(List.of("Micronutriente", "Adubo", "Dose micronutriente", "Dose adubo", "Observação técnica"))
                .rows(rows)
                .technicalObservations(deduplicate(extractLastColumn(rows)))
                .technicalWarnings(deduplicate(extractLastColumn(rows)))
                .build();
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
