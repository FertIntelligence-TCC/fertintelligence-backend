package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class AlternativeFertilizationCalculationServiceTest {

    private final AlternativeFertilizationCalculationService service = new AlternativeFertilizationCalculationService(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            new CropSpacingCalculationService());

    @Test
    void calculatesLinearDoseForMicronutrientFertilizerLines() throws Exception {
        CropModel crop = CropModel.builder()
                .spacingMode(CropSpacingMode.PLANTS_PER_LINEAR_METER)
                .distanceBetweenLines(0.5d)
                .plantsPerMeter(10d)
                .build();
        MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection =
                calculatedSelection(AppliedMicronutrient.B, "Fonte B", 20d, 10d);

        RecommendationCalculationService.MicronutrientFertilizerRecommendationRow row =
                invokeToDirectRecommendationMicronutrientRow(selection, crop, null);

        assertThat(row.getFertilizerDoseKgHa()).isEqualTo(10d);
        assertThat(row.getDoseUnitMode()).isEqualTo("LINEAR_METER");
        assertThat(row.getDoseUnitLabel()).isEqualTo("g/m linear");
        assertThat(row.getGramsPerLinearMeter()).isEqualTo(0.5d);
        assertThat(row.getGramsPerPit()).isNull();
        assertThat(row.getTechnicalObservation()).isNull();
    }

    @Test
    void calculatesPitDoseForMicronutrientFertilizerLines() throws Exception {
        CropModel crop = CropModel.builder()
                .spacingMode(CropSpacingMode.PIT)
                .distanceBetweenLines(0.5d)
                .distanceBetweenPits(0.25d)
                .plantsPerPit(2d)
                .build();
        MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection =
                calculatedSelection(AppliedMicronutrient.Zn, "Fonte Zn", 25d, 10d);

        RecommendationCalculationService.MicronutrientFertilizerRecommendationRow row =
                invokeToDirectRecommendationMicronutrientRow(selection, crop, null);

        assertThat(row.getFertilizerDoseKgHa()).isEqualTo(10d);
        assertThat(row.getDoseUnitMode()).isEqualTo("PIT");
        assertThat(row.getDoseUnitLabel()).isEqualTo("g/cova");
        assertThat(row.getGramsPerLinearMeter()).isNull();
        assertThat(row.getGramsPerPit()).isEqualTo(0.125d);
        assertThat(row.getTechnicalObservation()).isNull();
    }

    @Test
    void keepsKgPerHectareAndTechnicalObservationWhenSpacingDataIsIncomplete() throws Exception {
        CropModel crop = CropModel.builder()
                .spacingMode(CropSpacingMode.PIT)
                .distanceBetweenLines(0.5d)
                .plantsPerPit(2d)
                .build();
        MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection =
                calculatedSelection(AppliedMicronutrient.Zn, "Fonte Zn", 25d, 10d);

        RecommendationCalculationService.MicronutrientFertilizerRecommendationRow row =
                invokeToDirectRecommendationMicronutrientRow(selection, crop, null);

        assertThat(row.getFertilizerDoseKgHa()).isEqualTo(10d);
        assertThat(row.getDoseUnitMode()).isEqualTo("INSUFFICIENT_DATA");
        assertThat(row.getDoseUnitLabel()).isNull();
        assertThat(row.getGramsPerLinearMeter()).isNull();
        assertThat(row.getGramsPerPit()).isNull();
        assertThat(row.getTechnicalObservation()).contains("Distancia entre covas ausente ou invalida");
    }

    private RecommendationCalculationService.MicronutrientFertilizerRecommendationRow invokeToDirectRecommendationMicronutrientRow(
            MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection,
            CropModel crop,
            String technicalObservation) throws Exception {
        Method method = AlternativeFertilizationCalculationService.class.getDeclaredMethod(
                "toDirectRecommendationMicronutrientRow",
                MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult.class,
                CropModel.class,
                String.class);
        method.setAccessible(true);
        return (RecommendationCalculationService.MicronutrientFertilizerRecommendationRow) method.invoke(
                service, selection, crop, technicalObservation);
    }

    private MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult calculatedSelection(
            AppliedMicronutrient micronutrient,
            String fertilizerName,
            Double concentrationPercent,
            Double fertilizerDoseKgHa) {
        SimpleMineralFertilizerModel fertilizer = SimpleMineralFertilizerModel.builder()
                .id(1L)
                .name(fertilizerName)
                .build();
        return new MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult(
                micronutrient,
                2d,
                fertilizer,
                concentrationPercent,
                fertilizerDoseKgHa,
                null);
    }
}
