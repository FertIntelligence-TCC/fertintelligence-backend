package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PartFourAgronomicRulesTest {

    @Test
    void keepsSulfurAtPlantingWhenRequirementIsBelowLimit() {
        NutrientFertilizationCalculationService.SulfurPartition result =
                NutrientFertilizationCalculationService.partitionSulfur(20d);

        assertThat(result.plantingS()).isEqualTo(20d);
        assertThat(result.coverageS()).isZero();
    }

    @Test
    void acceptsExactlyTwentyFourKilogramsOfSulfurAtPlanting() {
        NutrientFertilizationCalculationService.SulfurPartition result =
                NutrientFertilizationCalculationService.partitionSulfur(24d);

        assertThat(result.plantingS()).isEqualTo(24d);
        assertThat(result.coverageS()).isZero();
    }

    @Test
    void transfersSulfurAbovePlantingLimitToCoverage() {
        NutrientFertilizationCalculationService.SulfurPartition result =
                NutrientFertilizationCalculationService.partitionSulfur(40d);

        assertThat(result.plantingS()).isEqualTo(24d);
        assertThat(result.coverageS()).isEqualTo(16d);
    }

    @Test
    void capsSulfurInTheConsolidatedCoverageApplicationAtTwentySix() {
        assertThat(NutrientFertilizationCalculationService.capCoverageSulfur(20d)).isEqualTo(20d);
        assertThat(NutrientFertilizationCalculationService.capCoverageSulfur(36d)).isEqualTo(26d);
    }

    @Test
    void rejectsCoverageFormulatedWhenOnlyNitrogenIsRequired() {
        assertThat(CoverageFormulatedFertilizerRecommendationService.hasAcceptableNkBalances(
                59d, 59d, 59d, 0d)).isFalse();
    }

    @Test
    void acceptsCoverageBalancesExactlyAtTenPercent() {
        assertThat(CoverageFormulatedFertilizerRecommendationService.hasAcceptableNkBalances(
                90d, 100d, 55d, 50d)).isTrue();
    }

    @Test
    void rejectsCoverageDeficitOrExcessAboveTenPercent() {
        assertThat(CoverageFormulatedFertilizerRecommendationService.hasAcceptableNkBalances(
                89.99d, 100d, 50d, 50d)).isFalse();
        assertThat(CoverageFormulatedFertilizerRecommendationService.hasAcceptableNkBalances(
                100d, 100d, 55.01d, 50d)).isFalse();
    }

    @Test
    void keepsTheRequestedFteAndComplementApplicationWarnings() {
        assertThat(RecommendationCalculationService.FTE_APPLICATION_WARNING)
                .isEqualTo("Misturar e aplicar conjuntamente com os adubos simples ou formulados para correção da fertilidade do solo.");
        assertThat(RecommendationCalculationService.MICRONUTRIENT_COMPLEMENT_APPLICATION_WARNING)
                .isEqualTo("Misturar com o adubo de plantio e por na linha de semeadura ou à lanço no pré-plantio, juntamente com o fósforo.");
    }

    @Test
    void gypsumKeepsSulfurSourcesOutOfNitrogenAndPhosphorusPreference() {
        SimpleMineralFertilizerModel urea = simple("Ureia", 45d, 0d, 0d);
        SimpleMineralFertilizerModel ammoniumSulfate = simple("Sulfato de amônio", 20d, 0d, 22d);
        SimpleMineralFertilizerModel triple = simple("Superfosfato Triplo", 0d, 46d, 0d);
        SimpleMineralFertilizerModel map = simple("MAP", 10d, 48d, 0d);
        SimpleMineralFertilizerModel simple = simple("Superfosfato Simples", 0d, 18d, 12d);

        assertThat(NutrientFertilizationCalculationService.isNitrogenSourceEligibleAfterGypsum(urea)).isTrue();
        assertThat(NutrientFertilizationCalculationService.isNitrogenSourceEligibleAfterGypsum(ammoniumSulfate)).isFalse();
        assertThat(NutrientFertilizationCalculationService.isPreferredPhosphorusSourceAfterGypsum(triple)).isTrue();
        assertThat(NutrientFertilizationCalculationService.isPreferredPhosphorusSourceAfterGypsum(map)).isTrue();
        assertThat(NutrientFertilizationCalculationService.isPreferredPhosphorusSourceAfterGypsum(simple)).isFalse();
    }

    private SimpleMineralFertilizerModel simple(String name, double nitrogen, double phosphorus, double sulfur) {
        return SimpleMineralFertilizerModel.builder()
                .name(name)
                .N(nitrogen)
                .P2O5(phosphorus)
                .S(sulfur)
                .build();
    }
}
