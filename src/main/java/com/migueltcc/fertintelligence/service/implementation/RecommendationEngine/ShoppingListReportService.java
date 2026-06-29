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
        PlotModel plot = recommendation.getPlot();
        Double area = plot != null ? plot.getArea() : null;
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
        TechnicalRecommendationDocumentSupport.appendIdentification(report, recommendation);
        report.append("- Área usada para totalização: ").append(TechnicalRecommendationDocumentSupport.formatArea(area)).append("\n\n");

        report.append("| Insumo | Tipo/grupo | Fase | Quantidade por hectare | Unidade localizada | Total para a área |\n");
        report.append("|---|---|---|---:|---|---:|\n");
        if (items.isEmpty()) {
            report.append("| Não calculado | Não calculado por falta de dados. | Não calculado por falta de dados. | Não calculado por falta de dados. | Não calculado por falta de dados. | Não calculado por falta de dados. |\n\n");
        } else {
            for (TechnicalRecommendationDocumentSupport.ShoppingItem item : items) {
                report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(item.getName()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(item.getTypeGroup()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(item.getPhase()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(item.getKgHa()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.safeCell(item.getLocalizedDose()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.formatTotal(item.getKgHa(), area))
                        .append(" |\n");
            }
            report.append("\n");
        }

        report.append("Observações\n\n");
        report.append("- A lista consolida insumos e doses do laudo técnico persistido e das linhas calculadas da Recomendação Direta.\n");
        report.append("- Quando a área do talhão está indisponível ou inválida, o total não é inferido.\n");
        report.append("- Itens sem dose em kg/ha não são convertidos para compra para evitar conversões não suportadas.\n");
        return report.toString();
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
}
