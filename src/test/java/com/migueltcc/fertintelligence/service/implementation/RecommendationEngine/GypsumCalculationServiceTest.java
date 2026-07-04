package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.repository.DiverseContentRangeRepository;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GypsumCalculationServiceTest {

    @Test
    void doesNotEvaluateGypsumWithoutSubsurfaceSaturationExtract() {
        List<String> warnings = new ArrayList<>();
        GypsumCalculationService service = newService();

        RecommendationCalculationService.GypsumRequirementResult result = service.calculate(
                List.of(fertility(21, 40, 4.0, null, null)),
                List.of(physical(21, 40, 300.0)),
                false,
                null,
                null,
                null,
                FertilizerSourceOption.BOTH,
                warnings);

        assertThat(result.getEvaluated()).isFalse();
        assertThat(result.getNeeded()).isNull();
        assertThat(result.getJustification()).contains("Não é possível recomendar gessagem");
        assertThat(warnings).anySatisfy(warning -> assertThat(warning).contains("extrato de saturação"));
    }

    @Test
    void calculatesGypsumFromHighestSubsurfaceClayWhenCriticalConditionExists() {
        List<String> warnings = new ArrayList<>();
        GypsumCalculationService service = newService();

        RecommendationCalculationService.GypsumRequirementResult result = service.calculate(
                List.of(
                        fertility(21, 40, 4.9, null, null),
                        fertility(41, 60, 6.0, 4.0, null)),
                List.of(
                        physical(21, 40, 300.0),
                        physical(41, 60, 420.0)),
                true,
                null,
                null,
                null,
                FertilizerSourceOption.BOTH,
                warnings);

        assertThat(result.getEvaluated()).isTrue();
        assertThat(result.getNeeded()).isTrue();
        assertThat(result.getCalculatedRequirement()).isEqualTo(2100.0);
        assertThat(result.getSulfurEquivalent()).isEqualTo(315.0);
        assertThat(result.getApplicationRecommendation()).contains("30 a 90 dias antes do plantio");
        assertThat(result.getJustification()).contains("NG = 5 * 420");
    }

    @Test
    void doesNotRecommendGypsumWhenSubsurfaceIndicatorsAreNotCritical() {
        List<String> warnings = new ArrayList<>();
        GypsumCalculationService service = newService();

        RecommendationCalculationService.GypsumRequirementResult result = service.calculate(
                List.of(fertility(21, 40, 5.0, 3.0, 20.0)),
                List.of(physical(21, 40, 300.0)),
                true,
                null,
                null,
                null,
                FertilizerSourceOption.BOTH,
                warnings);

        assertThat(result.getNeeded()).isFalse();
        assertThat(result.getCalculatedRequirement()).isZero();
        assertThat(result.getJustification()).contains("Gessagem não indicada");
    }

    private GypsumCalculationService newService() {
        return new GypsumCalculationService(diverseContentRangeRepository(), simpleMineralFertilizerRepository());
    }

    private FertilityAnalysisExtractModel fertility(int start, int end, Double calcium, Double aluminum, Double aluminumSaturation) {
        return FertilityAnalysisExtractModel.builder()
                .rangeExtract(range(start, end))
                .calcio(calcium)
                .aluminio(aluminum)
                .saturacaoAluminioM(aluminumSaturation)
                .build();
    }

    private PhysicalAnalysisExtractModel physical(int start, int end, Double clay) {
        return PhysicalAnalysisExtractModel.builder()
                .rangeExtract(range(start, end))
                .teorArgila(clay)
                .build();
    }

    private RangeExtractModel range(int start, int end) {
        return RangeExtractModel.builder()
                .profundidade_inicial(start)
                .profundidade_final(end)
                .build();
    }

    private DiverseContentRangeRepository diverseContentRangeRepository() {
        return (DiverseContentRangeRepository) Proxy.newProxyInstance(
                DiverseContentRangeRepository.class.getClassLoader(),
                new Class<?>[]{DiverseContentRangeRepository.class},
                (proxy, method, args) -> Optional.class.equals(method.getReturnType()) ? Optional.empty() : defaultValue(method.getReturnType()));
    }

    private SimpleMineralFertilizerRepository simpleMineralFertilizerRepository() {
        return (SimpleMineralFertilizerRepository) Proxy.newProxyInstance(
                SimpleMineralFertilizerRepository.class.getClassLoader(),
                new Class<?>[]{SimpleMineralFertilizerRepository.class},
                (proxy, method, args) -> List.class.isAssignableFrom(method.getReturnType()) ? List.of() : defaultValue(method.getReturnType()));
    }

    private Object defaultValue(Class<?> returnType) {
        if (boolean.class.equals(returnType)) return false;
        if (returnType.isPrimitive()) return 0;
        return null;
    }
}
