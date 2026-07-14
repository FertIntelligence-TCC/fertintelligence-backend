package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.DiverseContentRangeRepository;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationCalculationServiceTest {

    @Test
    void automaticCorrectivePhosphorusAcceptsOnlySimpleAndTripleSuperphosphate() {
        assertThat(RecommendationCalculationService.isEligibleAutomaticCorrectivePhosphorusSource("Superfosfato Simples")).isTrue();
        assertThat(RecommendationCalculationService.isEligibleAutomaticCorrectivePhosphorusSource("Superfosfato Triplo")).isTrue();
        assertThat(RecommendationCalculationService.isEligibleAutomaticCorrectivePhosphorusSource("Termofosfato Magnesiano")).isFalse();
        assertThat(RecommendationCalculationService.isEligibleAutomaticCorrectivePhosphorusSource("TFMg")).isFalse();
    }

    @Test
    void potassiumUsesIndependentDiverseRangesForFiveLevelClassificationAndBoundaries() throws Exception {
        SoilFertilityInterpretationCriteriaTableModel table = SoilFertilityInterpretationCriteriaTableModel.builder().id(42L).build();
        DiverseContentRangeModel range = micronutrientRange(table).toBuilder()
                .potassium_too_low(1d).potassium_low_i(1d).potassium_low_f(2d)
                .potassium_medium_i(2d).potassium_medium_f(3d)
                .potassium_hight_i(3d).potassium_hight_f(4d).potassium_too_hight(4d)
                .build();
        RecommendationCalculationService service = serviceWithRepositories(diverseContentRangeRepositoryReturningByTableId(range));

        assertThat(potassiumInterpretation(service, table, 0.5d, FertilityAnalysisUnit.MMOLC_PER_DM3)).isEqualTo("Muito baixo");
        assertThat(potassiumInterpretation(service, table, 1d, FertilityAnalysisUnit.MMOLC_PER_DM3)).isEqualTo("Baixo");
        assertThat(potassiumInterpretation(service, table, 2d, FertilityAnalysisUnit.MMOLC_PER_DM3)).isEqualTo("Médio");
        assertThat(potassiumInterpretation(service, table, 3d, FertilityAnalysisUnit.MMOLC_PER_DM3)).isEqualTo("Alto");
        assertThat(potassiumInterpretation(service, table, 4d, FertilityAnalysisUnit.MMOLC_PER_DM3)).isEqualTo("Muito alto");

        assertThat(range.getPotassium_low_f()).isNotEqualTo(range.getMagnesium_low_f());
    }

    @Test
    void potassiumWithoutValueOrWithIncompatibleUnitIsNotClassified() throws Exception {
        SoilFertilityInterpretationCriteriaTableModel table = SoilFertilityInterpretationCriteriaTableModel.builder().id(42L).build();
        DiverseContentRangeModel range = micronutrientRange(table).toBuilder()
                .potassium_too_low(1d).potassium_low_i(1d).potassium_low_f(2d)
                .potassium_medium_i(2d).potassium_medium_f(3d)
                .potassium_hight_i(3d).potassium_hight_f(4d).potassium_too_hight(4d)
                .build();
        RecommendationCalculationService service = serviceWithRepositories(diverseContentRangeRepositoryReturningByTableId(range));

        assertThat(potassiumItem(service, table, null, FertilityAnalysisUnit.MMOLC_PER_DM3).getAnalyzedValue()).isNull();
        RecommendationCalculationService.SoilChemicalDiagnosisItem incompatible =
                potassiumItem(service, table, 1.5d, FertilityAnalysisUnit.CMOLC_PER_DM3);
        assertThat(incompatible.getInterpretation()).isNull();
        assertThat(incompatible.getTechnicalObservation()).contains("unidade", "incompatível");
    }

    @Test
    void potassiumWithLegacyNullLimitsIsNotClassifiedInsteadOfThrowing() throws Exception {
        SoilFertilityInterpretationCriteriaTableModel table = SoilFertilityInterpretationCriteriaTableModel.builder().id(42L).build();
        DiverseContentRangeModel legacyRange = micronutrientRange(table).toBuilder()
                .potassium_too_low(null).potassium_low_i(null).potassium_low_f(null)
                .potassium_medium_i(null).potassium_medium_f(null)
                .potassium_hight_i(null).potassium_hight_f(null).potassium_too_hight(null)
                .build();
        RecommendationCalculationService service = serviceWithRepositories(
                diverseContentRangeRepositoryReturningByTableId(legacyRange));

        RecommendationCalculationService.SoilChemicalDiagnosisItem item =
                potassiumItem(service, table, 1.5d, FertilityAnalysisUnit.MMOLC_PER_DM3);

        assertThat(item.getInterpretation()).isNull();
        assertThat(item.getTechnicalObservation()).contains("Critério incompleto");
    }

    @Test
    void gypsumSelectionAcceptsSupportedSubsurfaceDepthEquivalentsAndExcludesSurface() throws Exception {
        RecommendationCalculationService service = serviceWithRepositories(null);
        PhysicalAnalysisExtractModel surface = physical(1L, 0, 20);
        PhysicalAnalysisExtractModel twentyToForty = physical(2L, 20, 40);
        PhysicalAnalysisExtractModel fortyToSixty = physical(3L, 40, 60);

        Method method = RecommendationCalculationService.class.getDeclaredMethod(
                "selectGypsumPhysicalExtracts", List.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<PhysicalAnalysisExtractModel> selected =
                (List<PhysicalAnalysisExtractModel>) method.invoke(
                        service, List.of(surface, twentyToForty, fortyToSixty));

        assertThat(selected).extracting(PhysicalAnalysisExtractModel::getId)
                .containsExactly(2L, 3L);
    }

    @Test
    void chemicalDiagnosisLoadsMicronutrientRangesByAuthorizedTableIdBeforeEntityLookup() throws Exception {
        SoilFertilityInterpretationCriteriaTableModel table = SoilFertilityInterpretationCriteriaTableModel.builder()
                .id(42L)
                .build();
        DiverseContentRangeModel range = micronutrientRange(table);
        DiverseContentRangeRepository diverseContentRangeRepository = diverseContentRangeRepositoryReturningByTableId(range);
        RecommendationCalculationService service = serviceWithRepositories(diverseContentRangeRepository);
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

    @Test
    void chemicalDiagnosisKeepsEntityLookupFallbackWhenTableHasNoId() throws Exception {
        SoilFertilityInterpretationCriteriaTableModel table = SoilFertilityInterpretationCriteriaTableModel.builder()
                .build();
        DiverseContentRangeModel range = micronutrientRange(table);
        DiverseContentRangeRepository diverseContentRangeRepository = diverseContentRangeRepositoryReturningByEntity(range);
        RecommendationCalculationService service = serviceWithRepositories(diverseContentRangeRepository);
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

    @Test
    void formulatedPlantingWarningDoesNotMentionP2O5WhenDeficitIsZero() throws Exception {
        NutrientFertilizationCalculationService service = new NutrientFertilizationCalculationService(
                null, null, null, null, null, null, null, null, null, null, null, null);
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .id(8L)
                .N(8d)
                .P2O5(32d)
                .K2O(16d)
                .build();
        FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate candidate =
                new FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate(
                        fertilizer,
                        new NPKrelation(1d, 4d, 2d),
                        7d,
                        7d,
                        56d,
                        250d,
                        false,
                        true,
                        "P2O5",
                        85.71d,
                        20d,
                        80d,
                        40d,
                        0d,
                        0d,
                        -20d,
                        0d,
                        0d,
                        20d,
                        null);
        List<String> warnings = new ArrayList<>();

        Method method = NutrientFertilizationCalculationService.class.getDeclaredMethod(
                "buildFormulatedSelection",
                FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate.class,
                Double.class,
                Double.class,
                Double.class,
                List.class);
        method.setAccessible(true);
        method.invoke(service, candidate, 20d, 80d, 60d, warnings);

        assertThat(warnings).hasSize(1);
        assertThat(warnings.get(0))
                .contains("K2O 20.00 kg/ha")
                .contains("serão repassados para cobertura")
                .doesNotContain("déficit de P2O5 0.00")
                .doesNotContain("P2O5 0.00 kg/ha exige ajuste técnico");
    }

    @Test
    void effectiveGypsumRequiresRecommendationAndPositiveDose() {
        assertThat(RecommendationCalculationService.isEffectiveGypsumRecommendation(
                RecommendationCalculationService.GypsumRequirementResult.builder()
                        .needed(true).calculatedRequirement(1742.5d).sulfurEquivalent(261.375d).build())).isTrue();
        assertThat(RecommendationCalculationService.isEffectiveGypsumRecommendation(
                RecommendationCalculationService.GypsumRequirementResult.builder()
                        .needed(true).calculatedRequirement(0d).build())).isFalse();
        assertThat(RecommendationCalculationService.isEffectiveGypsumRecommendation(
                RecommendationCalculationService.GypsumRequirementResult.builder()
                        .needed(false).calculatedRequirement(1742.5d).build())).isFalse();
        assertThat(RecommendationCalculationService.isEffectiveGypsumRecommendation(null)).isFalse();
    }

    @Test
    void newRecommendationSelectsBr12WhenBr24IsAlsoRegistered() throws Exception {
        RecommendationCalculationService service = serviceWithRepositories(null);
        Method method = RecommendationCalculationService.class.getDeclaredMethod("selectFteBr12", List.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<SimpleMineralFertilizerModel> selected = (Optional<SimpleMineralFertilizerModel>) method.invoke(
                service,
                List.of(
                        SimpleMineralFertilizerModel.builder().name("FTE BR-24").build(),
                        SimpleMineralFertilizerModel.builder().name("FTE BR-12").build()));

        assertThat(selected).isPresent();
        assertThat(selected.orElseThrow().getName()).isEqualTo("FTE BR-12");
    }

    @Test
    void fteBalancePresentationUsesTwoDecimalsWithoutChangingInputs() throws Exception {
        RecommendationCalculationService service = serviceWithRepositories(null);
        Method method = RecommendationCalculationService.class.getDeclaredMethod(
                "micronutrientBalanceSummary", Map.class, SimpleMineralFertilizerModel.class, Double.class);
        method.setAccessible(true);
        Map<com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient, Double> recommended =
                new java.util.LinkedHashMap<>();
        recommended.put(com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient.B, 2d);
        SimpleMineralFertilizerModel fte = SimpleMineralFertilizerModel.builder().B(6d).build();
        double originalDose = 20.001d;

        String memory = (String) method.invoke(service, recommended, fte, originalDose);

        assertThat(memory).contains("recomendado 2.00", "aplicado 1.20", "saldo -0.80");
        assertThat(memory).doesNotContain("1.20006", "-0.7999400000000001");
        assertThat(originalDose).isEqualTo(20.001d);
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

    private String potassiumInterpretation(RecommendationCalculationService service,
                                            SoilFertilityInterpretationCriteriaTableModel table,
                                            Double value,
                                            FertilityAnalysisUnit unit) throws Exception {
        return potassiumItem(service, table, value, unit).getInterpretation();
    }

    private RecommendationCalculationService.SoilChemicalDiagnosisItem potassiumItem(
            RecommendationCalculationService service,
            SoilFertilityInterpretationCriteriaTableModel table,
            Double value,
            FertilityAnalysisUnit unit) throws Exception {
        FertilityAnalysisExtractModel fertility = FertilityAnalysisExtractModel.builder()
                .potassio(value)
                .unidadePotassio(unit)
                .build();
        return invokeBuildSoilChemicalDiagnosis(service, fertility, table).stream()
                .filter(item -> "Potássio (K) trocável".equals(item.getAttribute()))
                .findFirst()
                .orElseThrow()
                ;
    }

    private RecommendationCalculationService serviceWithRepositories(
            DiverseContentRangeRepository diverseContentRangeRepository) {
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
                null,
                null,
                null,
                null,
                null,
                null);
    }

    private PhysicalAnalysisExtractModel physical(long id, int start, int end) {
        return PhysicalAnalysisExtractModel.builder()
                .id(id)
                .rangeExtract(RangeExtractModel.builder()
                        .profundidade_inicial(start)
                        .profundidade_final(end)
                        .build())
                .build();
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
                .magnesium_low_f(99d)
                .build();
    }

    private DiverseContentRangeRepository diverseContentRangeRepositoryReturningByTableId(DiverseContentRangeModel range) {
        return (DiverseContentRangeRepository) Proxy.newProxyInstance(
                DiverseContentRangeRepository.class.getClassLoader(),
                new Class<?>[]{DiverseContentRangeRepository.class},
                (proxy, method, args) -> {
                    if ("findByTable".equals(method.getName())) {
                        throw new AssertionError("findByTable should not be called before findByTable_Id when table id is present");
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

    private DiverseContentRangeRepository diverseContentRangeRepositoryReturningByEntity(DiverseContentRangeModel range) {
        return (DiverseContentRangeRepository) Proxy.newProxyInstance(
                DiverseContentRangeRepository.class.getClassLoader(),
                new Class<?>[]{DiverseContentRangeRepository.class},
                (proxy, method, args) -> {
                    if ("findByTable".equals(method.getName())) {
                        return Optional.of(range);
                    }
                    if ("findByTable_Id".equals(method.getName())) {
                        throw new AssertionError("findByTable_Id should not be called when table id is absent");
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

}
