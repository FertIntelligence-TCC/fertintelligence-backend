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
import java.util.List;
import java.util.Optional;

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
        sections.add(section(report, "2. Dados utilizados", "Dados utilizados"));
        sections.add(section(report, "3. Diagnóstico químico", CHEMICAL_DIAGNOSIS_TITLE));
        sections.add(section(report, "4. Diagnóstico físico", "Diagnóstico físico"));
        sections.add(section(report, "5. Diagnóstico de salinidade/sodicidade", "Diagnóstico de salinidade/sodicidade"));
        sections.add(section(report, "6. Diagnóstico foliar", FOLIAR_DIAGNOSIS_TITLE));
        sections.add(section(report, "9. Adubação corretiva", "Adubação corretiva"));
        sections.add(section(report, "13.2. Comparativo de custo de oportunidade", "Comparativo de custo de oportunidade"));
        return nonEmpty(sections);
    }

    public List<RecommendationTableSectionDto> summarySections(RecommendationModel recommendation) {
        String report = report(recommendation);
        List<RecommendationTableSectionDto> sections = new ArrayList<>();
        sections.add(section(report, "10. Adubação de plantio", "Recomendações de N, P2O5 e K2O - plantio"));
        sections.add(section(report, "11. Adubação de cobertura", "Recomendações de N, P2O5 e K2O - cobertura"));
        sections.add(section(report, "13. Fertilizantes recomendados", "Adubos simples e formulados"));
        sections.add(section(report, "13.2. Comparativo de custo de oportunidade", "Comparativo de custo de oportunidade"));
        sections.add(section(TechnicalRecommendationDocumentSupport.subsection(report, "Fontes orgânicas, organominerais e micronutrientes"),
                null,
                "Fontes orgânicas, organominerais e micronutrientes"));
        sections.add(section(report, "12. Balanço nutricional", "Balanço nutricional"));
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
        return observations;
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
                    value(line.getTechnicalObservation())));
        }
        for (DirectRecommendationCoverageFormulatedFertilizerLineModel line : coverage) {
            rows.add(List.of(
                    value(line.getPhase()),
                    value(line.getFertilizerName()),
                    value(line.getRelationUsed()),
                    TechnicalRecommendationDocumentSupport.formatKgHa(line.getDoseKgHa()),
                    value(line.getTechnicalObservation())));
        }

        return RecommendationTableSectionDto.builder()
                .title("Recomendação Direta - formulados")
                .source(SOURCE_DIRECT_LINES)
                .columns(List.of("Fase", "Formulado", "Relação N-P2O5-K2O", "Dose", "Observação técnica"))
                .rows(rows)
                .technicalObservations(extractLastColumn(rows))
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
                    value(line.getTechnicalObservation())));
        }

        return RecommendationTableSectionDto.builder()
                .title("Recomendação Direta - micronutrientes")
                .source(SOURCE_DIRECT_LINES)
                .columns(List.of("Micronutriente", "Adubo", "Dose micronutriente", "Dose adubo", "Observação técnica"))
                .rows(rows)
                .technicalObservations(extractLastColumn(rows))
                .build();
    }

    private RecommendationTableSectionDto section(String report, String heading, String title) {
        String section = heading == null ? report : TechnicalRecommendationDocumentSupport.section(report, heading);
        List<List<String>> tableRows = TechnicalRecommendationDocumentSupport.tableRows(section);
        if (tableRows.isEmpty()) {
            return null;
        }
        return RecommendationTableSectionDto.builder()
                .title(title)
                .source(SOURCE_TECHNICAL_REPORT)
                .columns(tableHeader(section))
                .rows(tableRows)
                .technicalObservations(extractTechnicalObservations(tableRows))
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
                    observations.add(cell);
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
                observations.add(row.get(row.size() - 1));
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
