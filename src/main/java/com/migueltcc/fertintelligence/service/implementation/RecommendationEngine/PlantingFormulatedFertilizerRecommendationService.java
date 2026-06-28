package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
class PlantingFormulatedFertilizerRecommendationService {

    private static final String PLANTING_PHASE = "PLANTIO";

    private final FormulatedFertilizerSelectionService formulatedFertilizerSelectionService;
    private final FormulatedFertilizerRatioService formulatedFertilizerRatioService;
    private final CropSpacingCalculationService cropSpacingCalculationService;

    List<RecommendationCalculationService.PlantingFormulatedFertilizerRecommendationRow> calculate(
            UserModel user,
            FertilizerSourceOption sourceOption,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O,
            CropModel crop,
            List<String> warnings) {
        FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult selection =
                formulatedFertilizerSelectionService.selectCandidates(user, sourceOption, requiredN, requiredP2O5, requiredK2O);
        if (selection.technicalMessage() != null && warnings != null) {
            warnings.add(selection.technicalMessage());
        }
        if (selection.candidates() == null || selection.candidates().isEmpty()) {
            return List.of();
        }
        FormulatedFertilizerRatioService.RatioCalculationResult recommendedRatio =
                formulatedFertilizerRatioService.calculateRecommendedRatio(requiredN, requiredP2O5, requiredK2O);

        return selection.candidates().stream()
                .limit(2)
                .map(candidate -> toRow(candidate, recommendedRatio.ratio(), crop))
                .toList();
    }

    private RecommendationCalculationService.PlantingFormulatedFertilizerRecommendationRow toRow(
            FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate candidate,
            NPKrelation recommendedRatio,
            CropModel crop) {
        FormulatedMineralFertilizerModel fertilizer = candidate.formulated();
        CropSpacingCalculationService.CropSpacingDoseResult spacingDose =
                cropSpacingCalculationService.calculate(crop, candidate.fertilizerDoseKgHa());
        CropSpacingMode resolvedMode = spacingDose.resolvedMode();
        CropSpacingCalculationService.DoseUnitMetadata doseUnitMetadata =
                cropSpacingCalculationService.resolveDoseUnitMetadata(resolvedMode);

        return RecommendationCalculationService.PlantingFormulatedFertilizerRecommendationRow.builder()
                .phase(PLANTING_PHASE)
                .fertilizerId(fertilizer != null ? fertilizer.getId() : null)
                .fertilizerName(formatFertilizerName(fertilizer))
                .nitrogenPercent(fertilizer != null ? fertilizer.getN() : null)
                .p2o5Percent(fertilizer != null ? fertilizer.getP2O5() : null)
                .k2oPercent(fertilizer != null ? fertilizer.getK2O() : null)
                .relationUsed(formatRelation(recommendedRatio))
                .selectionType(candidate.approximateFallback() ? "APROXIMADA" : "DIRETA")
                .doseKgHa(candidate.fertilizerDoseKgHa())
                .doseUnitMode(doseUnitMetadata.doseUnitMode())
                .doseUnitLabel(doseUnitMetadata.doseUnitLabel())
                .gramsPerLinearMeter(resolvedMode == CropSpacingMode.PLANTS_PER_LINEAR_METER
                        ? spacingDose.gramsPerLinearMeter()
                        : null)
                .gramsPerPit(resolvedMode == CropSpacingMode.PIT ? spacingDose.gramsPerPit() : null)
                .technicalObservation(buildTechnicalObservation(candidate, recommendedRatio, spacingDose))
                .build();
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
            CropSpacingCalculationService.CropSpacingDoseResult spacingDose) {
        String selectionObservation = candidate.approximateFallback()
                ? "Seleção por aproximação: sem correspondência direta de relação N-P2O5-K2O."
                : "Seleção direta por correspondência de relação N-P2O5-K2O.";
        String relationObservation = " Relação recomendada usada: " + formatRelation(recommendedRatio)
                + "; relação do formulado: " + formatRelation(candidate.relation()) + ".";
        String doseObservation = " Dose calculada por 100 * (N + P2O5 + K2O recomendados) / (N% + P2O5% + K2O% do formulado).";
        String spacingObservation = spacingDose.technicalWarning() != null
                ? " Conversão por espaçamento não calculada: " + spacingDose.technicalWarning()
                : " Conversão por espaçamento calculada conforme cadastro da cultura.";
        String candidateMessage = candidate.technicalMessage() != null ? " " + candidate.technicalMessage() : "";
        return selectionObservation + relationObservation + doseObservation + spacingObservation + candidateMessage;
    }
}
