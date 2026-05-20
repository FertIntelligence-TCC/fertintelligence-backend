package com.migueltcc.fertintelligence.service.implementation;

import org.springframework.stereotype.Service;

@Service
public class RecommendationReportService {

    public String buildTechnicalReport(RecommendationCalculationService.RecommendationCalculationResult result) {
        StringBuilder report = new StringBuilder();

        report.append("1. Cabeçalho e Identificação
");
        report.append("Este laudo corresponde a uma recomendação em versão preliminar estrutural.

");

        report.append("2. Diagnóstico
");
        result.getDiagnosticMessages().forEach(item -> report.append("- ").append(item).append("
"));
        report.append("- ").append(result.getPhysicalAnalysisSummary()).append("
");
        report.append("- ").append(result.getSoilFertilityAnalysisSummary()).append("
");
        report.append("- ").append(result.getSaturationExtractAnalysisSummary()).append("
");
        report.append("- ").append(result.getCropSummary()).append("
");
        report.append("- ").append(result.getFoliarAnalysisSummary()).append("

");

        report.append("3. Recomendação de Correção
");
        result.getCorrectionMessages().forEach(item -> report.append("- ").append(item).append("
"));
        report.append("
");

        report.append("4. Recomendação de Adubação

");
        report.append("| Fase | Nutrientes Necessários | Adubo Sugerido | Quantidade | Época e Modo de Aplicação |
");
        report.append("|---|---|---|---|---|
");
        if (result.getFertilizationRecommendationRows() != null && !result.getFertilizationRecommendationRows().isEmpty()) {
            result.getFertilizationRecommendationRows().forEach(row -> report
                    .append("| ").append(value(row.getPhase()))
                    .append(" | ").append(value(row.getNutrients()))
                    .append(" | ").append(value(row.getSuggestedFertilizer()))
                    .append(" | ").append(row.getFertilizerQuantityKgHa() == null ? "-" : String.format("%.2f kg/ha", row.getFertilizerQuantityKgHa()))
                    .append(" | ").append(value(row.getApplicationMode()))
                    .append(" |
"));
        } else {
            report.append("| - | - | - | - | - |
");
        }
        report.append("
");

        report.append("5. Observações Técnicas e Cuidados
");
        result.getWarnings().forEach(item -> report.append("- ").append(item).append("
"));
        report.append("- A lógica agronômica detalhada será implementada nos próximos incrementos.

");

        report.append("6. Encerramento
");
        report.append("Documento preliminar gerado para validação de fluxo e persistência do módulo Recommendation.
");

        return report.toString();
    }

    private String value(String value) {
        return value == null ? "-" : value.replace("|", "/");
    }
}
