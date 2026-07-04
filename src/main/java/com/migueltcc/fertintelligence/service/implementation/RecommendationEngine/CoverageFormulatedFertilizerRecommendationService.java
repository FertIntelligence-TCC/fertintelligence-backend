package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
class CoverageFormulatedFertilizerRecommendationService {

    private final FormulatedFertilizerSelectionService formulatedFertilizerSelectionService;
    private final FormulatedFertilizerRatioService formulatedFertilizerRatioService;
    private final CropSpacingCalculationService cropSpacingCalculationService;

    List<RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow> calculate(
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<CoverageNpkRecommendation> coverageRecommendations,
            CropModel crop,
            List<String> warnings) {
        if (coverageRecommendations == null || coverageRecommendations.isEmpty()) {
            return List.of();
        }

        List<RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow> rows = new ArrayList<>();
        for (CoverageNpkRecommendation coverage : coverageRecommendations.stream()
                .sorted(Comparator.comparing(CoverageNpkRecommendation::coverageOrder, Comparator.nullsLast(Integer::compareTo)))
                .toList()) {
            if (!hasPositiveNk(coverage)) {
                addWarning(warnings, "Cobertura " + coverageLabel(coverage.coverageOrder())
                        + " sem recomendação positiva de N ou K2O; seleção de formulado N-00-K2O não foi forçada.");
                continue;
            }

            FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult selection =
                    formulatedFertilizerSelectionService.selectCandidates(
                            user, sourceOption, coverage.requiredN(), 0d, coverage.requiredK2O());
            if (selection.technicalMessage() != null) {
                addWarning(warnings, "Cobertura " + coverageLabel(coverage.coverageOrder()) + ": " + selection.technicalMessage());
            }
            if (selection.candidates() == null || selection.candidates().isEmpty()) {
                continue;
            }

            FormulatedFertilizerRatioService.RatioCalculationResult recommendedRatio =
                    formulatedFertilizerRatioService.calculateRecommendedRatio(
                            coverage.requiredN(), 0d, coverage.requiredK2O());
            List<FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate> candidates = selection.candidates();
            if (isPositive(coverage.requiredS())) {
                List<FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate> sulfurCandidates = candidates.stream()
                        .filter(candidate -> candidate.formulated() != null && nvl(candidate.formulated().getS()) > 0d)
                        .toList();
                if (!sulfurCandidates.isEmpty()) {
                    candidates = sulfurCandidates;
                } else {
                    addWarning(warnings, "Cobertura " + coverageLabel(coverage.coverageOrder())
                            + " possui S pendente, mas nenhum formulado N-00-K2O selecionável contém S; complementar com fonte simples cadastrada.");
                }
            }
            rows.addAll(candidates.stream()
                    .limit(2)
                    .map(candidate -> toRow(candidate, recommendedRatio.ratio(), coverage, crop))
                    .toList());
        }
        return rows;
    }

    private RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow toRow(
            FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate candidate,
            NPKrelation recommendedRatio,
            CoverageNpkRecommendation coverage,
            CropModel crop) {
        FormulatedMineralFertilizerModel fertilizer = candidate.formulated();
        CropSpacingCalculationService.CropSpacingDoseResult spacingDose =
                cropSpacingCalculationService.calculate(crop, candidate.fertilizerDoseKgHa());
        CropSpacingMode resolvedMode = spacingDose.resolvedMode();
        CropSpacingCalculationService.DoseUnitMetadata doseUnitMetadata =
                cropSpacingCalculationService.resolveDoseUnitMetadata(resolvedMode);

        return RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow.builder()
                .coverageOrder(coverage.coverageOrder())
                .phase("COBERTURA " + coverageLabel(coverage.coverageOrder()))
                .fertilizerId(fertilizer != null ? fertilizer.getId() : null)
                .fertilizerName(formatFertilizerName(fertilizer))
                .nitrogenPercent(fertilizer != null ? fertilizer.getN() : null)
                .p2o5Percent(fertilizer != null ? fertilizer.getP2O5() : null)
                .k2oPercent(fertilizer != null ? fertilizer.getK2O() : null)
                .requiredN(round2(coverage.requiredN()))
                .requiredP2O5(0d)
                .requiredK2O(round2(coverage.requiredK2O()))
                .relationUsed(formatRelation(recommendedRatio))
                .selectionType(selectionType(candidate))
                .doseKgHa(candidate.fertilizerDoseKgHa())
                .limitingNutrient(candidate.limitingNutrient())
                .coveragePercent(candidate.coveragePercent())
                .providedN(candidate.providedN())
                .providedP2O5(candidate.providedP2O5())
                .providedK2O(candidate.providedK2O())
                .providedS(round2(candidate.fertilizerDoseKgHa() * nvl(fertilizer != null ? fertilizer.getS() : null) / 100d))
                .balanceN(candidate.balanceN())
                .balanceP2O5(candidate.balanceP2O5())
                .balanceK2O(candidate.balanceK2O())
                .balanceS(round2(candidate.fertilizerDoseKgHa() * nvl(fertilizer != null ? fertilizer.getS() : null) / 100d - nvl(coverage.requiredS())))
                .deficitN(candidate.deficitN())
                .deficitP2O5(candidate.deficitP2O5())
                .deficitK2O(candidate.deficitK2O())
                .deficitS(round2(Math.max(0d, nvl(coverage.requiredS()) - candidate.fertilizerDoseKgHa() * nvl(fertilizer != null ? fertilizer.getS() : null) / 100d)))
                .doseUnitMode(doseUnitMetadata.doseUnitMode())
                .doseUnitLabel(doseUnitMetadata.doseUnitLabel())
                .gramsPerLinearMeter(resolvedMode == CropSpacingMode.PLANTS_PER_LINEAR_METER
                        ? spacingDose.gramsPerLinearMeter()
                        : null)
                .gramsPerPit(resolvedMode == CropSpacingMode.PIT ? spacingDose.gramsPerPit() : null)
                .technicalObservation(buildTechnicalObservation(candidate, recommendedRatio, coverage, spacingDose))
                .build();
    }

    private boolean hasPositiveNk(CoverageNpkRecommendation coverage) {
        return coverage != null
                && (isPositive(coverage.requiredN()) || isPositive(coverage.requiredK2O()));
    }

    private boolean isPositive(Double value) {
        return value != null && Double.isFinite(value) && value > 0d;
    }

    private void addWarning(List<String> warnings, String warning) {
        if (warnings != null && warning != null && !warning.isBlank()) {
            warnings.add(warning);
        }
    }

    private String formatFertilizerName(FormulatedMineralFertilizerModel fertilizer) {
        if (fertilizer == null) {
            return null;
        }
        return String.format(Locale.US, "NPK %.2f-%.2f-%.2f", fertilizer.getN(), fertilizer.getP2O5(), fertilizer.getK2O());
    }

    private String formatRelation(NPKrelation relation) {
        if (relation == null) {
            return null;
        }
        return String.format(Locale.US, "%.2f-%.2f-%.2f", relation.getN(), relation.getP(), relation.getK());
    }

    private String selectionType(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate candidate) {
        if (candidate.maximizationFallback()) {
            return "MAXIMIZACAO";
        }
        return candidate.approximateFallback() ? "APROXIMADA" : "DIRETA";
    }

    private String buildTechnicalObservation(
            FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate candidate,
            NPKrelation recommendedRatio,
            CoverageNpkRecommendation coverage,
            CropSpacingCalculationService.CropSpacingDoseResult spacingDose) {
        String selectionObservation;
        if (candidate.maximizationFallback()) {
            selectionObservation = "Seleção por maximização: sem correspondência direta nem aproximada válida de relação N-P2O5-K2O.";
        } else if (candidate.approximateFallback()) {
            selectionObservation = "Seleção por aproximação: sem correspondência direta de relação N-P2O5-K2O.";
        } else {
            selectionObservation = "Seleção direta por correspondência de relação N-P2O5-K2O.";
        }
        String relationObservation = " Relação recomendada usada: " + formatRelation(recommendedRatio)
                + "; relação do formulado: " + formatRelation(candidate.relation()) + ".";
        String requiredObservation = String.format(Locale.US,
                " Cobertura %s com N %.2f kg/ha, P2O5 %.2f kg/ha, K2O %.2f kg/ha e S %.2f kg/ha. P2O5 não é recomendado em cobertura de cultura anual.",
                coverageLabel(coverage.coverageOrder()), coverage.requiredN(), 0d, coverage.requiredK2O(), coverage.requiredS());
        String doseObservation = candidate.maximizationFallback()
                ? " Dose calculada pelo nutriente limitante " + candidate.limitingNutrient()
                + ", maximizando o atendimento sem ultrapassar esse nutriente."
                : " Dose calculada por 100 * (N + K2O recomendados na cobertura) / (N% + K2O% do formulado).";
        String balanceObservation = String.format(Locale.US,
                " Fornecimento: N %.2f, P2O5 %.2f, K2O %.2f kg/ha; déficits remanescentes: N %.2f, P2O5 %.2f, K2O %.2f kg/ha.",
                candidate.providedN(), candidate.providedP2O5(), candidate.providedK2O(),
                candidate.deficitN(), candidate.deficitP2O5(), candidate.deficitK2O());
        String sulfurObservation = isPositive(coverage.requiredS())
                ? String.format(Locale.US, " S pendente para cobertura: %.2f kg/ha; usar S do formulado quando houver e complementar com fonte simples cadastrada se necessário.", coverage.requiredS())
                : "";
        String spacingObservation = spacingDose.technicalWarning() != null
                ? " Conversão por espaçamento não calculada: " + spacingDose.technicalWarning()
                : " Conversão por espaçamento calculada conforme cadastro da cultura.";
        String candidateMessage = candidate.technicalMessage() != null ? " " + candidate.technicalMessage() : "";
        return selectionObservation + relationObservation + requiredObservation + doseObservation + balanceObservation + sulfurObservation + spacingObservation + candidateMessage;
    }

    private String coverageLabel(Integer coverageOrder) {
        return coverageOrder == null ? "cadastrada" : coverageOrder + "ª";
    }

    private Double round2(Double value) {
        if (value == null || !Double.isFinite(value)) {
            return value;
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private double nvl(Double value) {
        return value == null || !Double.isFinite(value) ? 0d : value;
    }

    record CoverageNpkRecommendation(Integer coverageOrder, Double requiredN, Double requiredP2O5, Double requiredK2O, Double requiredS) {
    }
}
