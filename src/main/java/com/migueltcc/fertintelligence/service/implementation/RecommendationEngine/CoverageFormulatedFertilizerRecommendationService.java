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
            if (!hasCompletePositiveNpk(coverage)) {
                addWarning(warnings, "Cobertura " + coverageLabel(coverage.coverageOrder())
                        + " sem recomendação positiva completa de N, P2O5 e K2O; seleção de formulado NPK não foi forçada.");
                continue;
            }

            FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult selection =
                    formulatedFertilizerSelectionService.selectCandidates(
                            user, sourceOption, coverage.requiredN(), coverage.requiredP2O5(), coverage.requiredK2O());
            if (selection.technicalMessage() != null) {
                addWarning(warnings, "Cobertura " + coverageLabel(coverage.coverageOrder()) + ": " + selection.technicalMessage());
            }
            if (selection.candidates() == null || selection.candidates().isEmpty()) {
                continue;
            }

            FormulatedFertilizerRatioService.RatioCalculationResult recommendedRatio =
                    formulatedFertilizerRatioService.calculateRecommendedRatio(
                            coverage.requiredN(), coverage.requiredP2O5(), coverage.requiredK2O());
            rows.addAll(selection.candidates().stream()
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
                .requiredP2O5(round2(coverage.requiredP2O5()))
                .requiredK2O(round2(coverage.requiredK2O()))
                .relationUsed(formatRelation(recommendedRatio))
                .selectionType(candidate.approximateFallback() ? "APROXIMADA" : "DIRETA")
                .doseKgHa(candidate.fertilizerDoseKgHa())
                .doseUnitMode(doseUnitMetadata.doseUnitMode())
                .doseUnitLabel(doseUnitMetadata.doseUnitLabel())
                .gramsPerLinearMeter(resolvedMode == CropSpacingMode.PLANTS_PER_LINEAR_METER
                        ? spacingDose.gramsPerLinearMeter()
                        : null)
                .gramsPerPit(resolvedMode == CropSpacingMode.PIT ? spacingDose.gramsPerPit() : null)
                .technicalObservation(buildTechnicalObservation(candidate, recommendedRatio, coverage, spacingDose))
                .build();
    }

    private boolean hasCompletePositiveNpk(CoverageNpkRecommendation coverage) {
        return coverage != null
                && isPositive(coverage.requiredN())
                && isPositive(coverage.requiredP2O5())
                && isPositive(coverage.requiredK2O());
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

    private String buildTechnicalObservation(
            FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate candidate,
            NPKrelation recommendedRatio,
            CoverageNpkRecommendation coverage,
            CropSpacingCalculationService.CropSpacingDoseResult spacingDose) {
        String selectionObservation = candidate.approximateFallback()
                ? "Seleção por aproximação: sem correspondência direta de relação N-P2O5-K2O."
                : "Seleção direta por correspondência de relação N-P2O5-K2O.";
        String relationObservation = " Relação recomendada usada: " + formatRelation(recommendedRatio)
                + "; relação do formulado: " + formatRelation(candidate.relation()) + ".";
        String requiredObservation = String.format(Locale.US,
                " Cobertura %s com N %.2f kg/ha, P2O5 %.2f kg/ha e K2O %.2f kg/ha.",
                coverageLabel(coverage.coverageOrder()), coverage.requiredN(), coverage.requiredP2O5(), coverage.requiredK2O());
        String doseObservation = " Dose calculada por 100 * (N + P2O5 + K2O recomendados na cobertura) / (N% + P2O5% + K2O% do formulado).";
        String spacingObservation = spacingDose.technicalWarning() != null
                ? " Conversão por espaçamento não calculada: " + spacingDose.technicalWarning()
                : " Conversão por espaçamento calculada conforme cadastro da cultura.";
        String candidateMessage = candidate.technicalMessage() != null ? " " + candidate.technicalMessage() : "";
        return selectionObservation + relationObservation + requiredObservation + doseObservation + spacingObservation + candidateMessage;
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

    record CoverageNpkRecommendation(Integer coverageOrder, Double requiredN, Double requiredP2O5, Double requiredK2O) {
    }
}
