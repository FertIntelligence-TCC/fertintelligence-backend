package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.FormulatedFertilizerRatioService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FormulatedFertilizerRatioServiceTest {

    private final FormulatedFertilizerRatioService service = new FormulatedFertilizerRatioService();

    @Test
    void calculatesRecommendedRatioFromSmallestPositiveDose() {
        FormulatedFertilizerRatioService.RatioCalculationResult result =
                service.calculateRecommendedRatio(20d, 80d, 40d);

        assertThat(result.calculated()).isTrue();
        assertThat(result.ratio().getN()).isEqualTo(1d);
        assertThat(result.ratio().getP()).isEqualTo(4d);
        assertThat(result.ratio().getK()).isEqualTo(2d);
        assertThat(result.technicalMessage()).isNull();
    }

    @Test
    void keepsZeroComponentWhenAtLeastOneNutrientIsPositive() {
        FormulatedFertilizerRatioService.RatioCalculationResult result =
                service.calculateRecommendedRatio(0d, 80d, 40d);

        assertThat(result.calculated()).isTrue();
        assertThat(result.ratio().getN()).isZero();
        assertThat(result.ratio().getP()).isEqualTo(2d);
        assertThat(result.ratio().getK()).isEqualTo(1d);
    }

    @Test
    void returnsTechnicalMessageWhenAllRecommendedDosesAreZeroOrNull() {
        FormulatedFertilizerRatioService.RatioCalculationResult result =
                service.calculateRecommendedRatio(null, 0d, null);

        assertThat(result.calculated()).isFalse();
        assertThat(result.ratio()).isNull();
        assertThat(result.technicalMessage()).contains("relacao nao calculada");
    }

    @Test
    void calculatesFormulatedRatioFromRegisteredPercentages() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .N(4d)
                .P2O5(14d)
                .K2O(8d)
                .build();

        FormulatedFertilizerRatioService.RatioCalculationResult result =
                service.calculateFormulatedRatio(fertilizer);

        assertThat(result.calculated()).isTrue();
        assertThat(result.ratio().getN()).isEqualTo(1d);
        assertThat(result.ratio().getP()).isEqualTo(3.5d);
        assertThat(result.ratio().getK()).isEqualTo(2d);
    }

    @Test
    void comparesCompleteRatiosWithTolerance() {
        NPKrelation recommended = new NPKrelation(1d, 4d, 2d);
        NPKrelation formulated = new NPKrelation(1.004d, 3.996d, 2.001d);

        assertThat(service.hasCompleteRatioMatch(recommended, formulated)).isTrue();
        assertThat(service.hasCompleteRatioMatch(recommended, new NPKrelation(1d, 4d, 2.02d))).isFalse();
    }

    @Test
    void calculatesRatioAndConcentrationSums() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .N(4d)
                .P2O5(14d)
                .K2O(8d)
                .build();

        assertThat(service.calculateRatioSum(new NPKrelation(1d, 3.5d, 2d))).isEqualTo(6.5d);
        assertThat(service.calculateFormulatedConcentrationSum(fertilizer)).isEqualTo(26d);
    }
}
