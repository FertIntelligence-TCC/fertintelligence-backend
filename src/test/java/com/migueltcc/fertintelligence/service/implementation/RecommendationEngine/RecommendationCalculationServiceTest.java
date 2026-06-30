package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import com.migueltcc.fertintelligence.repository.DiverseContentRangeRepository;
import com.migueltcc.fertintelligence.repository.KExchangeableContentRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationCalculationServiceTest {

    @Test
    void chemicalDiagnosisLoadsMicronutrientRangesByAuthorizedTableIdWhenEntityLookupIsEmpty() throws Exception {
        SoilFertilityInterpretationCriteriaTableModel table = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(42L)
                .build();
        DiverseContentRangeModel range = micronutrientRange(table);
        DiverseContentRangeRepository diverseContentRangeRepository = diverseContentRangeRepositoryReturningByTableId(range);
        KExchangeableContentRepository kExchangeableContentRepository = emptyKExchangeableContentRepository();
        RecommendationCalculationService service = serviceWithRepositories(diverseContentRangeRepository, kExchangeableContentRepository);
        FertilityAnalysisExtractModel fertility = FertilityAnalysisExtractModel.builder()
                .boro(0.4d)
                .cobre(0.8d)
                .ferro(6d)
                .manganes(8d)
                .zinco(1.2d)
                .build();

        List<RecommendationCalculationService.SoilChemicalDiagnosisItem> diagnosis =
                invokeBuildSoilChemicalDiagnosis(service, fertility, table);

        assertThat(diagnosis)
                .filteredOn(item -> List.of("Boro", "Cobre", "Ferro", "Manganês", "Zinco").contains(item.getAttribute()))
                .extracting(RecommendationCalculationService.SoilChemicalDiagnosisItem::getInterpretation)
                .containsExactly("Médio", "Médio", "Médio", "Médio", "Médio");
    }

    @SuppressWarnings("unchecked")
    private List<RecommendationCalculationService.SoilChemicalDiagnosisItem> invokeBuildSoilChemicalDiagnosis(
            RecommendationCalculationService service,
            FertilityAnalysisExtractModel fertility,
            SoilFertilityInterpretationCriteriaTableModel table) throws Exception {
        Method method = RecommendationCalculationService.class.getDeclaredMethod(
                "buildSoilChemicalDiagnosis",
                Optional.class,
                com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel.class,
                SoilFertilityInterpretationCriteriaTableModel.class,
                List.class);
        method.setAccessible(true);
        return (List<RecommendationCalculationService.SoilChemicalDiagnosisItem>) method.invoke(
                service, Optional.of(fertility), null, table, new ArrayList<String>());
    }

    private RecommendationCalculationService serviceWithRepositories(
            DiverseContentRangeRepository diverseContentRangeRepository,
            KExchangeableContentRepository kExchangeableContentRepository) {
        return new RecommendationCalculationService(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                diverseContentRangeRepository,
                kExchangeableContentRepository,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    private DiverseContentRangeModel micronutrientRange(SoilFertilityInterpretationCriteriaTableModel table) {
        return DiverseContentRangeModel.builder()
                .table(table)
                .boron_low_f(0.2d)
                .boron_medium_i(0.2d)
                .boron_medium_f(0.6d)
                .boron_hight_i(0.6d)
                .copper_low_f(0.4d)
                .copper_medium_i(0.4d)
                .copper_medium_f(1.2d)
                .copper_hight_i(1.2d)
                .iron_low_f(4d)
                .iron_medium_i(4d)
                .iron_medium_f(12d)
                .iron_hight_i(12d)
                .manganese_low_f(5d)
                .manganese_medium_i(5d)
                .manganese_medium_f(10d)
                .manganese_hight_i(10d)
                .zinc_low_f(0.8d)
                .zinc_medium_i(0.8d)
                .zinc_medium_f(1.6d)
                .zinc_hight_i(1.6d)
                .build();
    }

    private DiverseContentRangeRepository diverseContentRangeRepositoryReturningByTableId(DiverseContentRangeModel range) {
        return (DiverseContentRangeRepository) Proxy.newProxyInstance(
                DiverseContentRangeRepository.class.getClassLoader(),
                new Class<?>[]{DiverseContentRangeRepository.class},
                (proxy, method, args) -> {
                    if ("findByTable".equals(method.getName())) {
                        return Optional.empty();
                    }
                    if ("findByTable_Id".equals(method.getName()) && Long.valueOf(42L).equals(args[0])) {
                        return Optional.of(range);
                    }
                    if (Optional.class.equals(method.getReturnType())) {
                        return Optional.empty();
                    }
                    if (List.class.isAssignableFrom(method.getReturnType())) {
                        return List.of();
                    }
                    if (boolean.class.equals(method.getReturnType())) {
                        return false;
                    }
                    if (method.getReturnType().isPrimitive()) {
                        return 0;
                    }
                    return null;
                });
    }

    private KExchangeableContentRepository emptyKExchangeableContentRepository() {
        return (KExchangeableContentRepository) Proxy.newProxyInstance(
                KExchangeableContentRepository.class.getClassLoader(),
                new Class<?>[]{KExchangeableContentRepository.class},
                (proxy, method, args) -> {
                    if (Optional.class.equals(method.getReturnType())) {
                        return Optional.empty();
                    }
                    if (List.class.isAssignableFrom(method.getReturnType())) {
                        return List.of();
                    }
                    if (boolean.class.equals(method.getReturnType())) {
                        return false;
                    }
                    if (method.getReturnType().isPrimitive()) {
                        return 0;
                    }
                    return null;
                });
    }
}
