package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingListReportService {

    public String build(RecommendationModel recommendation) {
        PlotModel plot = recommendation.getPlot();
        Double area = plot != null ? plot.getArea() : null;
        List<TechnicalRecommendationDocumentSupport.ShoppingItem> items =
                TechnicalRecommendationDocumentSupport.collectShoppingItems(recommendation);

        StringBuilder report = new StringBuilder();
        TechnicalRecommendationDocumentSupport.appendStyle(report);
        TechnicalRecommendationDocumentSupport.appendInstitutionalHeader(report);
        report.append("# Lista de Compras de insumos/ha\n\n");
        TechnicalRecommendationDocumentSupport.appendIdentification(report, recommendation);
        report.append("- Área usada para totalização: ").append(TechnicalRecommendationDocumentSupport.formatArea(area)).append("\n\n");

        report.append("| Insumo | Quantidade por hectare | Total para a área |\n");
        report.append("|---|---:|---:|\n");
        if (items.isEmpty()) {
            report.append("| Não calculado | Não calculado por falta de dados. | Não calculado por falta de dados. |\n\n");
        } else {
            for (TechnicalRecommendationDocumentSupport.ShoppingItem item : items) {
                report.append("| ").append(TechnicalRecommendationDocumentSupport.safeCell(item.getName()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.formatKgHa(item.getKgHa()))
                        .append(" | ").append(TechnicalRecommendationDocumentSupport.formatTotal(item.getKgHa(), area))
                        .append(" |\n");
            }
            report.append("\n");
        }

        report.append("## Observações\n\n");
        report.append("- A lista consolida apenas insumos e doses que aparecem no laudo técnico persistido da recomendação.\n");
        report.append("- Quando a área do talhão está indisponível ou inválida, o total não é inferido.\n");
        report.append("- Itens sem dose em kg/ha não são convertidos para compra para evitar conversões não suportadas.\n");
        return report.toString();
    }
}
