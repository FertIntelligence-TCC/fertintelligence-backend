package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class RecommendationReportService {
    private static final DateTimeFormatter BR_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String GLOBAL_BALANCE_PHASE = "Balanço global NPK";

    public String buildTechnicalReport(RecommendationCalculationService.RecommendationCalculationResult result) {
        StringBuilder report = new StringBuilder();
        appendTitle(report);
        appendIdentification(report, result);
        appendUsedData(report, result);
        appendChemicalDiagnosis(report, result);
        appendPhysicalDiagnosis(report, result);
        appendSalinityDiagnosis(report, result);
        appendFoliarDiagnosis(report, result);
        appendLimingRequirement(report, result);
        appendGypsumRequirement(report, result);
        appendCorrectiveFertilization(report, result);
        appendPlantingFertilization(report, result);
        appendCoverageFertilization(report, result);
        appendNutrientBalance(report, result);
        appendRecommendedFertilizers(report, result);
        appendLimitationsAndAlerts(report, result);
        appendCalculationMemory(report, result);
        appendClosing(report, result);
        return report.toString();
    }

    private void appendTitle(StringBuilder report) {
        report.append("Laudo Técnico de Recomendação Agrícola\n\n");
    }

    private void appendIdentification(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("1. Identificação\n\n");
        report.append("- Produtor ou solicitante: ").append(safe(result.getRequesterName())).append("\n");
        report.append("- Usuário solicitante: ").append(safe(result.getRequesterUsername())).append("\n");
        report.append("- Propriedade: ").append(safe(result.getPropertyName())).append(formatId(result.getPropertyId())).append("\n");
        report.append("- Talhão: ").append(safe(result.getPlotIdentification())).append(formatId(result.getPlotId())).append("\n");
        report.append("- Cultura: ").append(safe(result.getCropName())).append(formatId(result.getCropId())).append("\n");
        report.append("- Ano agrícola da pasta: ").append(result.getAnnualCropFolderYear() == null ? "Não informado" : result.getAnnualCropFolderYear()).append("\n");
        report.append("- Tipo de recomendação: ").append(safe(result.getRecommendationType())).append("\n");
        report.append("- Data de emissão: ").append(formatDate(result.getIssuedAt())).append("\n\n");
    }

    private void appendUsedData(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("2. Dados utilizados\n\n");
        report.append("| Base de dados | Identificador | Síntese técnica |\n");
        report.append("|---|---:|---|\n");
        appendDataRow(report, "Análise física", result.getPhysicalAnalysisId(), result.getPhysicalAnalysisSummary());
        appendDataRow(report, "Análise de fertilidade do solo", result.getSoilFertilityAnalysisId(), result.getSoilFertilityAnalysisSummary());
        appendDataRow(report, "Extrato de saturação", result.getSaturationExtractAnalysisId(), result.getSaturationExtractAnalysisSummary());
        appendDataRow(report, "Pasta de cultura anual", result.getAnnualCropFolderId(), result.getAnnualCropFolderSummary());
        appendDataRow(report, "Cultura", result.getCropId(), result.getCropSummary());
        appendDataRow(report, "Análise foliar", result.getFoliarAnalysisId(), result.getFoliarAnalysisSummary());
        report.append("\n");
        report.append("- Necessidade N considerada: ").append(formatQuantity(result.getRequiredN())).append("\n");
        report.append("- Necessidade P2O5 considerada: ").append(formatQuantity(result.getRequiredP2O5())).append("\n");
        report.append("- Necessidade K2O considerada: ").append(formatQuantity(result.getRequiredK2O())).append("\n\n");
    }

    private void appendChemicalDiagnosis(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("3. Diagnóstico químico\n\n");
        report.append("| Atributo | Valor analisado | Unidade | Interpretação | Faixa ou critério usado | Observação técnica |\n");
        report.append("|---|---:|---|---|---|---|\n");
        if (result.getSoilChemicalDiagnosis() == null || result.getSoilChemicalDiagnosis().isEmpty()) {
            report.append("| Não calculado | Não informado | Não informado | Não classificado | Não informado | Diagnóstico químico não foi produzido com os dados disponíveis. |\n\n");
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

    private void appendPhysicalDiagnosis(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("4. Diagnóstico físico\n\n");
        report.append("| Atributo | Valor analisado | Unidade | Observação técnica |\n");
        report.append("|---|---:|---|---|\n");
        if (result.getSoilPhysicalDiagnosis() == null || result.getSoilPhysicalDiagnosis().isEmpty()) {
            report.append("| Não calculado | Não informado | Não informado | Dados físicos insuficientes ou ausentes no extrato selecionado. |\n\n");
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

    private void appendSalinityDiagnosis(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("5. Diagnóstico de salinidade/sodicidade\n\n");
        report.append("| Atributo | Valor analisado | Unidade | Interpretação | Faixa ou critério usado | Observação técnica |\n");
        report.append("|---|---:|---|---|---|---|\n");
        if (result.getSoilSalinityDiagnosis() == null || result.getSoilSalinityDiagnosis().isEmpty()) {
            report.append("| Não calculado | Não informado | Não informado | Não classificado | Não informado | Dados de extrato de saturação e/ou sodicidade insuficientes para diagnóstico. |\n\n");
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

    private void appendFoliarDiagnosis(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("6. Diagnóstico foliar\n\n");
        report.append("| Nutriente | Valor analisado | Unidade | Interpretação | Faixa adequada usada | Observação técnica |\n");
        report.append("|---|---:|---|---|---|---|\n");
        if (result.getFoliarDiagnosis() == null || result.getFoliarDiagnosis().isEmpty()) {
            report.append("| Não calculado | Não informado | Não informado | Não classificado | Não informado | Análise foliar não informada ou critérios insuficientes para diagnóstico. |\n\n");
            return;
        }
        for (RecommendationCalculationService.FoliarDiagnosisItem item : result.getFoliarDiagnosis()) {
            report.append("| ").append(safeCell(item.getNutrient()))
                    .append(" | ").append(formatAnalyzedValue(item.getAnalyzedValue()))
                    .append(" | ").append(safeCell(item.getUnit()))
                    .append(" | ").append(safeCell(item.getInterpretation()))
                    .append(" | ").append(safeCell(item.getUsedCriterion()))
                    .append(" | ").append(safeCell(item.getTechnicalObservation()))
                    .append(" |\n");
        }
        report.append("\n");
    }

    private void appendLimingRequirement(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("7. Calagem\n\n");
        RecommendationCalculationService.LimingRequirementResult liming = result.getLimingRequirement();
        if (liming == null) {
            report.append("- Necessidade de calagem: Não calculada.\n");
            report.append("- Aviso técnico: resultado estruturado de calagem não foi produzido pelo cálculo.\n\n");
            return;
        }

        report.append("- Critério selecionado: ").append(safe(liming.getSelectedCriteria())).append("\n");
        report.append("- Critério aplicado: ").append(safe(liming.getCriterionJustification())).append("\n");
        report.append("- Fórmula usada: ").append(safe(liming.getFormula())).append("\n");
        report.append("- Fonte de calcário/corretivo: ").append(safe(liming.getLimestoneSource())).append("\n");
        report.append("- Necessidade de calagem ajustada: ").append(formatDose(adjustedLimingRequirement(liming), liming.getUnit())).append("\n");
        report.append("- Dose teórica de calagem: ").append(formatDose(liming.getTheoreticalRequirement(), liming.getUnit())).append("\n");
        report.append("- PRNT utilizado: ").append(liming.getPrnt() == null ? "Não informado" : String.format(Locale.US, "%.2f%%", liming.getPrnt())).append("\n");
        report.append("- Dose corrigida por PRNT: ").append(formatDose(liming.getCorrectedRequirement(), liming.getUnit())).append("\n");
        report.append("- Dose efetiva registrada pelo cálculo: ").append(formatDose(liming.getCalculatedRequirement(), liming.getUnit())).append("\n");
        report.append("- Avisos de calagem:\n");
        appendBulletList(report, liming.getWarnings(), "Nenhum aviso específico de calagem foi registrado.");
        report.append("\n");
    }

    private void appendGypsumRequirement(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("8. Gessagem\n\n");
        RecommendationCalculationService.GypsumRequirementResult gypsum = result.getGypsumRequirement();
        if (gypsum == null) {
            report.append("- Necessidade de gessagem: Não calculada.\n");
            report.append("- Aviso técnico: resultado estruturado de gessagem não foi produzido pelo cálculo.\n\n");
            return;
        }
        if (Boolean.FALSE.equals(gypsum.getEvaluated())) {
            report.append("- Necessidade de gessagem: Não avaliada.\n");
            report.append("- Aviso técnico: ").append(safe(gypsum.getJustification())).append("\n");
            report.append("- Avisos de gessagem:\n");
            appendBulletList(report, gypsum.getWarnings(), "Gessagem não avaliada porque as análises selecionadas não possuem camada 20-40 cm suficiente.");
            report.append("\n");
            return;
        }

        report.append("- Necessidade de gessagem: ").append(formatGypsumNeed(gypsum.getNeeded())).append("\n");
        report.append("- Critério usado: ").append(safe(gypsum.getCriterion())).append("\n");
        report.append("- Dose de gesso: ").append(formatDose(gypsum.getCalculatedRequirement(), gypsum.getUnit())).append("\n");
        report.append("- Fonte comercial: ").append(safe(gypsum.getSourceName())).append("\n");
        report.append("- Tipo da fonte: ").append(safe(gypsum.getSourceType())).append("\n");
        report.append("- Dose comercial: ").append(formatDose(gypsum.getCommercialDose(), gypsum.getCommercialDoseUnit())).append("\n");
        report.append("- Justificativa: ").append(safe(gypsum.getJustification())).append("\n");
        report.append("- Justificativa da fonte: ").append(safe(gypsum.getSourceJustification())).append("\n");
        report.append("- Limitações da fonte: ").append(safe(gypsum.getSourceLimitations())).append("\n");
        report.append("- Avisos de gessagem:\n");
        appendBulletList(report, gypsum.getWarnings(), "Nenhum aviso específico de gessagem foi registrado.");
        report.append("\n");
    }

    private void appendCorrectiveFertilization(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("9. Adubação corretiva\n\n");
        report.append("| Nutriente/Atributo corrigido | Necessidade | Fonte sugerida | Dose | Memória de cálculo | Aviso técnico |\n");
        report.append("|---|---|---|---:|---|---|\n");
        if (result.getCorrectiveFertilizationRows() == null || result.getCorrectiveFertilizationRows().isEmpty()) {
            report.append("| P/K/S corretivos | Não avaliada | Não sugerida | Não calculada | Não há regra quantitativa corretiva separada nos modelos atuais. | Adubação corretiva não foi calculada para evitar misturar dose de plantio/cobertura com correção. |\n\n");
            return;
        }
        for (RecommendationCalculationService.CorrectiveFertilizationRow row : result.getCorrectiveFertilizationRows()) {
            report.append("| ").append(safeCell(row.getCorrectedAttribute()))
                    .append(" | ").append(safeCell(row.getNeed()))
                    .append(" | ").append(safeCell(row.getSuggestedSource()))
                    .append(" | ").append(formatDose(row.getDose(), row.getDoseUnit()))
                    .append(" | ").append(safeCell(row.getCalculationMemory()))
                    .append(" | ").append(safeCell(row.getTechnicalWarning()))
                    .append(" |\n");
        }
        report.append("\n");
    }

    private void appendPlantingFertilization(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("10. Adubação de plantio\n\n");
        appendFertilizationRows(report, filterRows(result, "Plantio"), "Nenhuma linha de adubação de plantio foi calculada.");
    }

    private void appendCoverageFertilization(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("11. Adubação de cobertura\n\n");
        appendFertilizationRows(report, filterCoverageRows(result), "Nenhuma linha de adubação de cobertura foi calculada.");
    }

    private void appendFertilizationRows(StringBuilder report,
                                         List<RecommendationCalculationService.FertilizationRecommendationRow> rows,
                                         String emptyMessage) {
        report.append("| Fase da Cultura | Nutrientes Necessários | Sugestão de Adubo | Quantidade do Adubo | Época e Modo de Aplicação |\n");
        report.append("|---|---|---|---:|---|\n");
        if (rows.isEmpty()) {
            report.append("| Não calculado | Não calculado | Não calculado | Não calculado | ").append(safeCell(emptyMessage)).append(" |\n\n");
            return;
        }
        for (RecommendationCalculationService.FertilizationRecommendationRow row : rows) {
            report.append("| ").append(safeCell(row.getPhase()))
                    .append(" | ").append(safeCell(row.getNutrients()))
                    .append(" | ").append(safeCell(row.getSuggestedFertilizer()))
                    .append(" | ").append(formatQuantity(row.getFertilizerQuantityKgHa()))
                    .append(" | ").append(safeCell(joinApplicationDetails(row)))
                    .append(" |\n");
        }
        report.append("\n");
    }

    private void appendNutrientBalance(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("12. Balanço nutricional\n\n");
        report.append("| Nutriente | Necessidade total | Fornecido no plantio | Recomendado em cobertura | Fornecido em cobertura | Fornecido total | Saldo final | Situação |\n");
        report.append("|---|---:|---:|---:|---:|---:|---:|---|\n");
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

    private void appendRecommendedFertilizers(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("13. Fertilizantes recomendados\n\n");
        report.append("| Tipo | Fertilizante | N | P2O5 | K2O | Justificativa |\n");
        report.append("|---|---|---:|---:|---:|---|\n");
        if (result.getFertilizerSuggestions() == null || result.getFertilizerSuggestions().isEmpty()) {
            report.append("| Não calculado | Não selecionado | Não informado | Não informado | Não informado | Nenhum fertilizante comercial foi selecionado com os dados disponíveis. |\n\n");
        } else {
            for (RecommendationCalculationService.FertilizerSuggestion suggestion : result.getFertilizerSuggestions()) {
                report.append("| ").append(safeCell(suggestion.getFertilizerType()))
                        .append(" | ").append(safeCell(suggestion.getFertilizerName())).append(formatId(suggestion.getFertilizerId()))
                        .append(" | ").append(formatPercentValue(suggestion.getN()))
                        .append(" | ").append(formatPercentValue(suggestion.getP2o5()))
                        .append(" | ").append(formatPercentValue(suggestion.getK2o()))
                        .append(" | ").append(safeCell(suggestion.getReason()))
                        .append(" |\n");
            }
            report.append("\n");
        }

        report.append("13.1. Fontes orgânicas, organominerais e micronutrientes\n\n");
        report.append("| Tipo de fonte | Nutriente/objetivo | Fonte | Dose | Unidade | Justificativa | Limitações |\n");
        report.append("|---|---|---|---:|---|---|---|\n");
        boolean hasAlternativeRows = result.getAlternativeFertilizationRows() != null && !result.getAlternativeFertilizationRows().isEmpty();
        boolean hasMicronutrientRows = result.getMicronutrientFertilizerRows() != null && !result.getMicronutrientFertilizerRows().isEmpty();
        if (!hasAlternativeRows && !hasMicronutrientRows) {
            report.append("| Não calculado | Orgânicos/organominerais/micronutrientes | Não selecionada | Não calculada | Não modelada | Não há dados suficientes para recomendação suportada. | O backend não gerou recomendações genéricas sem cálculo. |\n\n");
            return;
        }
        if (hasAlternativeRows) {
            for (RecommendationCalculationService.AlternativeFertilizationRecommendationRow row : result.getAlternativeFertilizationRows()) {
                report.append("| ").append(safeCell(row.getSourceType()))
                        .append(" | ").append(safeCell(row.getNutrientOrObjective()))
                        .append(" | ").append(safeCell(row.getSourceName()))
                        .append(" | ").append(safeCell(row.getDose()))
                        .append(" | ").append(safeCell(row.getUnit()))
                        .append(" | ").append(safeCell(row.getJustification()))
                        .append(" | ").append(safeCell(row.getLimitations()))
                        .append(" |\n");
            }
        }
        if (hasMicronutrientRows) {
            Set<String> alternativeMicronutrientObjectives = alternativeMicronutrientObjectives(result.getAlternativeFertilizationRows());
            for (RecommendationCalculationService.MicronutrientFertilizerRecommendationRow row : result.getMicronutrientFertilizerRows()) {
                String micronutrient = row.getMicronutrient() != null ? row.getMicronutrient().name() : null;
                if (micronutrient != null && alternativeMicronutrientObjectives.contains(micronutrient)) {
                    continue;
                }
                report.append("| MICRONUTRIENTE")
                        .append(" | ").append(safeCell(micronutrient))
                        .append(" | ").append(safeCell(row.getFertilizerName())).append(formatId(row.getFertilizerId()))
                        .append(" | ").append(formatQuantity(row.getFertilizerDoseKgHa()))
                        .append(" | kg/ha de produto")
                        .append(" | ").append(safeCell(row.getTechnicalObservation()))
                        .append(" | ").append(safeCell(buildMicronutrientLimitations(row)))
                        .append(" |\n");
            }
        }
        report.append("\n");
    }

    private Set<String> alternativeMicronutrientObjectives(
            List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows) {
        Set<String> objectives = new HashSet<>();
        if (rows == null) {
            return objectives;
        }
        for (RecommendationCalculationService.AlternativeFertilizationRecommendationRow row : rows) {
            if (row == null || row.getSourceType() == null || row.getNutrientOrObjective() == null) continue;
            if (!"MICRONUTRIENTE".equalsIgnoreCase(row.getSourceType().trim())) continue;
            objectives.add(row.getNutrientOrObjective().trim());
        }
        return objectives;
    }

    private String buildMicronutrientLimitations(RecommendationCalculationService.MicronutrientFertilizerRecommendationRow row) {
        if (row == null || row.getFertilizerDoseKgHa() == null || row.getFertilizerName() == null || row.getFertilizerName().isBlank()) {
            return "Dose do produto não calculada por ausência de fonte mineral simples compatível.";
        }
        return "Aplicação sólida no plantio; validar compatibilidade operacional e mistura com os demais adubos.";
    }

    private void appendLimitationsAndAlerts(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("14. Limitações e alertas\n\n");
        report.append("A) Alertas de diagnóstico\n\n");
        appendBulletList(report, result.getDiagnosticMessages(), "Nenhuma limitação diagnóstica adicional foi registrada.");
        report.append("\n");
        report.append("B) Alertas de correção\n\n");
        appendBulletList(report, result.getCorrectionMessages(), "Nenhuma recomendação de correção foi calculada nesta etapa.");
        report.append("\n");
        report.append("C) Alertas gerais\n\n");
        appendBulletList(report, result.getWarnings(), "Nenhum alerta adicional foi registrado.");
        report.append("\n");
    }

    private void appendCalculationMemory(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("15. Memória de cálculo\n\n");
        report.append("A) Fertilizante comercial\n\n");
        report.append("| Fase | Fertilizante | Nutriente limitante/alvo | Necessidade alvo | Concentração do produto | Dose calculada | Fornecido N/P2O5/K2O | Déficit ou excedente N/P2O5/K2O |\n");
        report.append("|---|---|---|---:|---:|---:|---|---|\n");
        List<RecommendationCalculationService.FertilizationRecommendationRow> rows = nonBalanceRows(result);
        if (rows.isEmpty()) {
            report.append("| Não calculado | Não calculado | Não identificado | Não calculado | Não calculado | Não calculado | Não calculado | Não calculado |\n");
        } else {
            for (RecommendationCalculationService.FertilizationRecommendationRow row : rows) {
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
        }
        report.append("\n");

        report.append("B) Faixas de adubação utilizadas\n\n");
        report.append("| Nutriente | ID da faixa | Necessidade calculada |\n");
        report.append("|---|---:|---:|\n");
        appendRangeMemoryRow(report, "N", result.getNitrogenRangeId(), result.getRequiredN());
        appendRangeMemoryRow(report, "P2O5", result.getPhosphorusRangeId(), result.getRequiredP2O5());
        appendRangeMemoryRow(report, "K2O", result.getPotassiumRangeId(), result.getRequiredK2O());
        report.append("\n");

        appendInputValues(report, "Entradas de calagem", result.getLimingRequirement() != null ? result.getLimingRequirement().getInputValues() : null);
        appendInputValues(report, "Entradas de gessagem", result.getGypsumRequirement() != null ? result.getGypsumRequirement().getInputValues() : null);
    }

    private void appendClosing(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("16. Encerramento\n\n");
        report.append("Este laudo técnico consolida os diagnósticos, doses, fontes e memórias de cálculo produzidos pelo backend a partir dos dados cadastrados. ");
        report.append("Campos ausentes foram mantidos como não informados ou não calculados para evitar inferências não suportadas pelo modelo atual.\n\n");
        report.append("Data de emissão: ").append(formatDate(result.getIssuedAt())).append("\n");
    }

    private void appendDataRow(StringBuilder report, String label, Long id, String summary) {
        report.append("| ").append(safeCell(label))
                .append(" | ").append(id == null ? "Não informado" : id)
                .append(" | ").append(safeCell(summary))
                .append(" |\n");
    }

    private void appendRangeMemoryRow(StringBuilder report, String nutrient, Long rangeId, Double required) {
        report.append("| ").append(nutrient)
                .append(" | ").append(rangeId == null ? "Não informado" : rangeId)
                .append(" | ").append(formatQuantity(required))
                .append(" |\n");
    }

    private void appendInputValues(StringBuilder report, String title, Map<String, Double> inputValues) {
        report.append(title).append("\n\n");
        report.append("| Valor de entrada | Valor |\n");
        report.append("|---|---:|\n");
        if (inputValues == null || inputValues.isEmpty()) {
            report.append("| Não informado | Não informado |\n\n");
            return;
        }
        for (Map.Entry<String, Double> entry : inputValues.entrySet()) {
            report.append("| ").append(safeCell(entry.getKey()))
                    .append(" | ").append(formatAnalyzedValue(entry.getValue()))
                    .append(" |\n");
        }
        report.append("\n");
    }

    private List<RecommendationCalculationService.FertilizationRecommendationRow> filterRows(
            RecommendationCalculationService.RecommendationCalculationResult result,
            String phase) {
        if (result.getFertilizationRecommendationRows() == null) return List.of();
        return result.getFertilizationRecommendationRows().stream()
                .filter(row -> row.getPhase() != null)
                .filter(row -> phase.equalsIgnoreCase(row.getPhase()))
                .toList();
    }

    private List<RecommendationCalculationService.FertilizationRecommendationRow> filterCoverageRows(
            RecommendationCalculationService.RecommendationCalculationResult result) {
        if (result.getFertilizationRecommendationRows() == null) return List.of();
        return result.getFertilizationRecommendationRows().stream()
                .filter(row -> row.getPhase() != null)
                .filter(row -> !GLOBAL_BALANCE_PHASE.equals(row.getPhase()))
                .filter(row -> row.getPhase().toLowerCase(Locale.ROOT).contains("cobertura"))
                .toList();
    }

    private List<RecommendationCalculationService.FertilizationRecommendationRow> nonBalanceRows(
            RecommendationCalculationService.RecommendationCalculationResult result) {
        if (result.getFertilizationRecommendationRows() == null) return List.of();
        return result.getFertilizationRecommendationRows().stream()
                .filter(row -> !GLOBAL_BALANCE_PHASE.equals(row.getPhase()))
                .toList();
    }

    private String safe(Object value) {
        if (value == null) return "Não informado";
        String asText = String.valueOf(value).trim();
        return asText.isEmpty() ? "Não informado" : asText;
    }

    private String safeCell(String value) {
        return safe(value).replace("|", "/").replace("\n", " ");
    }

    private String formatId(Long id) {
        return id == null ? "" : " (ID " + id + ")";
    }

    private String formatDate(LocalDateTime date) {
        return (date == null ? LocalDateTime.now() : date).format(BR_DATE_TIME);
    }

    private String formatQuantity(Double value) {
        return value == null ? "Não calculado" : String.format(Locale.US, "%.2f kg/ha", value);
    }

    private String formatDose(Double value, String unit) {
        return value == null ? "Não calculada" : String.format(Locale.US, "%.2f %s", value, safe(unit));
    }

    private Double adjustedLimingRequirement(RecommendationCalculationService.LimingRequirementResult liming) {
        if (liming.getCalculatedRequirement() != null) {
            return liming.getCalculatedRequirement();
        }
        return liming.getCorrectedRequirement();
    }

    private String formatSignedQuantity(Double value) {
        return value == null ? "Não calculado" : String.format(Locale.US, "%+.2f kg/ha", value);
    }

    private String formatAnalyzedValue(Double value) {
        return value == null ? "Não informado" : String.format(Locale.US, "%.2f", value);
    }

    private String formatPercent(Double value) {
        return value == null ? "Não calculado" : String.format(Locale.US, "%.2f%%", value);
    }

    private String formatPercentValue(Double value) {
        return value == null ? "Não informado" : String.format(Locale.US, "%.2f%%", value);
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

    private String joinApplicationDetails(RecommendationCalculationService.FertilizationRecommendationRow row) {
        List<String> details = new ArrayList<>();
        if (row.getApplicationMode() != null && !row.getApplicationMode().isBlank()) {
            details.add(row.getApplicationMode());
        }
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
        if (row.getWarning() != null && !row.getWarning().isBlank()) {
            details.add("Alerta: " + row.getWarning());
        }
        return details.isEmpty() ? "Não informado" : String.join("; ", details);
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
