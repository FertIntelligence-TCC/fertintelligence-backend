package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.repository.DirectRecommendationCoverageFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationMicronutrientFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationPlantingFormulatedFertilizerLineRepository;
import com.migueltcc.fertintelligence.repository.DirectRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShoppingListReportService {

    private final DirectRecommendationRepository directRecommendationRepository;
    private final DirectRecommendationMicronutrientFertilizerLineRepository micronutrientFertilizerLineRepository;
    private final DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingFormulatedFertilizerLineRepository;
    private final DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageFormulatedFertilizerLineRepository;
    private final DirectRecommendationFertilizerResolver fertilizerResolver;

    public String build(RecommendationModel recommendation) {
        AreaResolution areaResolution = resolveArea(recommendation);
        Double area = areaResolution.area();
        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(
                        recommendation,
                        micronutrientFertilizerLines(recommendation),
                        plantingFormulatedFertilizerLines(recommendation),
                        coverageFormulatedFertilizerLines(recommendation),
                        fertilizerResolver);

        StringBuilder report = new StringBuilder();
        TechnicalRecommendationDocumentSupport.appendStyle(report);
        TechnicalRecommendationDocumentSupport.appendInstitutionalHeader(report);
        report.append("Lista de Compras de insumos/ha\n\n");
        TechnicalRecommendationDocumentSupport.appendIdentification(
                report,
                recommendation,
                area,
                recommendation != null ? recommendation.getCropPlantingDate() : null);
        report.append("- Área usada para totalização: ").append(TechnicalRecommendationDocumentSupport.formatArea(area)).append("\n\n");

        if (items.isEmpty()) {
            report.append("Aviso técnico: lista de compras não gerou itens com dose operacional persistida.\n\n");
        } else {
            appendBlock(report, "BLOCO 1 - Correção da Acidez",
                    itemsInSections(items, TechnicalRecommendationDocumentSupport.SECTION_ACIDITY_CORRECTION), area);
            appendBlock(report, "BLOCO 2 - Adubação Corretiva",
                    itemsInSections(items, TechnicalRecommendationDocumentSupport.SECTION_CORRECTIVE_FERTILIZATION), area);
            appendBlock(report, "BLOCO 3 - Plantio / Opção 1 - formulados, S e micronutrientes",
                    itemsInSections(items, TechnicalRecommendationDocumentSupport.SECTION_PLANTING_OPTION_1), area);
            appendBlock(report, "BLOCO 3 - Plantio / Opção 2 - adubos simples",
                    itemsInSections(items, TechnicalRecommendationDocumentSupport.SECTION_PLANTING_OPTION_2), area);
            appendBlock(report, "BLOCO 4 - Cobertura / Opção 1 - formulados e complementações",
                    itemsInSections(items, TechnicalRecommendationDocumentSupport.SECTION_COVERAGE_OPTION_1), area);
            appendBlock(report, "BLOCO 4 - Cobertura / Opção 2 - adubos simples",
                    itemsInSections(items, TechnicalRecommendationDocumentSupport.SECTION_COVERAGE_OPTION_2), area);
        }

        report.append("Observações\n\n");
        report.append("- A lista consolida insumos e doses do laudo técnico persistido e das linhas calculadas da Recomendação Direta.\n");
        report.append("- ").append(areaResolution.observation()).append("\n");
        if (!hasValidPlantingDate(recommendation)) {
            report.append("- Data de plantio da cultura usada na recomendação indisponível; campo omitível no frontend.\n");
        }
        report.append("- Itens sem dose em kg/ha não são convertidos para compra para evitar conversões não suportadas.\n");
        report.append("- Itens com flag alternativa representam opções incompatíveis e devem ser filtrados por opção antes da compra.\n");
        return report.toString();
    }

    private AreaResolution resolveArea(RecommendationModel recommendation) {
        if (recommendation != null && isPositive(recommendation.getCropUsedAreaInThePlot())) {
            return new AreaResolution(
                    recommendation.getCropUsedAreaInThePlot(),
                    "Área de totalização obtida da área da cultura considerada na recomendação.");
        }
        PlotModel plot = recommendation != null ? recommendation.getPlot() : null;
        if (plot != null && isPositive(plot.getArea())) {
            return new AreaResolution(
                    plot.getArea(),
                    "Área da cultura usada na recomendação indisponível; totalização feita pela área do talhão como fallback legado.");
        }
        return new AreaResolution(
                null,
                "Área da cultura usada na recomendação e área do talhão indisponíveis ou inválidas; o total não foi inferido.");
    }

    private boolean isPositive(Double value) {
        return value != null && value > 0;
    }

    private boolean hasValidPlantingDate(RecommendationModel recommendation) {
        if (recommendation == null || recommendation.getCropPlantingDate() == null) {
            return false;
        }
        return recommendation.getCropPlantingDate().getDay() > 0
                && recommendation.getCropPlantingDate().getMonth() > 0
                && recommendation.getCropPlantingDate().getYear() > 0;
    }

    private List<TechnicalRecommendationDocumentSupport.ShoppingItem> itemsInSections(
            List<TechnicalRecommendationDocumentSupport.ShoppingItem> items,
            String... sections) {
        List<TechnicalRecommendationDocumentSupport.ShoppingItem> selected = new ArrayList<>();
        for (TechnicalRecommendationDocumentSupport.ShoppingItem item : items) {
            for (String section : sections) {
                if (section.equals(item.getSection())) {
                    selected.add(item);
                    break;
                }
            }
        }
        return selected;
    }

    private void appendBlock(StringBuilder report,
                             String title,
                             List<TechnicalRecommendationDocumentSupport.ShoppingItem> items,
                             Double area) {
        report.append(title).append("\n\n");
        if (items.isEmpty()) {
            report.append("Aviso técnico: nenhum item com dose operacional foi classificado para este bloco.\n\n");
            return;
        }
        for (Map.Entry<String, List<TechnicalRecommendationDocumentSupport.ShoppingItem>> group : groupedByOptionAndPhase(items).entrySet()) {
            if (!group.getKey().isBlank()) {
                report.append("Grupo: ").append(group.getKey()).append("\n\n");
            }
            appendItemsTable(report, group.getValue(), area);
        }
    }

    private Map<String, List<TechnicalRecommendationDocumentSupport.ShoppingItem>> groupedByOptionAndPhase(
            List<TechnicalRecommendationDocumentSupport.ShoppingItem> items) {
        Map<String, List<TechnicalRecommendationDocumentSupport.ShoppingItem>> grouped = new LinkedHashMap<>();
        for (TechnicalRecommendationDocumentSupport.ShoppingItem item : items) {
            String key = cell(item.getOption());
            String phase = cell(item.getPhase());
            if (!phase.isBlank()) {
                key = key.isBlank() ? phase : key + " / " + phase;
            }
            grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(item);
        }
        return grouped;
    }

    private void appendItemsTable(StringBuilder report,
                                  List<TechnicalRecommendationDocumentSupport.ShoppingItem> items,
                                  Double area) {
        report.append("| Insumo | Tipo/grupo | Seção | Opção | Flag | Fase | Quantidade por hectare | Unidade localizada | Total para a área | Decisão de custo |\n");
        report.append("|---|---|---|---|---|---|---:|---|---:|---|\n");
        for (TechnicalRecommendationDocumentSupport.ShoppingItem item : items) {
            report.append("| ").append(cell(item.getName()))
                    .append(" | ").append(cell(item.getTypeGroup()))
                    .append(" | ").append(cell(item.getSection()))
                    .append(" | ").append(cell(item.getOption()))
                    .append(" | ").append(cell(item.getItemFlag()))
                    .append(" | ").append(cell(item.getPhase()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(item.getKgHa()))
                    .append(" | ").append(cell(item.getLocalizedDose()))
                    .append(" | ").append(TechnicalRecommendationDocumentSupport.formatTotal(item.getKgHa(), area))
                    .append(" | ").append(cell(item.getOpportunityCostDecision()))
                    .append(" |\n");
        }
        report.append("\n");
    }

    private String cell(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.replace("|", "/").replace("\n", " ").trim();
    }

    private List<DirectRecommendationMicronutrientFertilizerLineModel> micronutrientFertilizerLines(
            RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(micronutrientFertilizerLineRepository::findAllByDirectRecommendationOrderByIdAsc)
                .orElseGet(List::of);
    }

    private List<DirectRecommendationPlantingFormulatedFertilizerLineModel> plantingFormulatedFertilizerLines(
            RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(plantingFormulatedFertilizerLineRepository::findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc)
                .orElseGet(List::of);
    }

    private List<DirectRecommendationCoverageFormulatedFertilizerLineModel> coverageFormulatedFertilizerLines(
            RecommendationModel recommendation) {
        return directRecommendation(recommendation)
                .map(coverageFormulatedFertilizerLineRepository::findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc)
                .orElseGet(List::of);
    }

    private java.util.Optional<DirectRecommendationModel> directRecommendation(RecommendationModel recommendation) {
        if (recommendation == null || recommendation.getId() == null) {
            return java.util.Optional.empty();
        }
        return directRecommendationRepository.findByRecommendation(recommendation);
    }

    private record AreaResolution(Double area, String observation) {
    }
}
