package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FertilizerOpportunityCostServiceTest {

    private final FertilizerOpportunityCostService service =
            new FertilizerOpportunityCostService(null, null, null);

    @Test
    void selectsLowestNitrogenUnitCostWithoutSulfurBenefit() {
        SimpleMineralFertilizerModel ammoniumSulfate = SimpleMineralFertilizerModel.builder()
                .id(1L).name("Sulfato de amônio").N(20d).S(22d)
                .precoSaco50Kg(new BigDecimal("150.00")).build();
        SimpleMineralFertilizerModel urea = SimpleMineralFertilizerModel.builder()
                .id(2L).name("Ureia").N(45d)
                .precoSaco50Kg(new BigDecimal("180.00")).build();

        assertThat(service.selectLowestCostSimpleSource(
                List.of(ammoniumSulfate, urea), FertilizerOpportunityCostService.Nutrient.N))
                .contains(urea);
    }

    @Test
    void preservesExistingFallbackWhenPricesAreMissing() {
        SimpleMineralFertilizerModel ammoniumSulfate = SimpleMineralFertilizerModel.builder()
                .id(1L).name("Sulfato de amônio").N(20d).S(22d).build();
        SimpleMineralFertilizerModel urea = SimpleMineralFertilizerModel.builder()
                .id(2L).name("Ureia").N(45d).build();

        assertThat(service.selectLowestCostSimpleSource(
                List.of(ammoniumSulfate, urea), FertilizerOpportunityCostService.Nutrient.N))
                .isEmpty();
    }
}
