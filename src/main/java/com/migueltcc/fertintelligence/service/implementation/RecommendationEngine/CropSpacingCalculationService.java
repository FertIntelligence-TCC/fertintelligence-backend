package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import org.springframework.stereotype.Service;

@Service
public class CropSpacingCalculationService {

    private static final double SQUARE_METERS_PER_HECTARE = 10_000d;
    private static final double GRAMS_PER_KILOGRAM = 1_000d;
    private static final String LINEAR_METER_MODE = "LINEAR_METER";
    private static final String PIT_MODE = "PIT";
    private static final String INSUFFICIENT_DATA_MODE = "INSUFFICIENT_DATA";
    private static final String LINEAR_METER_LABEL = "g/m linear";
    private static final String PIT_LABEL = "g/cova";
    private static final String LINEAR_METER_COLUMN = "gPerLinearMeter";
    private static final String PIT_COLUMN = "gPerPit";

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
            return calculateLinear(kgPerHectare, distanceBetweenLines, plantsPerMeter);
        }
        if (resolvedMode == CropSpacingMode.PIT) {
            return calculatePit(kgPerHectare, distanceBetweenLines, distanceBetweenPits, plantsPerPit);
        }

        return insufficientData("Modo de espacamento ausente ou desconhecido; conversao por espacamento nao calculada.");
    }

    public DoseUnitMetadata resolveDoseUnitMetadata(CropSpacingDoseResult spacingDose) {
        return resolveDoseUnitMetadata(spacingDose != null ? spacingDose.resolvedMode() : null);
    }

    public DoseUnitMetadata resolveDoseUnitMetadata(CropSpacingMode mode) {
        if (mode == CropSpacingMode.PLANTS_PER_LINEAR_METER) {
            return new DoseUnitMetadata(LINEAR_METER_MODE, LINEAR_METER_LABEL, LINEAR_METER_COLUMN);
        }
        if (mode == CropSpacingMode.PIT) {
            return new DoseUnitMetadata(PIT_MODE, PIT_LABEL, PIT_COLUMN);
        }
        return insufficientDoseUnitMetadata();
    }

    public DoseUnitMetadata resolveDoseUnitMetadata(String mode, String label) {
        if (LINEAR_METER_MODE.equals(mode)) {
            return new DoseUnitMetadata(LINEAR_METER_MODE, label != null ? label : LINEAR_METER_LABEL, LINEAR_METER_COLUMN);
        }
        if (PIT_MODE.equals(mode)) {
            return new DoseUnitMetadata(PIT_MODE, label != null ? label : PIT_LABEL, PIT_COLUMN);
        }
        return insufficientDoseUnitMetadata();
    }

    public boolean hasApplicableDoseColumn(DoseUnitMetadata metadata) {
        return metadata != null && metadata.applicableDoseColumn() != null;
    }

    public Double applicableDoseValue(String mode, Double gramsPerLinearMeter, Double gramsPerPit) {
        if (LINEAR_METER_MODE.equals(mode)) {
            return gramsPerLinearMeter;
        }
        if (PIT_MODE.equals(mode)) {
            return gramsPerPit;
        }
        return null;
    }

    private CropSpacingDoseResult calculateLinear(Double kgPerHectare, Double distanceBetweenLines, Double plantsPerMeter) {
        if (!isPositiveFinite(plantsPerMeter)) {
            return insufficientData("Numero de plantas por metro linear ausente ou invalido; conversao por metro linear nao calculada.");
        }

        double estimatedLinearMetersPerHectare = SQUARE_METERS_PER_HECTARE / distanceBetweenLines;
        double gramsPerLinearMeter = gramsPerOperationalUnit(kgPerHectare, estimatedLinearMetersPerHectare);

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
        double gramsPerPit = gramsPerOperationalUnit(kgPerHectare, estimatedPitsPerHectare);

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

    private double gramsPerOperationalUnit(double kgPerHectare, double operationalUnitsPerHectare) {
        return kgPerHectare * GRAMS_PER_KILOGRAM / operationalUnitsPerHectare;
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

    private DoseUnitMetadata insufficientDoseUnitMetadata() {
        return new DoseUnitMetadata(INSUFFICIENT_DATA_MODE, null, null);
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

    public record DoseUnitMetadata(String doseUnitMode, String doseUnitLabel, String applicableDoseColumn) {
    }
}
