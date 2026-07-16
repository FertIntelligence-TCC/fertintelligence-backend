package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class FertilizerOpportunityCostServiceTest {
    private final FertilizerOpportunityCostService service = new FertilizerOpportunityCostService(null, null, null);

    @Test void selectsLowestCostPerKgOfNutrient() {
        var ammonium = source(1L, "Sulfato de amônio", 20d, "150.00");
        var urea = source(2L, "Ureia", 45d, "180.00");
        assertThat(service.selectLowestCostSimpleSource(List.of(ammonium, urea),
                FertilizerOpportunityCostService.Nutrient.N)).contains(urea);
    }

    @Test void rejectsMissingZeroPriceAndZeroContentAndPreservesFallback() {
        var missingPrice = source(1L, "Ureia", 45d, null);
        var zeroPrice = source(2L, "Ureia zero", 45d, "0.00");
        var zeroContent = source(3L, "Ureia sem N", 0d, "100.00");
        assertThat(service.selectLowestCostSimpleSource(List.of(missingPrice, zeroPrice, zeroContent),
                FertilizerOpportunityCostService.Nutrient.N)).isEmpty();
    }

    @Test void tieIsDeterministicAndKeepsFirstEligibleSource() {
        var first = source(1L, "Ureia A", 50d, "100.00");
        var second = source(2L, "Ureia B", 50d, "100.00");
        assertThat(service.selectLowestCostSimpleSource(List.of(first, second),
                FertilizerOpportunityCostService.Nutrient.N)).contains(first);
    }

    private SimpleMineralFertilizerModel source(Long id, String name, double n, String price50) {
        return SimpleMineralFertilizerModel.builder().id(id).name(name).N(n)
                .precoSaco50Kg(price50 == null ? null : new BigDecimal(price50)).build();
    }
}
