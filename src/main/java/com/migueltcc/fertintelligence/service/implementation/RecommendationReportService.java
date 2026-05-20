package com.migueltcc.fertintelligence.service.implementation;

import org.springframework.stereotype.Service;

@Service
public class RecommendationReportService {

    public String buildTechnicalReport(RecommendationCalculationService.RecommendationCalculationResult result) {
        StringBuilder report = new StringBuilder();

        report.append("1. Cabeçalho e Identificação:\n");
        report.append("Este laudo corresponde a uma recomendação em versão preliminar estrutural.\n");

        report.append("2. Diagnóstico:\n");
        result.getDiagnosticMessages().forEach(item -> report.append("- ").append(item).append("\n"));
        report.append("- ").append(result.getPhysicalAnalysisSummary()).append("\n");
        report.append("- ").append(result.getSoilFertilityAnalysisSummary()).append("\n");
        report.append("- ").append(result.getSaturationExtractAnalysisSummary()).append("\n");
        report.append("- ").append(result.getCropSummary()).append("\n");
        report.append("- ").append(result.getFoliarAnalysisSummary()).append("\n");

        report.append("3. Recomendação de Correção:\n");
        result.getCorrectionMessages().forEach(item -> report.append("- ").append(item).append("\n"));
        report.append("\n");

        report.append("4. Recomendação de Adubação:\n");
        report.append("| Fase | Nutrientes Necessários | Adubo Sugerido | Quantidade | Época e Modo de Aplicação | ");
        report.append("|---|---|---|---|---|\n");
        if (result.getFertilizationRecommendationRows() != null && !result.getFertilizationRecommendationRows().isEmpty()) {
            result.getFertilizationRecommendationRows().forEach(row -> report
                    .append("| ").append(value(row.getPhase()))
                    .append(" | ").append(value(row.getNutrients()))
                    .append(" | ").append(value(row.getSuggestedFertilizer()))
                    .append(" | ").append(row.getFertilizerQuantityKgHa() == null ? "-" : String.format("%.2f kg/ha", row.getFertilizerQuantityKgHa()))
                    .append(" | ").append(value(row.getApplicationMode()))
                    .append(" | \n"));
        } else {
            report.append("| - | - | - | - | - | ");
        }
        report.append("\n");

        report.append("5. Observações Técnicas e Cuidados:\n");
        result.getWarnings().forEach(item -> report.append("- ").append(item).append("\n"));
        report.append("- A lógica agronômica detalhada será implementada nos próximos incrementos.\n");

        report.append("6. Encerramento:\n");
        report.append("Documento preliminar gerado para validação de fluxo e persistência do módulo Recommendation.\n");

        return report.toString();
    }

    private String value(String value) {
        return value == null ? "-" : value.replace("|", "/");
    }
}
