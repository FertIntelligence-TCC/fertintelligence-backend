package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.BioFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.GreenFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganoMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.BioFertilizerRepository;
import com.migueltcc.fertintelligence.repository.GreenFertilizerRepository;
import com.migueltcc.fertintelligence.repository.OrganoMineralFertilizerRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AlternativeFertilizationCalculationServiceTest {

    private final AlternativeFertilizationCalculationService service = new AlternativeFertilizationCalculationService(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            new CropSpacingCalculationService());

    @Test
    void calculatesLinearDoseForMicronutrientFertilizerLines() throws Exception {
        CropModel crop = CropModel.builder()
                .spacingMode(CropSpacingMode.PLANTS_PER_LINEAR_METER)
                .distanceBetweenLines(0.5d)
                .plantsPerMeter(10d)
                .build();
        MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection =
                calculatedSelection(AppliedMicronutrient.B, "Fonte B", 20d, 10d);

        RecommendationCalculationService.MicronutrientFertilizerRecommendationRow row =
                invokeToDirectRecommendationMicronutrientRow(selection, crop, null);

        assertThat(row.getFertilizerDoseKgHa()).isEqualTo(10d);
        assertThat(row.getDoseUnitMode()).isEqualTo("LINEAR_METER");
        assertThat(row.getDoseUnitLabel()).isEqualTo("g/m linear");
        assertThat(row.getGramsPerLinearMeter()).isEqualTo(0.5d);
        assertThat(row.getGramsPerPit()).isNull();
        assertThat(row.getTechnicalObservation()).isNull();
    }

    @Test
    void calculatesPitDoseForMicronutrientFertilizerLines() throws Exception {
        CropModel crop = CropModel.builder()
                .spacingMode(CropSpacingMode.PIT)
                .distanceBetweenLines(0.5d)
                .distanceBetweenPits(0.25d)
                .plantsPerPit(2d)
                .build();
        MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection =
                calculatedSelection(AppliedMicronutrient.Zn, "Fonte Zn", 25d, 10d);

        RecommendationCalculationService.MicronutrientFertilizerRecommendationRow row =
                invokeToDirectRecommendationMicronutrientRow(selection, crop, null);

        assertThat(row.getFertilizerDoseKgHa()).isEqualTo(10d);
        assertThat(row.getDoseUnitMode()).isEqualTo("PIT");
        assertThat(row.getDoseUnitLabel()).isEqualTo("g/cova");
        assertThat(row.getGramsPerLinearMeter()).isNull();
        assertThat(row.getGramsPerPit()).isEqualTo(0.125d);
        assertThat(row.getTechnicalObservation()).isNull();
    }

    @Test
    void keepsKgPerHectareAndTechnicalObservationWhenSpacingDataIsIncomplete() throws Exception {
        CropModel crop = CropModel.builder()
                .spacingMode(CropSpacingMode.PIT)
                .distanceBetweenLines(0.5d)
                .plantsPerPit(2d)
                .build();
        MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection =
                calculatedSelection(AppliedMicronutrient.Zn, "Fonte Zn", 25d, 10d);

        RecommendationCalculationService.MicronutrientFertilizerRecommendationRow row =
                invokeToDirectRecommendationMicronutrientRow(selection, crop, null);

        assertThat(row.getFertilizerDoseKgHa()).isEqualTo(10d);
        assertThat(row.getDoseUnitMode()).isEqualTo("INSUFFICIENT_DATA");
        assertThat(row.getDoseUnitLabel()).isNull();
        assertThat(row.getGramsPerLinearMeter()).isNull();
        assertThat(row.getGramsPerPit()).isNull();
        assertThat(row.getTechnicalObservation()).contains("Distancia entre covas ausente ou invalida");
    }

    @Test
    void calculatesGreenFertilizerContributionAndRemainingMineralNeed() throws Exception {
        GreenFertilizerRepository greenFertilizerRepository = greenFertilizerRepositoryReturning(List.of(
                GreenFertilizerModel.builder()
                        .id(7L)
                        .name("Crotalaria")
                        .N(3d)
                        .P2O5(1d)
                        .K2O(2d)
                        .taxaMineralizacaoPrimeiroAnoPercentual(50d)
                        .build()));
        AlternativeFertilizationCalculationService service = serviceWithGreenFertilizerRepository(greenFertilizerRepository);
        List<String> warnings = new ArrayList<>();

        Object contribution = invokeCalculateGreenFertilizerContribution(
                service, true, "crotalaria", 10_000d, 80d, null, 100d, 50d, 60d, warnings);

        assertThat(invokeDouble(contribution, "remainingN")).isEqualTo(70d);
        assertThat(invokeDouble(contribution, "remainingP2O5")).isEqualTo(40d);
        assertThat(invokeDouble(contribution, "remainingK2O")).isEqualTo(40d);
        RecommendationCalculationService.AlternativeFertilizationRecommendationRow row =
                (RecommendationCalculationService.AlternativeFertilizationRecommendationRow) invokeMethod(contribution, "row");
        assertThat(row.getDose()).isEqualTo("2000");
        assertThat(row.getJustification()).contains("N 30.00, P2O5 10.00, K2O 20.00 kg/ha");
        assertThat(warnings).isEmpty();
    }

    @Test
    void keepsOrganoMineralUnselectedWhenUsageIsNotEnabled() throws Exception {
        List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        invokeAddOrganoMineralFertilizationRow(service, rows, false, 100d, 50d, 60d, warnings);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getSourceType()).isEqualTo("ORGANOMINERAL");
        assertThat(rows.get(0).getSourceName()).isEqualTo("Não selecionada");
        assertThat(rows.get(0).getJustification()).contains("não habilitado");
        assertThat(warnings).isEmpty();
    }

    @Test
    void selectsBestOrganoMineralByHighestNpkSumWhenUsageIsEnabled() throws Exception {
        OrganoMineralFertilizerRepository repository = organoMineralRepositoryReturning(List.of(
                OrganoMineralFertilizerModel.builder()
                        .id(1L)
                        .name("Organomineral 04-14-08")
                        .N(4d)
                        .P2O5(14d)
                        .K2O(8d)
                        .build(),
                OrganoMineralFertilizerModel.builder()
                        .id(2L)
                        .name("Organomineral 08-20-12")
                        .N(8d)
                        .P2O5(20d)
                        .K2O(12d)
                        .build()));
        AlternativeFertilizationCalculationService service = serviceWithOrganoMineralRepository(repository);
        List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        invokeAddOrganoMineralFertilizationRow(service, rows, true, 100d, 50d, 60d, warnings);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getSourceType()).isEqualTo("ORGANOMINERAL");
        assertThat(rows.get(0).getSourceName()).isEqualTo("Organomineral 08-20-12");
        assertThat(rows.get(0).getDose()).isEqualTo("1250.00");
        assertThat(rows.get(0).getNutrientOrObjective()).isEqualTo("NPK - nutriente alvo N");
        assertThat(warnings).containsExactly("A dose usa teor total cadastrado; o backend não possui coeficiente de eficiência agronômica, liberação gradual ou restrição específica do produto organomineral.");
    }

    @Test
    void keepsBiofertilizerUnselectedWhenUsageIsNotEnabled() throws Exception {
        List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        invokeAddBiofertilizerRow(service, rows, false, warnings);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getSourceType()).isEqualTo("BIOFERTILIZANTE");
        assertThat(rows.get(0).getSourceName()).isEqualTo("Não selecionada");
        assertThat(rows.get(0).getJustification()).contains("não habilitado");
        assertThat(warnings).isEmpty();
    }

    @Test
    void selectsBestBiofertilizerByHighestPositiveCompositionScoreWhenUsageIsEnabled() throws Exception {
        BioFertilizerRepository repository = bioFertilizerRepositoryReturning(List.of(
                BioFertilizerModel.builder()
                        .id(1L)
                        .name("Bio básico")
                        .N(2d)
                        .P2O5(1d)
                        .K2O(1d)
                        .build(),
                BioFertilizerModel.builder()
                        .id(2L)
                        .name("Bio amino")
                        .N(1d)
                        .P2O5(1d)
                        .K2O(1d)
                        .aminoacidosGl(12d)
                        .acucaresGl(6d)
                        .build()));
        AlternativeFertilizationCalculationService service = serviceWithBioFertilizerRepository(repository);
        List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        invokeAddBiofertilizerRow(service, rows, true, warnings);

        assertThat(rows).hasSize(1);
        assertThat(rows.get(0).getSourceType()).isEqualTo("BIOFERTILIZANTE");
        assertThat(rows.get(0).getSourceName()).isEqualTo("Bio amino");
        assertThat(rows.get(0).getDose()).isEqualTo("Não calculada");
        assertThat(rows.get(0).getJustification()).contains("índice 21.00");
        assertThat(warnings).containsExactly("Biofertilizante possui composição cadastrada, mas o backend não possui dose recomendada por cultura, concentração de calda, via de aplicação ou eficiência para calcular recomendação operacional.");
    }

    private RecommendationCalculationService.MicronutrientFertilizerRecommendationRow invokeToDirectRecommendationMicronutrientRow(
            MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection,
            CropModel crop,
            String technicalObservation) throws Exception {
        Method method = AlternativeFertilizationCalculationService.class.getDeclaredMethod(
                "toDirectRecommendationMicronutrientRow",
                MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult.class,
                CropModel.class,
                String.class);
        method.setAccessible(true);
        return (RecommendationCalculationService.MicronutrientFertilizerRecommendationRow) method.invoke(
                service, selection, crop, technicalObservation);
    }

    private Object invokeCalculateGreenFertilizerContribution(AlternativeFertilizationCalculationService service,
                                                              Boolean useGreenFertilizer,
                                                              String species,
                                                              Double greenMass,
                                                              Double moisture,
                                                              Double dryMass,
                                                              Double requiredN,
                                                              Double requiredP2O5,
                                                              Double requiredK2O,
                                                              List<String> warnings) throws Exception {
        Method method = AlternativeFertilizationCalculationService.class.getDeclaredMethod(
                "calculateGreenFertilizerContribution",
                com.migueltcc.fertintelligence.model.fertintelligence.UserModel.class,
                FertilizerSourceOption.class,
                Boolean.class,
                String.class,
                Double.class,
                Double.class,
                Double.class,
                Double.class,
                Double.class,
                Double.class,
                List.class);
        method.setAccessible(true);
        return method.invoke(service, null, FertilizerSourceOption.DEFAULT, useGreenFertilizer, species,
                greenMass, moisture, dryMass, requiredN, requiredP2O5, requiredK2O, warnings);
    }

    private void invokeAddOrganoMineralFertilizationRow(AlternativeFertilizationCalculationService service,
                                                        List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows,
                                                        Boolean useOrganoMineralFertilizer,
                                                        Double requiredN,
                                                        Double requiredP2O5,
                                                        Double requiredK2O,
                                                        List<String> warnings) throws Exception {
        Method method = AlternativeFertilizationCalculationService.class.getDeclaredMethod(
                "addOrganoMineralFertilizationRow",
                List.class,
                com.migueltcc.fertintelligence.model.fertintelligence.UserModel.class,
                FertilizerSourceOption.class,
                Boolean.class,
                Double.class,
                Double.class,
                Double.class,
                List.class);
        method.setAccessible(true);
        method.invoke(service, rows, null, FertilizerSourceOption.DEFAULT, useOrganoMineralFertilizer,
                requiredN, requiredP2O5, requiredK2O, warnings);
    }

    private void invokeAddBiofertilizerRow(AlternativeFertilizationCalculationService service,
                                           List<RecommendationCalculationService.AlternativeFertilizationRecommendationRow> rows,
                                           Boolean useBioFertilizer,
                                           List<String> warnings) throws Exception {
        Method method = AlternativeFertilizationCalculationService.class.getDeclaredMethod(
                "addBiofertilizerRow",
                List.class,
                com.migueltcc.fertintelligence.model.fertintelligence.UserModel.class,
                FertilizerSourceOption.class,
                Boolean.class,
                List.class);
        method.setAccessible(true);
        method.invoke(service, rows, null, FertilizerSourceOption.DEFAULT, useBioFertilizer, warnings);
    }

    private Double invokeDouble(Object target, String methodName) throws Exception {
        return (Double) invokeMethod(target, methodName);
    }

    private Object invokeMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(target);
    }

    private AlternativeFertilizationCalculationService serviceWithGreenFertilizerRepository(
            GreenFertilizerRepository greenFertilizerRepository) {
        return new AlternativeFertilizationCalculationService(
                null,
                null,
                greenFertilizerRepository,
                null,
                null,
                null,
                null,
                new CropSpacingCalculationService());
    }

    private AlternativeFertilizationCalculationService serviceWithOrganoMineralRepository(
            OrganoMineralFertilizerRepository organoMineralFertilizerRepository) {
        return new AlternativeFertilizationCalculationService(
                null,
                organoMineralFertilizerRepository,
                null,
                null,
                null,
                null,
                null,
                new CropSpacingCalculationService());
    }

    private AlternativeFertilizationCalculationService serviceWithBioFertilizerRepository(
            BioFertilizerRepository bioFertilizerRepository) {
        return new AlternativeFertilizationCalculationService(
                null,
                null,
                null,
                bioFertilizerRepository,
                null,
                null,
                null,
                new CropSpacingCalculationService());
    }

    private GreenFertilizerRepository greenFertilizerRepositoryReturning(List<GreenFertilizerModel> fertilizers) {
        InvocationHandler handler = (proxy, method, args) -> {
            if ("findAllByUser_CargoOrderByNameAsc".equals(method.getName())) {
                return fertilizers;
            }
            if (List.class.isAssignableFrom(method.getReturnType())) {
                return Collections.emptyList();
            }
            if (method.getReturnType().equals(boolean.class)) {
                return false;
            }
            if (method.getReturnType().isPrimitive()) {
                return 0;
            }
            return null;
        };
        return (GreenFertilizerRepository) Proxy.newProxyInstance(
                GreenFertilizerRepository.class.getClassLoader(),
                new Class<?>[]{GreenFertilizerRepository.class},
                handler);
    }

    private OrganoMineralFertilizerRepository organoMineralRepositoryReturning(List<OrganoMineralFertilizerModel> fertilizers) {
        InvocationHandler handler = (proxy, method, args) -> {
            if ("findAllByUser_CargoOrderByNameAsc".equals(method.getName())) {
                return fertilizers;
            }
            if (List.class.isAssignableFrom(method.getReturnType())) {
                return Collections.emptyList();
            }
            if (method.getReturnType().equals(boolean.class)) {
                return false;
            }
            if (method.getReturnType().isPrimitive()) {
                return 0;
            }
            return null;
        };
        return (OrganoMineralFertilizerRepository) Proxy.newProxyInstance(
                OrganoMineralFertilizerRepository.class.getClassLoader(),
                new Class<?>[]{OrganoMineralFertilizerRepository.class},
                handler);
    }

    private BioFertilizerRepository bioFertilizerRepositoryReturning(List<BioFertilizerModel> fertilizers) {
        InvocationHandler handler = (proxy, method, args) -> {
            if ("findAllByUser_CargoOrderByNameAsc".equals(method.getName())) {
                return fertilizers;
            }
            if (List.class.isAssignableFrom(method.getReturnType())) {
                return Collections.emptyList();
            }
            if (method.getReturnType().equals(boolean.class)) {
                return false;
            }
            if (method.getReturnType().isPrimitive()) {
                return 0;
            }
            return null;
        };
        return (BioFertilizerRepository) Proxy.newProxyInstance(
                BioFertilizerRepository.class.getClassLoader(),
                new Class<?>[]{BioFertilizerRepository.class},
                handler);
    }

    private MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult calculatedSelection(
            AppliedMicronutrient micronutrient,
            String fertilizerName,
            Double concentrationPercent,
            Double fertilizerDoseKgHa) {
        SimpleMineralFertilizerModel fertilizer = SimpleMineralFertilizerModel.builder()
                .id(1L)
                .name(fertilizerName)
                .build();
        return new MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult(
                micronutrient,
                2d,
                fertilizer,
                concentrationPercent,
                fertilizerDoseKgHa,
                null);
    }
}
