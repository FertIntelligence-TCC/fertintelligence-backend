package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import org.springframework.stereotype.Service;

@Service
public class CropSpacingCalculationService {

    private static final double SQUARE_METERS_PER_HECTARE = 10_000d;
    private static final double GRAMS_PER_KILOGRAM = 1_000d;

    public CropSpacingDoseResult calculate(CropModel crop, Double kgPerHectare) {
        if (crop == null) {
            return insufficientData("Cultura ausente; conversao de dose por espacamento nao calculada.");
        }

        return calculate(
                kgPerHectare,
                crop.getSpacingMode(),
                crop.getDistanceBetweenLines(),
                crop.getPlantsPerMeter(),
                crop.getDistanceBetweenPits(),
                crop.getPlantsPerPit()
        );
    }

    public CropSpacingDoseResult calculate(Double kgPerHectare,
                                           CropSpacingMode spacingMode,
                                           Double distanceBetweenLines,
                                           Double plantsPerMeter,
                                           Double distanceBetweenPits,
                                           Double plantsPerPit) {
        CropSpacingMode resolvedMode = resolveSpacingMode(spacingMode, plantsPerMeter);

        if (!isValidDose(kgPerHectare)) {
            return insufficientData("Dose em kg/ha ausente ou invalida; conversao por espacamento nao calculada.");
        }
        if (!isPositiveFinite(distanceBetweenLines)) {
            return insufficientData("Distancia entre linhas ausente ou invalida; conversao por espacamento nao calculada.");
        }

        if (resolvedMode == CropSpacingMode.PLANTS_PER_LINEAR_METER) {
            return calculateLinear(kgPerHectare, distanceBetweenLines);
        }
        if (resolvedMode == CropSpacingMode.PIT) {
            return calculatePit(kgPerHectare, distanceBetweenLines, distanceBetweenPits, plantsPerPit);
        }

        return insufficientData("Modo de espacamento ausente ou desconhecido; conversao por espacamento nao calculada.");
    }

    private CropSpacingDoseResult calculateLinear(Double kgPerHectare, Double distanceBetweenLines) {
        double estimatedLinearMetersPerHectare = SQUARE_METERS_PER_HECTARE / distanceBetweenLines;
        double gramsPerLinearMeter = kgPerHectare * GRAMS_PER_KILOGRAM / estimatedLinearMetersPerHectare;

        return new CropSpacingDoseResult(
                CropSpacingMode.PLANTS_PER_LINEAR_METER,
                estimatedLinearMetersPerHectare,
                gramsPerLinearMeter,
                null,
                null,
                null,
                null
        );
    }

    private CropSpacingDoseResult calculatePit(Double kgPerHectare,
                                               Double distanceBetweenLines,
                                               Double distanceBetweenPits,
                                               Double plantsPerPit) {
        if (!isPositiveFinite(distanceBetweenPits)) {
            return insufficientData("Distancia entre covas ausente ou invalida; conversao por cova nao calculada.");
        }
        if (!isPositiveFinite(plantsPerPit)) {
            return insufficientData("Numero de plantas por cova ausente ou invalido; populacao por covas e dose por cova nao calculadas.");
        }

        double estimatedPitsPerHectare = SQUARE_METERS_PER_HECTARE / (distanceBetweenLines * distanceBetweenPits);
        double estimatedPopulationByPits = estimatedPitsPerHectare * plantsPerPit;
        double gramsPerPit = kgPerHectare * GRAMS_PER_KILOGRAM / estimatedPitsPerHectare;

        return new CropSpacingDoseResult(
                CropSpacingMode.PIT,
                null,
                null,
                estimatedPitsPerHectare,
                estimatedPopulationByPits,
                gramsPerPit,
                null
        );
    }

    private CropSpacingMode resolveSpacingMode(CropSpacingMode spacingMode, Double plantsPerMeter) {
        if (spacingMode != null && spacingMode != CropSpacingMode.UNKNOWN) {
            return spacingMode;
        }
        if (isPositiveFinite(plantsPerMeter)) {
            return CropSpacingMode.PLANTS_PER_LINEAR_METER;
        }
        return CropSpacingMode.UNKNOWN;
    }

    private CropSpacingDoseResult insufficientData(String warning) {
        return new CropSpacingDoseResult(
                CropSpacingMode.UNKNOWN,
                null,
                null,
                null,
                null,
                null,
                warning
        );
    }

    private boolean isValidDose(Double value) {
        return value != null && Double.isFinite(value) && value >= 0d;
    }

    private boolean isPositiveFinite(Double value) {
        return value != null && Double.isFinite(value) && value > 0d;
    }

    public record CropSpacingDoseResult(
            CropSpacingMode resolvedMode,
            Double estimatedLinearMetersPerHectare,
            Double gramsPerLinearMeter,
            Double estimatedPitsPerHectare,
            Double estimatedPopulationByPits,
            Double gramsPerPit,
            String technicalWarning
    ) {
    }
}
