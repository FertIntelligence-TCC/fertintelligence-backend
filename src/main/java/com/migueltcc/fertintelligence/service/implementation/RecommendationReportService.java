package com.migueltcc.fertintelligence.service.implementation;

import org.springframework.stereotype.Service;

@Service
public class RecommendationReportService {

    public String buildTechnicalReport(RecommendationCalculationService.RecommendationCalculationResult result) {
        StringBuilder report = new StringBuilder();

        report.append("1. Cabeçalho e Identificação\n");
        report.append("Este laudo corresponde a uma recomendação em versão preliminar estrutural.\n\n");

        report.append("2. Diagnóstico\n");
        result.getDiagnosticMessages().forEach(item -> report.append("- ").append(item).append("\n"));
        report.append("\n");

        report.append("3. Recomendação de Correção\n");
        result.getCorrectionMessages().forEach(item -> report.append("- ").append(item).append("\n"));
        report.append("\n");

        report.append("4. Recomendação de Adubação\n");
        result.getFertilizationRows().forEach(item -> report.append("- ").append(item).append("\n"));
        report.append("\n");

        report.append("5. Observações Técnicas e Cuidados\n");
        result.getWarnings().forEach(item -> report.append("- ").append(item).append("\n"));
        report.append("- A lógica agronômica detalhada será implementada nos próximos incrementos.\n\n");

        report.append("6. Encerramento\n");
        report.append("Documento preliminar gerado para validação de fluxo e persistência do módulo Recommendation.\n");

        return report.toString();
    }
}
