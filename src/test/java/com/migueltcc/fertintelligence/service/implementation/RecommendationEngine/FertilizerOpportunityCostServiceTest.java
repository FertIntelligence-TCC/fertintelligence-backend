package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.FormulatedMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.MineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Test void appliesStrictCmToCoDecisionBoundary() {
        assertThat(FertilizerOpportunityCostService.preferSimpleSources(new BigDecimal("1.01"))).isTrue();
        assertThat(FertilizerOpportunityCostService.preferSimpleSources(new BigDecimal("1.00"))).isFalse();
        assertThat(FertilizerOpportunityCostService.preferSimpleSources(new BigDecimal("0.99"))).isFalse();
    }

    @Test void keepsSelectedFormulatedByIdWhenItHasACommercialUnit() {
        var selected = formulated(20L, 20d, 0d, 10d, "125.00");
        var duplicate = formulated(21L, 20d, 0d, 10d, "100.00");
        assertThat(service.resolveEquivalentPricedFormulated(selected, List.of(selected, duplicate))).isSameAs(selected);
    }

    @Test void resolvesTwentyZeroTenPriceFromAnExactlyEquivalentEligibleFormula() {
        var selectedWithoutPrice = formulated(20L, 20d, 0d, 10d, null);
        var priced25Kg = FormulatedMineralFertilizerModel.builder()
                .id(21L).N(20d).P2O5(0d).K2O(10d).precoSaco25Kg(new BigDecimal("75.00")).build();
        assertThat(service.resolveEquivalentPricedFormulated(selectedWithoutPrice, List.of(selectedWithoutPrice, priced25Kg)))
                .isSameAs(priced25Kg);
    }

    @Test void calculatesPcPoForTwentyZeroTenUsingTheEquivalent25KgCommercialUnit() {
        var simpleRepository = mock(SimpleMineralFertilizerRepository.class);
        var formulatedRepository = mock(FormulatedMineralFertilizerRepository.class);
        var mineralRepository = mock(MineralFertilizerRepository.class);
        var calculator = new FertilizerOpportunityCostService(simpleRepository, formulatedRepository, mineralRepository);
        var selectedWithoutPrice = formulated(20L, 20d, 0d, 10d, null);
        var priced25Kg = FormulatedMineralFertilizerModel.builder()
                .id(21L).N(20d).P2O5(0d).K2O(10d).precoSaco25Kg(new BigDecimal("75.00")).build();
        var urea = source(1L, "Ureia", 50d, "100.00");
        var potassiumChloride = SimpleMineralFertilizerModel.builder().id(2L).name("Cloreto de potássio")
                .K2O(50d).precoSaco50Kg(new BigDecimal("100.00")).build();
        when(simpleRepository.findAllByUserAndPublicoFalseOrderByNameAsc(null)).thenReturn(List.of(urea, potassiumChloride));
        when(formulatedRepository.findAllByUserAndPublicoFalseOrderByIdAsc(null)).thenReturn(List.of(selectedWithoutPrice, priced25Kg));
        when(mineralRepository.findAllByUserAndPublicoFalseOrderByNameAsc(null)).thenReturn(List.of());

        var result = calculator.calculate(null,
                com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption.PRIVATE,
                List.of(20L), new java.util.ArrayList<>());

        assertThat(result.decisions()).singleElement().satisfies(decision -> {
            assertThat(decision.commercialPrice()).isEqualByComparingTo("75.00");
            assertThat(decision.commercialWeightKg()).isEqualByComparingTo("25");
            assertThat(decision.opportunityPrice()).isEqualByComparingTo("33.00");
            assertThat(decision.ratio()).isEqualByComparingTo("2.2727");
            assertThat(decision.decision()).isEqualTo("comprar e usar adubos simples");
        });
    }

    @Test void doesNotConfuseSimilarOrNutritionallyDifferentFormula() {
        var selected = formulated(20L, 20d, 0d, 10d, null);
        var similar = formulated(21L, 20d, 0d, 11d, "100.00");
        var additionalSulfur = FormulatedMineralFertilizerModel.builder()
                .id(22L).N(20d).P2O5(0d).K2O(10d).S(5d).precoSaco50Kg(new BigDecimal("100.00")).build();
        assertThat(service.resolveEquivalentPricedFormulated(selected, List.of(selected, similar, additionalSulfur)))
                .isSameAs(selected);
    }

    private SimpleMineralFertilizerModel source(Long id, String name, double n, String price50) {
        return SimpleMineralFertilizerModel.builder().id(id).name(name).N(n)
                .precoSaco50Kg(price50 == null ? null : new BigDecimal(price50)).build();
    }

    private FormulatedMineralFertilizerModel formulated(Long id, double n, double p, double k, String price50) {
        return FormulatedMineralFertilizerModel.builder().id(id).N(n).P2O5(p).K2O(k)
                .precoSaco50Kg(price50 == null ? null : new BigDecimal(price50)).build();
    }
}
