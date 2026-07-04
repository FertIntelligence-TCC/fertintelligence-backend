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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingListReportService {

    private final DirectRecommendationRepository directRecommendationRepository;
    private final DirectRecommendationMicronutrientFertilizerLineRepository micronutrientFertilizerLineRepository;
    private final DirectRecommendationPlantingFormulatedFertilizerLineRepository plantingFormulatedFertilizerLineRepository;
    private final DirectRecommendationCoverageFormulatedFertilizerLineRepository coverageFormulatedFertilizerLineRepository;

    public String build(RecommendationModel recommendation) {
        AreaResolution areaResolution = resolveArea(recommendation);
        Double area = areaResolution.area();
        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(
                        recommendation,
                        micronutrientFertilizerLines(recommendation),
                        plantingFormulatedFertilizerLines(recommendation),
                        coverageFormulatedFertilizerLines(recommendation));

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

        report.append("| Insumo | Tipo/grupo | Fase | Quantidade por hectare | Unidade localizada | Total para a área | Decisão de custo |\n");
        report.append("|---|---|---|---:|---|---:|---|\n");
        if (items.isEmpty()) {
            report.append("| Não calculado | Não calculado por falta de dados. | Não calculado por falta de dados. | Não calculado por falta de dados. | Não calculado por falta de dados. | Não calculado por falta de dados. | Não calculado por falta de dados. |\n\n");
        } else {
            for (TechnicalRecommendationDocumentSupport.ShoppingItem item : items) {
                report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(item.getName()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(item.getTypeGroup()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(item.getPhase()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(item.getKgHa()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(item.getLocalizedDose()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.formatTotal(item.getKgHa(), area))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(item.getOpportunityCostDecision()))
                        .append(" |\n");
            }
            report.append("\n");
        }

        report.append("Observações\n\n");
        report.append("- A lista consolida insumos e doses do laudo técnico persistido e das linhas calculadas da Recomendação Direta.\n");
        report.append("- ").append(areaResolution.observation()).append("\n");
        if (!hasValidPlantingDate(recommendation)) {
            report.append("- Data de plantio da cultura usada na recomendação indisponível; o campo foi mantido como não informado.\n");
        }
        report.append("- Itens sem dose em kg/ha não são convertidos para compra para evitar conversões não suportadas.\n");
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
