package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        appendChemicalDiagnosis(report, result);
        appendPhysicalDiagnosis(report, result);
        appendSalinityDiagnosis(report, result);
        appendFoliarDiagnosis(report, result);
        appendLimingRequirement(report, result);
        appendGypsumRequirement(report, result);
        if (shouldRenderFertilization(result)) {
            appendCorrectiveFertilization(report, result);
            appendPlantingFertilization(report, result);
            appendCoverageFertilization(report, result);
            appendRecommendedFertilizers(report, result);
        }
        appendSingleTechnicalWarning(report, result);
        return report.toString();
    }

    private boolean shouldRenderFertilization(RecommendationCalculationService.RecommendationCalculationResult result) {
        return result == null
                || result.getRecommendationType() == null
                || !"ACIDITY_OR_SALINITY_CORRECTION".equals(result.getRecommendationType());
    }

    private void appendTitle(StringBuilder report) {
        report.append("Laudo Técnico de Recomendação Agrícola\n\n");
    }

    private void appendIdentification(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("1. Identificação\n\n");
        appendPresentBullet(report, "Produtor ou solicitante", result.getRequesterName(), null);
        appendPresentBullet(report, "Usuário solicitante", result.getRequesterUsername(), null);
        appendPresentBullet(report, "Propriedade", result.getPropertyName(), formatId(result.getPropertyId()));
        appendPresentBullet(report, "Talhão", result.getPlotIdentification(), formatId(result.getPlotId()));
        appendPresentBullet(report, "Cultura", result.getCropName(), formatId(result.getCropId()));
        if (result.getAnnualCropFolderYear() != null) {
            report.append("- Ano agrícola da pasta: ").append(result.getAnnualCropFolderYear()).append("\n");
        }
        appendPresentBullet(report, "Tipo de recomendação", result.getRecommendationType(), null);
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
        report.append("- Necessidade K2O considerada: ").append(formatQuantity(result.getRequiredK2O())).append("\n");
        report.append("- Necessidade S considerada: ").append(formatQuantity(result.getRequiredS())).append("\n\n");
    }

    private void appendChemicalDiagnosis(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("3. Diagnóstico químico\n\n");
        report.append("| Atributo | Valor analisado | Unidade | Interpretação | Faixa ou critério usado | Observação técnica |\n");
        report.append("|---|---:|---|---|---|---|\n");
        if (result.getSoilChemicalDiagnosis() == null || result.getSoilChemicalDiagnosis().isEmpty()) {
            report.append("Aviso técnico: diagnóstico químico não foi produzido com os dados disponíveis.\n\n");
            return;
        }
        for (RecommendationCalculationService.SoilChemicalDiagnosisItem item : result.getSoilChemicalDiagnosis()) {
            if (item == null || TechnicalRecommendationDocumentSupport.looksUnavailable(item.getAttribute())) continue;
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

    private void appendOpportunityCostComparison(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("13.3. Comparativo de custo de oportunidade\n\n");
        report.append("Menores preços unitários dos nutrientes em fontes simples/concentradas\n\n");
        report.append("| Nutriente | R$/kg nutriente | Fonte | Tipo | Unidade comercial | Preço comercial |\n");
        report.append("|---|---:|---|---|---:|---:|\n");
        if (result.getOpportunityCostNutrientPrices() == null || result.getOpportunityCostNutrientPrices().isEmpty()) {
            report.append("| Não calculado | Não calculado | Não encontrada | Não informado | Não informado | Não informado |\n");
        } else {
            for (RecommendationCalculationService.OpportunityCostNutrientPriceRow row : result.getOpportunityCostNutrientPrices()) {
                report.append("| ").append(safeCell(row.getNutrient()))
                        .append(" | ").append(formatMoney(row.getPricePerKg()))
                        .append(" | ").append(safeCell(row.getSourceName()))
                        .append(" | ").append(safeCell(row.getSourceType()))
                        .append(" | ").append(formatWeight(row.getCommercialWeightKg()))
                        .append(" | ").append(formatMoney(row.getCommercialPrice()))
                        .append(" |\n");
            }
        }
        report.append("\n");

        report.append("Decisão econômica por unidade comercial\n\n");
        report.append("| Categoria | Adubo | PC | PO | Unidade | Razão PC/PO | Decisão | Justificativa |\n");
        report.append("|---|---|---:|---:|---:|---:|---|---|\n");
        if (result.getOpportunityCostDecisionRows() == null || result.getOpportunityCostDecisionRows().isEmpty()) {
            report.append("| Não calculado | Não avaliado | Não calculado | Não calculado | Não informada | Não calculada | indeterminada por ausência de preço | Nenhum composto, formulado ou FTE com preço e composição suficientes foi avaliado. |\n\n");
            return;
        }
        for (RecommendationCalculationService.OpportunityCostDecisionRow row : result.getOpportunityCostDecisionRows()) {
            report.append("| ").append(safeCell(row.getCategory()))
                    .append(" | ").append(safeCell(row.getFertilizerName()))
                    .append(" | ").append(formatLabeledMoney(row.getCommercialPriceLabel(), row.getCommercialPrice()))
                    .append(" | ").append(formatLabeledMoney(row.getOpportunityPriceLabel(), row.getOpportunityPrice()))
                    .append(" | ").append(formatWeight(row.getCommercialWeightKg()))
                    .append(" | ").append(formatRatio(row.getRatio()))
                    .append(" | ").append(safeCell(row.getDecision()))
                    .append(" | ").append(safeCell(opportunityJustification(row)))
                    .append(" |\n");
        }
        report.append("\n");
    }

    private void appendPhysicalDiagnosis(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("4. Diagnóstico físico\n\n");
        report.append("| Atributo | Valor analisado | Unidade | Observação técnica |\n");
        report.append("|---|---:|---|---|\n");
        if (result.getSoilPhysicalDiagnosis() == null || result.getSoilPhysicalDiagnosis().isEmpty()) {
            report.append("Aviso técnico: dados físicos insuficientes ou ausentes no extrato selecionado.\n\n");
            return;
        }
        for (RecommendationCalculationService.SoilPhysicalDiagnosisItem item : result.getSoilPhysicalDiagnosis()) {
            if (item == null || TechnicalRecommendationDocumentSupport.looksUnavailable(item.getAttribute())) continue;
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
            report.append("Aviso técnico: dados de extrato de saturação e/ou sodicidade insuficientes para diagnóstico.\n\n");
            return;
        }
        for (RecommendationCalculationService.SoilSalinityDiagnosisItem item : result.getSoilSalinityDiagnosis()) {
            if (item == null || TechnicalRecommendationDocumentSupport.looksUnavailable(item.getAttribute())) continue;
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
            report.append("Aviso técnico: análise foliar ausente ou critérios insuficientes para diagnóstico.\n\n");
            return;
        }
        for (RecommendationCalculationService.FoliarDiagnosisItem item : result.getFoliarDiagnosis()) {
            if (item == null || TechnicalRecommendationDocumentSupport.looksUnavailable(item.getNutrient())) continue;
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
            report.append("Aviso técnico: resultado estruturado de calagem não foi produzido pelo cálculo.\n\n");
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
        appendCalciumMagnesiumBalance(report, liming.getCalciumMagnesiumBalance());
        appendFirstWarning(report, "Aviso técnico de calagem", liming.getWarnings());
        report.append("\n");
    }

    private void appendCalciumMagnesiumBalance(
            StringBuilder report,
            CalciumMagnesiumBalanceCalculator.CalciumMagnesiumBalanceRangeResult balance) {
        if (balance == null) {
            report.append("- Composição teórica de CaO e MgO: Não calculada.\n");
            return;
        }
        if (balance.currentRatio() == null) {
            report.append("- Relação atual Ca/Mg: indisponível; o teor de Mg é zero ou os dados de Ca/Mg estão incompletos.\n");
        } else {
            report.append("- A relação Ca/Mg do solo é igual a ")
                    .append(formatDecimal(balance.currentRatio())).append(".\n");
        }
        if (balance.scenarios() == null || balance.scenarios().isEmpty()) {
            report.append("- Aviso técnico da composição do calcário: ")
                    .append(safe(balance.technicalWarning())).append("\n");
            return;
        }

        if (balance.available()) {
            long availableScenarioCount = balance.scenarios().stream().filter(
                    CalciumMagnesiumBalanceCalculator.CalciumMagnesiumBalanceScenario::available).count();
            report.append(availableScenarioCount == 2
                            ? "- Para manter a relação Ca/Mg esperada entre 3:1 e 4:1, deve ser usado calcário com "
                            : "- Para o alvo matematicamente disponível, deve ser usado calcário com ")
                    .append(formatDecimal(balance.minimumCalciumOxidePercent())).append("% a ")
                    .append(formatDecimal(balance.maximumCalciumOxidePercent())).append("% de CaO e ")
                    .append(formatDecimal(balance.minimumMagnesiumOxidePercent())).append("% a ")
                    .append(formatDecimal(balance.maximumMagnesiumOxidePercent())).append("% de MgO. ")
                    .append("Quanto menos MgO% tiver o calcário, mais a relação Ca/Mg se alarga.\n");
        }
        for (CalciumMagnesiumBalanceCalculator.CalciumMagnesiumBalanceScenario scenario : balance.scenarios()) {
            if (!scenario.available()) {
                report.append("- Aviso técnico para o alvo ")
                        .append((int) scenario.desiredRatio()).append(":1: ")
                        .append(safe(scenario.technicalWarning())).append("\n");
                continue;
            }
            report.append("- Composição teórica para ")
                    .append((int) scenario.desiredRatio()).append(":1: CaO ")
                    .append(formatDecimal(scenario.calciumOxidePercent())).append("%; MgO ")
                    .append(formatDecimal(scenario.magnesiumOxidePercent())).append("%; classificação: ")
                    .append(safe(scenario.limestoneClassification())).append(".\n");
        }
    }

    private String formatDecimal(Double value) {
        return value == null || !Double.isFinite(value) ? "Não informado" : String.format(Locale.US, "%.2f", value);
    }

    private void appendGypsumRequirement(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("8. Gessagem\n\n");
        RecommendationCalculationService.GypsumRequirementResult gypsum = result.getGypsumRequirement();
        if (gypsum == null) {
            report.append("Aviso técnico: resultado estruturado de gessagem não foi produzido pelo cálculo.\n\n");
            return;
        }
        if (Boolean.FALSE.equals(gypsum.getEvaluated())) {
            report.append("- Gessagem: não calculada.\n");
            report.append("- Aviso técnico: ").append(firstNonBlank(
                    gypsum.getJustification(),
                    firstWarning(gypsum.getWarnings()),
                    "Gessagem não avaliada porque as análises selecionadas não possuem camada subsuperficial suficiente.")).append("\n");
            report.append("\n");
            return;
        }

        report.append("- Necessidade de gessagem: ").append(formatGypsumNeed(gypsum.getNeeded())).append("\n");
        report.append("- Critério usado: ").append(safe(gypsum.getCriterion())).append("\n");
        report.append("- Dose de gesso: ").append(formatDose(gypsum.getCalculatedRequirement(), gypsum.getUnit())).append("\n");
        report.append("- Enxofre equivalente: ").append(formatDose(gypsum.getSulfurEquivalent(), "kg/ha de S")).append("\n");
        report.append("- Recomendação de aplicação: ").append(safe(gypsum.getApplicationRecommendation())).append("\n");
        report.append("- Fonte comercial: ").append(safe(gypsum.getSourceName())).append("\n");
        report.append("- Tipo da fonte: ").append(safe(gypsum.getSourceType())).append("\n");
        report.append("- Dose comercial: ").append(formatDose(gypsum.getCommercialDose(), gypsum.getCommercialDoseUnit())).append("\n");
        if (Boolean.TRUE.equals(gypsum.getLowDoseAlternativeApplicable())) {
            report.append("- Alternativa com sulfato de amônio 22% S: ").append(formatDose(gypsum.getSulfurEquivalent() / 0.22d, "kg/ha")).append("\n");
            report.append("- Alternativa com superfosfato simples 11% S: ").append(formatDose(gypsum.getSulfurEquivalent() / 0.11d, "kg/ha")).append("\n");
        }
        report.append("- Justificativa: ").append(safe(gypsum.getJustification())).append("\n");
        report.append("- Justificativa da fonte: ").append(safe(gypsum.getSourceJustification())).append("\n");
        appendPresentBullet(report, "Limitações da fonte", gypsum.getSourceLimitations(), null);
        appendFirstWarning(report, "Aviso técnico de gessagem", gypsum.getWarnings());
        if (RecommendationCalculationService.isEffectiveGypsumRecommendation(gypsum)) {
            report.append("- Atenção: Caso se faça gessagem da área, não precisa aplicar enxofre na adubação de plantio. "
                    + "Se precisar usar sulfato de amônio ou superfosfato simples na adubação de plantio, "
                    + "desconsiderar a quantidade adicional de S aplicada. Ele não é limitante.\n");
        }
        report.append("\n");
    }

    private void appendCorrectiveFertilization(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("9. Adubação corretiva\n\n");
        report.append("| Nutriente/Atributo corrigido | Necessidade | Fonte sugerida | Dose | Memória de cálculo | Aviso técnico |\n");
        report.append("|---|---|---|---:|---|---|\n");
        List<RecommendationCalculationService.CorrectiveFertilizationRow> rows = relevantCorrectiveRows(result);
        if (rows.isEmpty()) {
            report.append("Aviso técnico: adubação corretiva não gerou dose operacional com os dados persistidos.\n\n");
            return;
        }
        for (RecommendationCalculationService.CorrectiveFertilizationRow row : rows) {
            report.append("| ").append(safeCell(row.getCorrectedAttribute()))
                    .append(" | ").append(safeCell(row.getNeed()))
                    .append(" | ").append(safeCell(row.getSuggestedSource()))
                    .append(" | ").append(formatDose(row.getDose(), row.getDoseUnit()))
                    .append(" | ").append(safeCell(row.getCalculationMemory()))
                    .append(" | ").append(safeCell(row.getTechnicalWarning()))
                    .append(" |\n");
        }
        boolean recommendsSimpleSuperphosphate = rows.stream().anyMatch(row -> row.getDose() != null
                && row.getDose() > 0d
                && row.getSuggestedSource() != null
                && row.getSuggestedSource().toLowerCase(Locale.ROOT).contains("superfosfato simples"));
        if (recommendsSimpleSuperphosphate) {
            report.append("\nAtenção: Se utilizar superfosfato simples na adubação corretiva, não usar enxofre na adubação de plantio.\n");
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
            report.append("Aviso técnico: ").append(emptyMessage).append("\n\n");
            return;
        }
        for (RecommendationCalculationService.FertilizationRecommendationRow row : rows) {
            if (!hasRenderableFertilizationDose(row)) {
                continue;
            }
            report.append("| ").append(safeCell(row.getPhase()))
                    .append(" | ").append(safeCell(displayNutrients(row)))
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
        report.append("| Tipo | Fertilizante | N | P2O5 | K2O | S | Justificativa |\n");
        report.append("|---|---|---:|---:|---:|---:|---|\n");
        if (result.getFertilizerSuggestions() == null || result.getFertilizerSuggestions().isEmpty()) {
            report.append("Aviso técnico: nenhum fertilizante comercial foi selecionado com os dados disponíveis.\n\n");
        } else {
            for (RecommendationCalculationService.FertilizerSuggestion suggestion : result.getFertilizerSuggestions()) {
                if (suggestion == null || TechnicalRecommendationDocumentSupport.looksUnavailable(suggestion.getFertilizerName())) continue;
                report.append("| ").append(safeCell(suggestion.getFertilizerType()))
                        .append(" | ").append(safeCell(suggestion.getFertilizerName())).append(formatId(suggestion.getFertilizerId()))
                        .append(" | ").append(formatPercentValue(suggestion.getN()))
                        .append(" | ").append(formatPercentValue(suggestion.getP2o5()))
                        .append(" | ").append(formatPercentValue(suggestion.getK2o()))
                        .append(" | ").append(formatPercentValue(suggestion.getS()))
                        .append(" | ").append(safeCell(suggestion.getReason()))
                        .append(" |\n");
            }
            report.append("\n");
        }

        report.append("13.1. Fontes quelatadas, orgânicas e organominerais\n\n");
        report.append("| Tipo de fonte | Nutriente/objetivo | Fonte | Dose | Unidade | Justificativa | Limitações |\n");
        report.append("|---|---|---|---:|---|---|---|\n");
        boolean hasAlternativeRows = result.getAlternativeFertilizationRows() != null && !result.getAlternativeFertilizationRows().isEmpty();
        boolean hasMicronutrientRows = result.getMicronutrientFertilizerRows() != null && !result.getMicronutrientFertilizerRows().isEmpty();
        if (!hasAlternativeRows) {
            report.append("Aviso técnico: fontes quelatadas, orgânicas e organominerais não geraram dose operacional com os dados persistidos.\n");
        } else {
            for (RecommendationCalculationService.AlternativeFertilizationRecommendationRow row : result.getAlternativeFertilizationRows()) {
                if (row == null
                        || TechnicalRecommendationDocumentSupport.looksUnavailable(row.getSourceName())
                        || !TechnicalRecommendationDocumentSupport.hasPositiveKgHa(row.getDose() + " " + row.getUnit())) continue;
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
        report.append("\n13.2. Adubação complementar de micronutrientes com outras fontes (usar na adubação corretiva ou na de plantio)\n\n");
        report.append("| Tipo de fonte | Nutriente/objetivo | Fonte | Dose | Unidade | Justificativa | Limitações |\n");
        report.append("|---|---|---|---:|---|---|---|\n");
        if (!hasMicronutrientRows) {
            report.append("Aviso técnico: complementos minerais de micronutrientes não geraram dose operacional com os dados persistidos.\n");
        }
        if (hasMicronutrientRows) {
            Set<String> alternativeMicronutrientObjectives = alternativeMicronutrientObjectives(result.getAlternativeFertilizationRows());
            for (RecommendationCalculationService.MicronutrientFertilizerRecommendationRow row : result.getMicronutrientFertilizerRows()) {
                if (FteProductEligibility.isHistoricalSupportedFte(row.getFertilizerName())) continue;
                String micronutrient = row.getMicronutrient() != null ? row.getMicronutrient().name() : null;
                if (micronutrient != null && alternativeMicronutrientObjectives.contains(micronutrient)) {
                    continue;
                }
                if (!TechnicalRecommendationDocumentSupport.hasPositiveKgHa(row.getFertilizerDoseKgHa())
                        || TechnicalRecommendationDocumentSupport.looksUnavailable(row.getFertilizerName())) continue;
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
        if (result.getOpportunityCostDecisionRows() != null && !result.getOpportunityCostDecisionRows().isEmpty()) {
            appendOpportunityCostComparison(report, result);
        }
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
        report.append("15. Limitações e alertas\n\n");
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

    private void appendSingleTechnicalWarning(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        String warning = firstNonBlank(firstWarning(result.getWarnings()), firstWarning(result.getDiagnosticMessages()), firstWarning(result.getCorrectionMessages()));
        if (warning == null) {
            return;
        }
        report.append("14. Observações técnicas\n\n");
        report.append("- ").append(warning).append("\n\n");
    }

    private void appendCalculationMemory(StringBuilder report, RecommendationCalculationService.RecommendationCalculationResult result) {
        report.append("17. Memória de cálculo\n\n");
        report.append("A) Fertilizante comercial\n\n");
        report.append("| Fase | Fertilizante | Nutriente limitante/alvo | Necessidade alvo | Concentração do produto | Dose calculada | Fornecido N/P2O5/K2O/S | Déficit ou excedente N/P2O5/K2O/S |\n");
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
                        .append(" | ").append(formatNpk(row.getProvidedN(), row.getProvidedP2O5(), row.getProvidedK2O(), row.getProvidedS()))
                        .append(" | ").append(formatSignedNpk(row.getBalanceN(), row.getBalanceP2O5(), row.getBalanceK2O(), row.getBalanceS()))
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
        report.append("18. Encerramento\n\n");
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
                .filter(row -> phase.equalsIgnoreCase(row.getPhase())
                        || row.getPhase().toLowerCase(Locale.ROOT).contains(phase.toLowerCase(Locale.ROOT)))
                .filter(this::hasRenderableFertilizationDose)
                .toList();
    }

    private List<RecommendationCalculationService.FertilizationRecommendationRow> filterCoverageRows(
            RecommendationCalculationService.RecommendationCalculationResult result) {
        if (result.getFertilizationRecommendationRows() == null) return List.of();
        return result.getFertilizationRecommendationRows().stream()
                .filter(row -> row.getPhase() != null)
                .filter(row -> !GLOBAL_BALANCE_PHASE.equals(row.getPhase()))
                .filter(row -> row.getPhase().toLowerCase(Locale.ROOT).contains("cobertura"))
                .filter(this::hasRenderableFertilizationDose)
                .toList();
    }

    private boolean hasRenderableFertilizationDose(RecommendationCalculationService.FertilizationRecommendationRow row) {
        return row != null
                && TechnicalRecommendationDocumentSupport.hasPositiveKgHa(row.getFertilizerQuantityKgHa())
                && !TechnicalRecommendationDocumentSupport.looksUnavailable(row.getSuggestedFertilizer());
    }

    private List<RecommendationCalculationService.CorrectiveFertilizationRow> relevantCorrectiveRows(
            RecommendationCalculationService.RecommendationCalculationResult result) {
        if (result.getCorrectiveFertilizationRows() == null) return List.of();
        List<RecommendationCalculationService.CorrectiveFertilizationRow> positiveRows = result.getCorrectiveFertilizationRows().stream()
                .filter(row -> row != null)
                .filter(row -> TechnicalRecommendationDocumentSupport.hasPositiveKgHa(row.getDose()))
                .filter(row -> !TechnicalRecommendationDocumentSupport.looksUnavailable(row.getSuggestedSource()))
                .toList();
        if (!positiveRows.isEmpty()) {
            return positiveRows;
        }
        return result.getCorrectiveFertilizationRows().stream()
                .filter(row -> row != null)
                .filter(row -> row.getTechnicalWarning() != null && !row.getTechnicalWarning().isBlank())
                .filter(row -> row.getNeed() != null && row.getNeed().toLowerCase(Locale.ROOT).contains("bloqueado"))
                .limit(1)
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

    private void appendPresentBullet(StringBuilder report, String label, Object value, String suffix) {
        if (value == null) return;
        String asText = String.valueOf(value).trim();
        if (asText.isBlank()) return;
        report.append("- ").append(label).append(": ").append(asText).append(suffix == null ? "" : suffix).append("\n");
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

    private String formatMoney(BigDecimal value) {
        return value == null ? "Não calculado" : "R$ " + value.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String formatLabeledMoney(String label, BigDecimal value) {
        if (value == null) {
            return "Não calculado";
        }
        return safe(label) + " " + formatMoney(value);
    }

    private String formatWeight(BigDecimal value) {
        return value == null ? "Não informada" : value.stripTrailingZeros().toPlainString() + " kg";
    }

    private String formatRatio(BigDecimal value) {
        return value == null ? "Não calculada" : value.setScale(4, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String opportunityJustification(RecommendationCalculationService.OpportunityCostDecisionRow row) {
        if (row == null) {
            return "Não calculado";
        }
        if (row.isIndeterminate()) {
            return row.getJustification();
        }
        return row.getContributionSummary();
    }

    private String formatGypsumNeed(Boolean needed) {
        if (needed == null) return "Não avaliada";
        return needed ? "Sim" : "Não";
    }

    private String formatNpk(Double n, Double p2o5, Double k2o, Double s) {
        if (n == null && p2o5 == null && k2o == null && s == null) return "Não calculado";
        return String.format(Locale.US, "N %.2f / P2O5 %.2f / K2O %.2f / S %.2f kg/ha", nvl(n), nvl(p2o5), nvl(k2o), nvl(s));
    }

    private String formatSignedNpk(Double n, Double p2o5, Double k2o, Double s) {
        if (n == null && p2o5 == null && k2o == null && s == null) return "Não calculado";
        return String.format(Locale.US, "N %+.2f / P2O5 %+.2f / K2O %+.2f / S %+.2f kg/ha", nvl(n), nvl(p2o5), nvl(k2o), nvl(s));
    }

    private String joinApplicationDetails(RecommendationCalculationService.FertilizationRecommendationRow row) {
        List<String> details = new ArrayList<>();
        if (row.getApplicationMode() != null && !row.getApplicationMode().isBlank()) {
            details.add(row.getApplicationMode());
        }
        if (row.getProvidedN() != null || row.getProvidedP2O5() != null || row.getProvidedK2O() != null) {
            if (isCoverage(row)) {
                String provided = positiveNutrientSummary(row.getProvidedN(), null, row.getProvidedK2O(), row.getProvidedS());
                if (!provided.isBlank()) {
                    details.add("Fornecido: " + provided);
                }
            } else {
                details.add(String.format(Locale.US, "Fornecido: N %.2f, P2O5 %.2f, K2O %.2f, S %.2f kg/ha",
                        nvl(row.getProvidedN()), nvl(row.getProvidedP2O5()), nvl(row.getProvidedK2O()), nvl(row.getProvidedS())));
            }
        }
        if (row.getBalanceN() != null || row.getBalanceP2O5() != null || row.getBalanceK2O() != null || row.getBalanceS() != null) {
            if (isCoverage(row)) {
                details.add(String.format(Locale.US, "Saldo: N %.2f, K2O %.2f, S %.2f kg/ha",
                        nvl(row.getBalanceN()), nvl(row.getBalanceK2O()), nvl(row.getBalanceS())));
            } else {
                details.add(String.format(Locale.US, "Saldo: N %.2f, P2O5 %.2f, K2O %.2f, S %.2f kg/ha",
                        nvl(row.getBalanceN()), nvl(row.getBalanceP2O5()), nvl(row.getBalanceK2O()), nvl(row.getBalanceS())));
            }
        }
        if (row.getCalculationMemory() != null && !row.getCalculationMemory().isBlank()) {
            details.add("Memória de cálculo: " + row.getCalculationMemory());
        }
        if (row.getWarning() != null && !row.getWarning().isBlank()) {
            details.add("Alerta: " + row.getWarning());
        }
        return details.isEmpty() ? "Não informado" : String.join("; ", details);
    }

    private String displayNutrients(RecommendationCalculationService.FertilizationRecommendationRow row) {
        String nutrients = row.getNutrients();
        if (!isCoverage(row) || nutrients == null) {
            return nutrients;
        }
        if (nutrients.contains(":")) {
            return displayCoverageNutrients(nutrients);
        }
        return nutrients
                .replaceAll("(?i)\\s*/?\\s*P2O5\\s*[-+]?\\d*(?:[\\.,]\\d+)?\\s*kg/ha", "")
                .replaceAll("(?i)P2O5\\s*[,;/]?\\s*", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    private String displayCoverageNutrients(String nutrients) {
        List<String> parts = new ArrayList<>();
        String[] rawParts = nutrients.split(",");
        for (int i = 0; i < rawParts.length; i++) {
            String part = rawParts[i] == null ? "" : rawParts[i].trim();
            if (part.isBlank()) {
                continue;
            }
            int separator = part.indexOf(':');
            if (separator < 0) {
                parts.add(part);
                continue;
            }
            String label = part.substring(0, separator).trim();
            String value = part.substring(separator + 1).trim();
            if (label.isBlank()) {
                label = inferMissingCoverageNutrient(rawParts, i);
            }
            if (label == null || label.isBlank() || value.isBlank()) {
                continue;
            }
            parts.add(label + ": " + value);
        }
        return String.join(", ", parts).replaceAll("\\s{2,}", " ").trim();
    }

    private String inferMissingCoverageNutrient(String[] rawParts, int index) {
        boolean previousHasN = hasCoverageNutrient(rawParts, 0, index, "N");
        boolean nextHasK2O = hasCoverageNutrient(rawParts, index + 1, rawParts.length, "K2O");
        boolean alreadyHasP2O5 = hasCoverageNutrient(rawParts, 0, rawParts.length, "P2O5");
        return previousHasN && nextHasK2O && !alreadyHasP2O5 ? "P2O5" : null;
    }

    private boolean hasCoverageNutrient(String[] rawParts, int startInclusive, int endExclusive, String nutrient) {
        for (int i = startInclusive; i < endExclusive; i++) {
            if (i < 0 || i >= rawParts.length || rawParts[i] == null) {
                continue;
            }
            String part = rawParts[i].trim();
            int separator = part.indexOf(':');
            String label = separator >= 0 ? part.substring(0, separator).trim() : part;
            if (nutrient.equalsIgnoreCase(label)) {
                return true;
            }
        }
        return false;
    }

    private String positiveNutrientSummary(Double n, Double p2o5, Double k2o, Double s) {
        List<String> parts = new ArrayList<>();
        addPositiveNutrient(parts, "N", n);
        addPositiveNutrient(parts, "P2O5", p2o5);
        addPositiveNutrient(parts, "K2O", k2o);
        addPositiveNutrient(parts, "S", s);
        return String.join(", ", parts);
    }

    private void addPositiveNutrient(List<String> parts, String nutrient, Double value) {
        if (value != null && value > 0d) {
            parts.add(String.format(Locale.US, "%s %.2f kg/ha", nutrient, value));
        }
    }

    private boolean isCoverage(RecommendationCalculationService.FertilizationRecommendationRow row) {
        return row != null && row.getPhase() != null && row.getPhase().toLowerCase(Locale.ROOT).contains("cobertura");
    }

    private void appendFirstWarning(StringBuilder report, String label, List<String> warnings) {
        String warning = firstWarning(warnings);
        if (warning != null) {
            report.append("- ").append(label).append(": ").append(warning).append("\n");
        }
    }

    private String firstWarning(List<String> warnings) {
        if (warnings == null) return null;
        return warnings.stream()
                .filter(message -> message != null && !message.isBlank())
                .findFirst()
                .orElse(null);
    }

    private String firstNonBlank(String first, String second, String third) {
        for (String value : new String[]{first, second, third}) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
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
