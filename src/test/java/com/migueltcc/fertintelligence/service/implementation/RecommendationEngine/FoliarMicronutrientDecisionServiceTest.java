package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.ChelatedFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.ChelatedFertilizerRepository;
import com.migueltcc.fertintelligence.repository.MineralFertilizerRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
class FoliarMicronutrientDecisionServiceTest {

    private final FoliarMicronutrientDecisionService service =
            new FoliarMicronutrientDecisionService(repository(MineralFertilizerRepository.class, List.of()),
                    repository(ChelatedFertilizerRepository.class, List.of()));

    @Test
    void boundaryIsStrictlyLowerThanPointSixtyForEverySupportedMicronutrient() {
        for (AppliedMicronutrient nutrient : List.of(
                AppliedMicronutrient.Cu, AppliedMicronutrient.Fe, AppliedMicronutrient.Mn, AppliedMicronutrient.Zn)) {
            assertThat(service.isFoliarDose(0.59)).as("%s at 0.59", nutrient).isTrue();
            assertThat(service.isFoliarDose(0.60)).as("%s at 0.60", nutrient).isFalse();
            assertThat(service.isFoliarDose(0.61)).as("%s at 0.61", nutrient).isFalse();
        }
    }

    @Test
    void soilApplicationCountUsesTwoForMediumAndThreeForLowAndVeryLow() {
        assertThat(NutrientFertilizationCalculationService.applicationsFor("Médio")).isEqualTo(2);
        assertThat(NutrientFertilizationCalculationService.applicationsFor("Baixo")).isEqualTo(3);
        assertThat(NutrientFertilizationCalculationService.applicationsFor("Muito baixo")).isEqualTo(3);
    }

    @Test
    void chelateAtTwentyAndExactlyTwentyFivePercentMoreIsSelected() {
        var mineral = alternative(100d);
        assertThat(FoliarMicronutrientDecisionService.decide(mineral, alternative(120d)).chelatedState())
                .isEqualTo(FoliarMicronutrientDecisionService.AlternativeState.SELECTED);
        assertThat(FoliarMicronutrientDecisionService.decide(mineral, alternative(125d)).chelatedState())
                .isEqualTo(FoliarMicronutrientDecisionService.AlternativeState.SELECTED);
    }

    @Test
    void chelateAtTwentySixPercentMoreRejectsChelateAndCheaperChelateIsSelected() {
        var mineral = alternative(100d);
        assertThat(FoliarMicronutrientDecisionService.decide(mineral, alternative(126d)).mineralState())
                .isEqualTo(FoliarMicronutrientDecisionService.AlternativeState.SELECTED);
        assertThat(FoliarMicronutrientDecisionService.decide(mineral, alternative(80d)).chelatedState())
                .isEqualTo(FoliarMicronutrientDecisionService.AlternativeState.SELECTED);
    }

    @Test
    void missingOneOrBothPricesKeepsBothAlternativesUndetermined() {
        var priced = alternative(100d);
        var unpriced = alternative(null);
        assertThat(FoliarMicronutrientDecisionService.decide(unpriced, priced).mineralState())
                .isEqualTo(FoliarMicronutrientDecisionService.AlternativeState.UNDETERMINED);
        assertThat(FoliarMicronutrientDecisionService.decide(priced, unpriced).chelatedState())
                .isEqualTo(FoliarMicronutrientDecisionService.AlternativeState.UNDETERMINED);
        assertThat(FoliarMicronutrientDecisionService.decide(unpriced, unpriced).message())
                .contains("ausência de preços comerciais");
    }

    @Test
    void calculatesProductKgNutrientAndCumicWithoutEarlyRoundingUsingDifferentPackagesAndConcentrations() {
        MineralFertilizerModel mineralProduct = MineralFertilizerModel.builder()
                .name("Sulfato cúprico pentahidratado").Cu(25d)
                .precoUnidadeComercial(new BigDecimal("100")).pesoUnidadeComercialKg(20d)
                .unidadeComercial("saco").build();
        ChelatedFertilizerModel chelatedProduct = ChelatedFertilizerModel.builder()
                .name("Cobre quelatado Na2CuEDTA").Cu(13d)
                .precoUnidadeComercial(new BigDecimal("62.40")).pesoUnidadeComercialKg(10d)
                .unidadeComercial("saco").build();
        FoliarMicronutrientDecisionService pricedService = new FoliarMicronutrientDecisionService(
                repository(MineralFertilizerRepository.class, List.of(mineralProduct)),
                repository(ChelatedFertilizerRepository.class, List.of(chelatedProduct)));

        List<FoliarMicronutrientDecisionService.Alternative> alternatives =
                pricedService.calculate(null, FertilizerSourceOption.DEFAULT, AppliedMicronutrient.Cu, 0.52d);

        assertThat(alternatives).hasSize(2);
        var mineral = alternatives.get(0);
        var chelated = alternatives.get(1);
        assertThat(mineral.productDoseKgHa()).isEqualTo(2.08d);
        assertThat(mineral.pricePerProductKg()).isEqualTo(5d);
        assertThat(mineral.pricePerNutrientKg()).isEqualTo(20d);
        assertThat(mineral.costPerHa()).isEqualTo(10.4d);
        assertThat(chelated.product()).isEqualTo("Cobre quelatado Na2CuEDTA");
        assertThat(chelated.concentrationPercent()).isEqualTo(13d);
        assertThat(chelated.productDoseKgHa()).isEqualTo(4d);
        assertThat(chelated.pricePerProductKg()).isEqualTo(6.24d);
        assertThat(chelated.pricePerNutrientKg()).isEqualTo(48d);
        assertThat(chelated.costPerHa()).isEqualTo(24.96d);
        assertThat(mineral.state()).isEqualTo(FoliarMicronutrientDecisionService.AlternativeState.SELECTED);
        assertThat(chelated.state()).isEqualTo(FoliarMicronutrientDecisionService.AlternativeState.NOT_SELECTED);
    }

    private FoliarMicronutrientDecisionService.Alternative alternative(Double cost) {
        return new FoliarMicronutrientDecisionService.Alternative(
                "FOLIAR_Cu", AppliedMicronutrient.Cu,
                FoliarMicronutrientDecisionService.SourceType.MINERAL_SIMPLE,
                "Produto", 10d, 0.5d, 5d, "saco", null, null, cost,
                FoliarMicronutrientDecisionService.AlternativeState.UNDETERMINED, null);
    }

    @SuppressWarnings("unchecked")
    private static <T> T repository(Class<T> type, List<?> values) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, args) -> {
                    if (method.getReturnType().equals(List.class)) return values;
                    if (method.getName().equals("toString")) return type.getSimpleName() + "Fixture";
                    return null;
                });
    }
}
