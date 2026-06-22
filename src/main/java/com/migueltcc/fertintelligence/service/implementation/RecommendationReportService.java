package com.migueltcc.fertintelligence.service.implementation;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        appendPhysicalDiagnosis(report, result);
        appendChemicalDiagnosis(report, result);
        appendSalinityDiagnosis(report, result);
    }

    private void appendPhysicalDiagnosis(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("Diagnóstico Físico do Solo\n\n");
        report.append("| Atributo | Valor analisado | Unidade | Observação técnica |\n");
        report.append("|---|---|---|---|\n");
        if (result.getSoilPhysicalDiagnosis() == null || result.getSoilPhysicalDiagnosis().isEmpty()) {
            report.append("| Não calculado | Não informado | Não informado | Dados físicos insuficientes ou ausentes no extrato de análise física selecionado. |\n\n");
            return;
        }
        for (RecommendationCalculationService.SoilPhysicalDiagnosisItem item : result.getSoilPhysicalDiagnosis()) {
            report.append("| ").append(safeCell(item.getAttribute()))
                    .append(" | ").append(formatAnalyzedValue(item.getAnalyzedValue()))
                    .append(" | ").append(safeCell(item.getUnit()))
                    .append(" | ").append(safeCell(item.getTechnicalObservation()))
                    .append(" |\n");
        }
        report.append("\n");
    }

    private void appendChemicalDiagnosis(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("Diagnóstico Químico da Fertilidade do Solo\n\n");
        report.append("| Atributo | Valor analisado | Unidade | Interpretação | Faixa ou critério usado | Observação técnica |\n");
        report.append("|---|---|---|---|---|---|\n");
        if (result.getSoilChemicalDiagnosis() == null || result.getSoilChemicalDiagnosis().isEmpty()) {
            report.append("| Não calculado | Não informado | Não informado | Não classificado | Não informado | Diagnóstico químico não foi calculado nesta etapa. |\n\n");
            return;
        }
        for (RecommendationCalculationService.SoilChemicalDiagnosisItem item : result.getSoilChemicalDiagnosis()) {
            report.append("| ").append(safeCell(item.getAttribute()))
                    .append(" | ").append(formatAnalyzedValue(item.getAnalyzedValue()))
                    .append(" | ").append(safeCell(item.getUnit()))
                    .append(" | ").append(safeCell(item.getInterpretation()))
                    .append(" | ").append(safeCell(item.getUsedCriterion()))
                    .append(" | ").append(safeCell(item.getTechnicalObservation()))
                    .append(" |\n");
        }
        report.append("\n");
    }

    private void appendSalinityDiagnosis(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("Diagnóstico de Salinidade e Sodicidade\n\n");
        report.append("| Atributo | Valor analisado | Unidade | Interpretação | Faixa ou critério usado | Observação técnica |\n");
        report.append("|---|---|---|---|---|---|\n");
        if (result.getSoilSalinityDiagnosis() == null || result.getSoilSalinityDiagnosis().isEmpty()) {
            report.append("| Não calculado | Não informado | Não informado | Não classificado | Não informado | Dados de extrato de saturação e/ou PST insuficientes para diagnóstico. |\n\n");
            return;
        }
        for (RecommendationCalculationService.SoilSalinityDiagnosisItem item : result.getSoilSalinityDiagnosis()) {
            report.append("| ").append(safeCell(item.getAttribute()))
                    .append(" | ").append(formatAnalyzedValue(item.getAnalyzedValue()))
                    .append(" | ").append(safeCell(item.getUnit()))
                    .append(" | ").append(safeCell(item.getInterpretation()))
                    .append(" | ").append(safeCell(item.getUsedCriterion()))
                    .append(" | ").append(safeCell(item.getTechnicalObservation()))
                    .append(" |\n");
        }
        report.append("\n");
    }

    private void appendCorrection(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("3. Recomendação de Correção\n\n");
        if (result.getLimingCriteria() != null && !result.getLimingCriteria().isBlank()) {
            report.append("- Critério de calagem selecionado: ").append(result.getLimingCriteria()).append("\n");
        }
        appendBulletList(report, result.getCorrectionMessages(), "Nenhuma recomendação de correção foi calculada nesta etapa.");
        report.append("\n");
        appendLimingRequirement(report, result);
        appendGypsumRequirement(report, result);
    }

    private void appendLimingRequirement(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("Calagem\n\n");
        RecommendationCalculationService.LimingRequirementResult liming = result.getLimingRequirement();
        if (liming == null) {
            report.append("- Necessidade de calagem: Não calculada.\n");
            report.append("- Aviso técnico: Resultado estruturado de calagem não foi produzido pelo cálculo.\n\n");
            return;
        }

        report.append("- Critério selecionado: ").append(safe(liming.getSelectedCriteria())).append("\n");
        report.append("- Fórmula usada: ").append(safe(liming.getFormula())).append("\n");
        report.append("- Fonte de calcário/corretivo: ").append(safe(liming.getLimestoneSource())).append("\n");
        report.append("- Dose teórica de calagem: ");
        appendLimingDose(report, liming.getTheoreticalRequirement(), liming.getUnit());
        report.append("\n");
        report.append("- PRNT utilizado: ");
        if (liming.getPrnt() == null) {
            report.append("Não informado");
        } else {
            report.append(String.format(Locale.US, "%.2f%%", liming.getPrnt()));
        }
        report.append("\n");
        report.append("- Dose corrigida por PRNT: ");
        appendLimingDose(report, liming.getCorrectedRequirement(), liming.getUnit());
        report.append("\n");

        report.append("| Valor de entrada | Valor |\n");
        report.append("|---|---|\n");
        Map<String, Double> inputValues = liming.getInputValues();
        if (inputValues == null || inputValues.isEmpty()) {
            report.append("| Não informado | Não informado |\n");
        } else {
            for (Map.Entry<String, Double> entry : inputValues.entrySet()) {
                report.append("| ").append(safeCell(entry.getKey()))
                        .append(" | ").append(formatAnalyzedValue(entry.getValue()))
                        .append(" |\n");
            }
        }
        report.append("\n");

        report.append("- Avisos de calagem:\n");
        appendBulletList(report, liming.getWarnings(), "Nenhum aviso específico de calagem foi registrado.");
        report.append("\n");
    }

    private void appendGypsumRequirement(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("Gessagem\n\n");
        RecommendationCalculationService.GypsumRequirementResult gypsum = result.getGypsumRequirement();
        if (gypsum == null) {
            report.append("- Necessidade de gessagem: Não calculada.\n");
            report.append("- Aviso técnico: Resultado estruturado de gessagem não foi produzido pelo cálculo.\n\n");
            return;
        }

        report.append("- Necessidade de gessagem: ").append(formatGypsumNeed(gypsum.getNeeded())).append("\n");
        report.append("- Critério usado: ").append(safe(gypsum.getCriterion())).append("\n");
        report.append("- Dose de gesso: ");
        appendLimingDose(report, gypsum.getCalculatedRequirement(), gypsum.getUnit());
        report.append("\n");
        report.append("- Justificativa: ").append(safe(gypsum.getJustification())).append("\n");

        report.append("| Valor de entrada | Valor |\n");
        report.append("|---|---|\n");
        Map<String, Double> inputValues = gypsum.getInputValues();
        if (inputValues == null || inputValues.isEmpty()) {
            report.append("| Não informado | Não informado |\n");
        } else {
            for (Map.Entry<String, Double> entry : inputValues.entrySet()) {
                report.append("| ").append(safeCell(entry.getKey()))
                        .append(" | ").append(formatAnalyzedValue(entry.getValue()))
                        .append(" |\n");
            }
        }
        report.append("\n");

        report.append("- Avisos de gessagem:\n");
        appendBulletList(report, gypsum.getWarnings(), "Nenhum aviso específico de gessagem foi registrado.");
        report.append("\n");
    }

    private void appendFertilization(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("4. Recomendação de Adubação\n\n");
        report.append("| Fase da Cultura | Nutrientes Necessários | Sugestão de Adubo | Quantidade do Adubo | Época e Modo de Aplicação |\n");
        report.append("|---|---|---|---|---|\n");
        if (result.getFertilizationRecommendationRows() != null && !result.getFertilizationRecommendationRows().isEmpty()) {
            for (RecommendationCalculationService.FertilizationRecommendationRow row : result.getFertilizationRecommendationRows()) {
                if ("Balanço global NPK".equals(row.getPhase())) continue;
                report.append("| ").append(safeCell(row.getPhase()))
                        .append(" | ").append(safeCell(row.getNutrients()))
                        .append(" | ").append(safeCell(row.getSuggestedFertilizer()))
                        .append(" | ").append(formatQuantity(row.getFertilizerQuantityKgHa()))
                        .append(" | ").append(safeCell(joinApplicationDetails(row)))
                        .append(" |\n");
            }
        } else if (result.getFertilizationRows() != null && !result.getFertilizationRows().isEmpty()) {
            report.append("| Não calculado | ").append(safeCell(result.getFertilizationRows().get(0))).append(" | Não calculado | Não calculado | Não calculado |\n");
        } else {
            report.append("| Não calculado | Não calculado | Não calculado | Não calculado | Não calculado |\n");
        }
        report.append("\n");
        appendCommercialFertilizerCalculationMemory(report, result);
        appendNutrientBalance(report, result);
    }

    private void appendCommercialFertilizerCalculationMemory(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("Memória de Cálculo do Fertilizante Comercial\n\n");
        report.append("| Fase | Fertilizante | Nutriente limitante/alvo | Necessidade alvo | Concentração do produto | Dose calculada | Fornecido N/P2O5/K2O | Déficit ou excedente N/P2O5/K2O |\n");
        report.append("|---|---|---|---|---|---|---|---|\n");
        if (result.getFertilizationRecommendationRows() == null || result.getFertilizationRecommendationRows().isEmpty()) {
            report.append("| Não calculado | Não calculado | Não identificado | Não calculado | Não calculado | Não calculado | Não calculado | Não calculado |\n\n");
            return;
        }
        boolean hasCommercialRow = false;
        for (RecommendationCalculationService.FertilizationRecommendationRow row : result.getFertilizationRecommendationRows()) {
            if ("Balanço global NPK".equals(row.getPhase())) continue;
            hasCommercialRow = true;
            report.append("| ").append(safeCell(row.getPhase()))
                    .append(" | ").append(safeCell(row.getSuggestedFertilizer()))
                    .append(" | ").append(safeCell(row.getLimitingNutrient()))
                    .append(" | ").append(formatQuantity(row.getTargetNeedKgHa()))
                    .append(" | ").append(formatPercent(row.getProductConcentrationPercent()))
                    .append(" | ").append(formatQuantity(row.getFertilizerQuantityKgHa()))
                    .append(" | ").append(formatNpk(row.getProvidedN(), row.getProvidedP2O5(), row.getProvidedK2O()))
                    .append(" | ").append(formatSignedNpk(row.getBalanceN(), row.getBalanceP2O5(), row.getBalanceK2O()))
                    .append(" |\n");
        }
        if (!hasCommercialRow) {
            report.append("| Não calculado | Não calculado | Não identificado | Não calculado | Não calculado | Não calculado | Não calculado | Não calculado |\n");
        }
        report.append("\n");
    }

    private void appendNutrientBalance(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("Balanço Global NPK\n\n");
        report.append("| Nutriente | Necessidade total | Fornecido no plantio | Recomendado em cobertura | Fornecido em cobertura | Fornecido total | Saldo final | Situação |\n");
        report.append("|---|---|---|---|---|---|---|---|\n");
        if (result.getNutrientBalanceRows() == null || result.getNutrientBalanceRows().isEmpty()) {
            report.append("| Não calculado | Não calculado | Não calculado | Não calculado | Não calculado | Não calculado | Não calculado | Dados insuficientes |\n\n");
            return;
        }
        for (RecommendationCalculationService.NutrientBalanceRow row : result.getNutrientBalanceRows()) {
            report.append("| ").append(safeCell(row.getNutrient()))
                    .append(" | ").append(formatQuantity(row.getRequiredTotalKgHa()))
                    .append(" | ").append(formatQuantity(row.getProvidedByPlantingKgHa()))
                    .append(" | ").append(formatQuantity(row.getRecommendedCoverageKgHa()))
                    .append(" | ").append(formatQuantity(row.getProvidedByCoverageKgHa()))
                    .append(" | ").append(formatQuantity(row.getProvidedTotalKgHa()))
                    .append(" | ").append(formatSignedQuantity(row.getFinalBalanceKgHa()))
                    .append(" | ").append(safeCell(row.getStatus()))
                    .append(" |\n");
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

    private void appendLimingDose(StringBuilder report, Double value, String unit) {
        if (value == null) {
            report.append("Não calculada");
            return;
        }
        report.append(String.format(Locale.US, "%.2f %s", value, safe(unit)));
    }

    private String formatSignedQuantity(Double value) {
        return value == null ? "Não calculado" : String.format(Locale.US, "%+.2f kg/ha", value);
    }

    private String formatAnalyzedValue(Double value) {
        return value == null ? "Não informado" : String.format(Locale.US, "%.2f", value);
    }

    private String joinApplicationDetails(RecommendationCalculationService.FertilizationRecommendationRow row) {
        List<String> details = new ArrayList<>();
        details.add(row.getApplicationMode());
        if (row.getProvidedN() != null || row.getProvidedP2O5() != null || row.getProvidedK2O() != null) {
            details.add(String.format(Locale.US, "Fornecido: N %.2f, P2O5 %.2f, K2O %.2f kg/ha",
                    nvl(row.getProvidedN()), nvl(row.getProvidedP2O5()), nvl(row.getProvidedK2O())));
        }
        if (row.getBalanceN() != null || row.getBalanceP2O5() != null || row.getBalanceK2O() != null) {
            details.add(String.format(Locale.US, "Saldo: N %.2f, P2O5 %.2f, K2O %.2f kg/ha",
                    nvl(row.getBalanceN()), nvl(row.getBalanceP2O5()), nvl(row.getBalanceK2O())));
        }
        if (row.getCalculationMemory() != null && !row.getCalculationMemory().isBlank()) {
            details.add("Memória de cálculo: " + row.getCalculationMemory());
        }
        if (row.getWarning() != null && !row.getWarning().isBlank()) details.add("Alerta: " + row.getWarning());
        return String.join("; ", details);
    }

    private String formatPercent(Double value) {
        return value == null ? "Não calculado" : String.format(Locale.US, "%.2f%%", value);
    }

    private String formatGypsumNeed(Boolean needed) {
        if (needed == null) return "Não avaliada";
        return needed ? "Sim" : "Não";
    }

    private String formatNpk(Double n, Double p2o5, Double k2o) {
        if (n == null && p2o5 == null && k2o == null) return "Não calculado";
        return String.format(Locale.US, "N %.2f / P2O5 %.2f / K2O %.2f kg/ha", nvl(n), nvl(p2o5), nvl(k2o));
    }

    private String formatSignedNpk(Double n, Double p2o5, Double k2o) {
        if (n == null && p2o5 == null && k2o == null) return "Não calculado";
        return String.format(Locale.US, "N %+.2f / P2O5 %+.2f / K2O %+.2f kg/ha", nvl(n), nvl(p2o5), nvl(k2o));
    }

    private double nvl(Double value) {
        return value == null ? 0d : value;
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
