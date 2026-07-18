package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.FormulatedMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.MineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShoppingInputCostServiceTest {

    @Test
    void calculatesFiveTwentyFiveFiftyAndThousandKgCommercialUnits() {
        assertUnit(5d, "sc");
        assertUnit(25d, "sc");
        assertUnit(50d, "sc");
        assertUnit(1000d, "t");
    }

    @Test
    void calculatesFractionalDoseAreaAndDimensionallyEquivalentTotalsWithoutEarlyRounding() {
        var cost = ShoppingInputCostService.calculate(12.5d, 2.4d,
                new ShoppingInputCostService.CommercialUnit(25d, new BigDecimal("80.00"), "sc"));

        assertThat(cost.estimatedCostPerHa()).isEqualTo(40d);
        assertThat(cost.theoreticalKg()).isEqualTo(30d);
        assertThat(cost.theoreticalCommercialQuantity()).isEqualTo(1.2d);
        assertThat(cost.estimatedTotalCost()).isEqualTo(96d);
        assertThat(cost.estimatedTotalCost()).isEqualTo(cost.estimatedCostPerHa() * 2.4d);
    }

    @Test
    void missingZeroPriceOrInvalidMassNeverBecomesFreeCost() {
        assertThat(ShoppingInputCostService.calculate(100d, 2d, null).priced()).isFalse();
        assertThat(ShoppingInputCostService.calculate(100d, 2d,
                new ShoppingInputCostService.CommercialUnit(0d, BigDecimal.TEN, "sc")).priced()).isFalse();
        assertThat(ShoppingInputCostService.calculate(100d, 2d,
                new ShoppingInputCostService.CommercialUnit(50d, BigDecimal.ZERO, "sc")).priced()).isFalse();
    }

    @Test
    void missingAreaKeepsPerHectareCostButDoesNotInventTotalQuantityOrCost() {
        var cost = ShoppingInputCostService.calculate(100d, null,
                new ShoppingInputCostService.CommercialUnit(50d, new BigDecimal("120.00"), "sc"));

        assertThat(cost.estimatedCostPerHa()).isEqualTo(240d);
        assertThat(cost.theoreticalKg()).isNull();
        assertThat(cost.theoreticalCommercialQuantity()).isNull();
        assertThat(cost.estimatedTotalCost()).isNull();
    }

    @Test
    void resolvesLimestoneGypsumAndFormulatedFromTheirOwnRegisteredCommercialPrices() {
        var simpleRepository = mock(SimpleMineralFertilizerRepository.class);
        var formulatedRepository = mock(FormulatedMineralFertilizerRepository.class);
        var mineralRepository = mock(MineralFertilizerRepository.class);
        var service = new ShoppingInputCostService(simpleRepository, formulatedRepository, mineralRepository);
        when(simpleRepository.findAllByUserAndPublicoFalseOrderByNameAsc(null)).thenReturn(java.util.List.of(
                SimpleMineralFertilizerModel.builder().name("Calcário").precoSaco1000Kg(new BigDecimal("350.00")).build(),
                SimpleMineralFertilizerModel.builder().name("Gesso agrícola").precoSaco50Kg(new BigDecimal("40.00")).build()));
        when(formulatedRepository.findAllByUserAndPublicoFalseOrderByIdAsc(null)).thenReturn(java.util.List.of(
                FormulatedMineralFertilizerModel.builder().N(0d).P2O5(20d).K2O(10d)
                        .precoSaco25Kg(new BigDecimal("75.00")).build()));
        when(mineralRepository.findAllByUserAndPublicoFalseOrderByNameAsc(null)).thenReturn(java.util.List.of());
        var recommendation = RecommendationModel.builder().origemAdubos(FertilizerSourceOption.PRIVATE).build();

        assertThat(service.estimate(recommendation, "Calcário", 1000d, 2d).commercialUnitSymbol()).isEqualTo("t");
        assertThat(service.estimate(recommendation, "Gesso agrícola", 100d, 2d).commercialUnitMassKg()).isEqualTo(50d);
        assertThat(service.estimate(recommendation, "NPK 00.00-20.00-10.00", 200d, 2d).commercialUnitMassKg()).isEqualTo(25d);
    }

    private void assertUnit(double mass, String symbol) {
        var cost = ShoppingInputCostService.calculate(100d, 2d,
                new ShoppingInputCostService.CommercialUnit(mass, new BigDecimal("100.00"), symbol));
        assertThat(cost.priced()).isTrue();
        assertThat(cost.estimatedCostPerHa()).isEqualTo(100d / mass * 100d);
        assertThat(cost.theoreticalKg()).isEqualTo(200d);
        assertThat(cost.theoreticalCommercialQuantity()).isEqualTo(200d / mass);
        assertThat(cost.estimatedTotalCost()).isEqualTo(200d / mass * 100d);
    }
}
