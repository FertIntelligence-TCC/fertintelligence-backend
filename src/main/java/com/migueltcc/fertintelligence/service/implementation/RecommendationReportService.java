package com.migueltcc.fertintelligence.service.implementation;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class RecommendationReportService {
    private static final DateTimeFormatter BR_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String buildTechnicalReport(RecommendationCalculationService.RecommendationCalculationResult result) {
        StringBuilder report = new StringBuilder();
        appendTitle(report);
        appendHeader(report, result);
        appendDiagnosis(report, result);
        appendCorrection(report, result);
        appendFertilization(report, result);
        appendTechnicalNotes(report, result);
        appendClosing(report, result);
        return report.toString();
    }

    private void appendTitle(StringBuilder report) {
        report.append("Laudo Técnico de Recomendação Agrícola\n\n");
    }

    private void appendHeader(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("1. Cabeçalho e Identificação\n\n");
        report.append("- Produtor ou solicitante: ").append(safe(result.getRequesterName())).append("\n");
        report.append("- Usuário solicitante: ").append(safe(result.getRequesterUsername())).append("\n");
        report.append("- Propriedade: ").append(safe(result.getPropertyName())).append("\n");
        report.append("- Talhão: ").append(safe(result.getPlotIdentification())).append("\n");
        report.append("- Cultura: ").append(safe(result.getCropName())).append("\n");
        report.append("- Tipo de recomendação: ").append(safe(result.getRecommendationType())).append("\n");
        report.append("- Critério de calagem: ").append(safe(result.getLimingCriteria())).append("\n");
        report.append("- Data de emissão: ").append(formatDate(result.getIssuedAt())).append("\n");
        report.append("\n");
    }

    private void appendDiagnosis(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("2. Diagnóstico do Solo e da Cultura\n\n");
        report.append("- Análise física: ").append(safe(result.getPhysicalAnalysisSummary())).append("\n");
        report.append("- Análise de fertilidade: ").append(safe(result.getSoilFertilityAnalysisSummary())).append("\n");
        report.append("- Extrato de saturação: ").append(safe(result.getSaturationExtractAnalysisSummary())).append("\n");
        report.append("- Análise foliar: ").append(safe(result.getFoliarAnalysisSummary())).append("\n");
        report.append("- Principais alertas ou limitações identificadas:\n");
        appendBulletList(report, result.getDiagnosticMessages(), "Nenhuma limitação adicional foi registrada.");
        report.append("\n");
    }

    private void appendCorrection(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("3. Recomendação de Correção\n\n");
        if (result.getLimingCriteria() != null && !result.getLimingCriteria().isBlank()) {
            report.append("- Critério de calagem selecionado: ").append(result.getLimingCriteria()).append("\n");
        }
        appendBulletList(report, result.getCorrectionMessages(), "Nenhuma recomendação de correção foi calculada nesta etapa.");
        report.append("\n");
    }

    private void appendFertilization(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("4. Recomendação de Adubação\n\n");
        report.append("| Fase da Cultura | Nutrientes Necessários | Sugestão de Adubo | Quantidade do Adubo | Época e Modo de Aplicação |\n");
        report.append("|---|---|---|---|---|\n");
        if (result.getFertilizationRecommendationRows() != null && !result.getFertilizationRecommendationRows().isEmpty()) {
            for (RecommendationCalculationService.FertilizationRecommendationRow row : result.getFertilizationRecommendationRows()) {
                report.append("| ").append(safeCell(row.getPhase()))
                        .append(" | ").append(safeCell(row.getNutrients()))
                        .append(" | ").append(safeCell(row.getSuggestedFertilizer()))
                        .append(" | ").append(formatQuantity(row.getFertilizerQuantityKgHa()))
                        .append(" | ").append(safeCell(row.getApplicationMode()))
                        .append(" |\n");
            }
        } else if (result.getFertilizationRows() != null && !result.getFertilizationRows().isEmpty()) {
            report.append("| Não calculado | ").append(safeCell(result.getFertilizationRows().get(0))).append(" | Não calculado | Não calculado | Não calculado |\n");
        } else {
            report.append("| Não calculado | Não calculado | Não calculado | Não calculado | Não calculado |\n");
        }
        report.append("\n");
    }

    private void appendTechnicalNotes(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("5. Observações Técnicas e Cuidados\n\n");
        appendBulletList(report, result.getWarnings(), "Nenhum alerta adicional foi registrado.");
        report.append("\n");
    }

    private void appendClosing(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("6. Encerramento\n\n");
        report.append("Data de emissão: ").append(formatDate(result.getIssuedAt())).append("\n\n");
    }

    private String safe(Object value) {
        if (value == null) return "Não informado";
        String asText = String.valueOf(value).trim();
        return asText.isEmpty() ? "Não informado" : asText;
    }

    private String safeCell(String value) {
        return safe(value).replace("|", "/");
    }

    private String formatDate(LocalDateTime date) {
        return (date == null ? LocalDateTime.now() : date).format(BR_DATE_TIME);
    }

    private String formatQuantity(Double value) {
        return value == null ? "Não calculado" : String.format(Locale.US, "%.2f kg/ha", value);
    }

    private void appendBulletList(StringBuilder report, List<String> items, String emptyMessage) {
        List<String> values = items == null ? new ArrayList<>() : items;
        if (values.isEmpty()) {
            report.append("- ").append(emptyMessage).append("\n");
            return;
        }
        for (String item : values) report.append("- ").append(safe(item)).append("\n");
    }
}
