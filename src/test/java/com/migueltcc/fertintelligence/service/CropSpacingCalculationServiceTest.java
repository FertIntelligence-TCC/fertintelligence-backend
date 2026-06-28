package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.CropSpacingCalculationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CropSpacingCalculationServiceTest {

    private final CropSpacingCalculationService service = new CropSpacingCalculationService();

    @Test
    void calculateLinearSpacing() {
        CropSpacingCalculationService.CropSpacingDoseResult result = service.calculate(
                100d, CropSpacingMode.PLANTS_PER_LINEAR_METER, 0.5d, 10d, null, null);

        assertThat(result.resolvedMode()).isEqualTo(CropSpacingMode.PLANTS_PER_LINEAR_METER);
        assertThat(result.estimatedLinearMetersPerHectare()).isEqualTo(20_000d);
        assertThat(result.gramsPerLinearMeter()).isEqualTo(5d);
        assertThat(result.technicalWarning()).isNull();
    }

    @Test
    void calculatePitSpacing() {
        CropSpacingCalculationService.CropSpacingDoseResult result = service.calculate(
                100d, CropSpacingMode.PIT, 0.5d, null, 0.25d, 2d);

        assertThat(result.resolvedMode()).isEqualTo(CropSpacingMode.PIT);
        assertThat(result.estimatedPitsPerHectare()).isEqualTo(80_000d);
        assertThat(result.estimatedPopulationByPits()).isEqualTo(160_000d);
        assertThat(result.gramsPerPit()).isEqualTo(1.25d);
        assertThat(result.technicalWarning()).isNull();
    }

    @Test
    void calculateZeroKgPerHectareWhenSpacingIsValid() {
        CropSpacingCalculationService.CropSpacingDoseResult linear = service.calculate(
                0d, CropSpacingMode.PLANTS_PER_LINEAR_METER, 0.5d, 10d, null, null);
        CropSpacingCalculationService.CropSpacingDoseResult pit = service.calculate(
                0d, CropSpacingMode.PIT, 0.5d, null, 0.25d, 2d);

        assertThat(linear.gramsPerLinearMeter()).isZero();
        assertThat(linear.technicalWarning()).isNull();
        assertThat(pit.gramsPerPit()).isZero();
        assertThat(pit.technicalWarning()).isNull();
    }

    @Test
    void returnsUnknownWhenDistanceBetweenLinesIsInvalid() {
        CropSpacingCalculationService.CropSpacingDoseResult result = service.calculate(
                100d, CropSpacingMode.PLANTS_PER_LINEAR_METER, 0d, 10d, null, null);

        assertThat(result.resolvedMode()).isEqualTo(CropSpacingMode.UNKNOWN);
        assertThat(result.gramsPerLinearMeter()).isNull();
        assertThat(result.technicalWarning()).isNotBlank();
    }

    @Test
    void returnsUnknownWhenPlantsPerMeterIsInvalidForLinearMode() {
        CropSpacingCalculationService.CropSpacingDoseResult result = service.calculate(
                100d, CropSpacingMode.PLANTS_PER_LINEAR_METER, 0.5d, null, null, null);

        assertThat(result.resolvedMode()).isEqualTo(CropSpacingMode.UNKNOWN);
        assertThat(result.gramsPerLinearMeter()).isNull();
        assertThat(result.technicalWarning()).contains("plantas por metro linear");
    }

    @Test
    void returnsUnknownWhenDistanceBetweenPitsIsInvalid() {
        CropSpacingCalculationService.CropSpacingDoseResult result = service.calculate(
                100d, CropSpacingMode.PIT, 0.5d, null, Double.NaN, 2d);

        assertThat(result.resolvedMode()).isEqualTo(CropSpacingMode.UNKNOWN);
        assertThat(result.gramsPerPit()).isNull();
        assertThat(result.technicalWarning()).isNotBlank();
    }

    @Test
    void returnsUnknownWhenPlantsPerPitIsInvalid() {
        CropSpacingCalculationService.CropSpacingDoseResult result = service.calculate(
                100d, CropSpacingMode.PIT, 0.5d, null, 0.25d, Double.POSITIVE_INFINITY);

        assertThat(result.resolvedMode()).isEqualTo(CropSpacingMode.UNKNOWN);
        assertThat(result.estimatedPopulationByPits()).isNull();
        assertThat(result.technicalWarning()).isNotBlank();
    }

    @Test
    void returnsUnknownWhenSpacingModeIsMissing() {
        CropSpacingCalculationService.CropSpacingDoseResult result = service.calculate(
                100d, null, 0.5d, null, null, null);

        assertThat(result.resolvedMode()).isEqualTo(CropSpacingMode.UNKNOWN);
        assertThat(result.technicalWarning()).isNotBlank();
    }
}
