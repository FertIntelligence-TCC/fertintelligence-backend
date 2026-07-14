package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.MicronutrientDoseModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.SulfurDoseModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.ContentRangeRepository;
import com.migueltcc.fertintelligence.repository.CoverageRepository;
import com.migueltcc.fertintelligence.repository.MicronutrientDoseRepository;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.SulfurDoseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Service
class NutrientFertilizationCalculationService {

    static final double MAX_PLANTING_S_KG_HA = 24d;
    static final double MAX_COVERAGE_S_KG_HA = 26d;

    private final ContentRangeRepository contentRangeRepository;
    private final CoverageRepository coverageRepository;
    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;
    private final MicronutrientDoseRepository micronutrientDoseRepository;
    private final SulfurDoseRepository sulfurDoseRepository;
    private final AlternativeFertilizationCalculationService alternativeFertilizationCalculationService;
    private final FormulatedFertilizerSelectionService formulatedFertilizerSelectionService;
    private final MicronutrientFertilizerSelectionService micronutrientFertilizerSelectionService;
    private final PlantingFormulatedFertilizerRecommendationService plantingFormulatedFertilizerRecommendationService;
    private final CoverageFormulatedFertilizerRecommendationService coverageFormulatedFertilizerRecommendationService;
    private final CropSpacingCalculationService cropSpacingCalculationService;
    private final FertilizerOpportunityCostService fertilizerOpportunityCostService;

    NutrientFertilizationCalculationService(ContentRangeRepository contentRangeRepository,
                                            CoverageRepository coverageRepository,
                                            SimpleMineralFertilizerRepository simpleMineralFertilizerRepository,
                                            MicronutrientDoseRepository micronutrientDoseRepository,
                                            SulfurDoseRepository sulfurDoseRepository,
                                            AlternativeFertilizationCalculationService alternativeFertilizationCalculationService,
                                            FormulatedFertilizerSelectionService formulatedFertilizerSelectionService,
                                            MicronutrientFertilizerSelectionService micronutrientFertilizerSelectionService,
                                            PlantingFormulatedFertilizerRecommendationService plantingFormulatedFertilizerRecommendationService,
                                            CoverageFormulatedFertilizerRecommendationService coverageFormulatedFertilizerRecommendationService,
                                            CropSpacingCalculationService cropSpacingCalculationService,
                                            FertilizerOpportunityCostService fertilizerOpportunityCostService) {
        this.contentRangeRepository = contentRangeRepository;
        this.coverageRepository = coverageRepository;
        this.simpleMineralFertilizerRepository = simpleMineralFertilizerRepository;
        this.micronutrientDoseRepository = micronutrientDoseRepository;
        this.sulfurDoseRepository = sulfurDoseRepository;
        this.alternativeFertilizationCalculationService = alternativeFertilizationCalculationService;
        this.formulatedFertilizerSelectionService = formulatedFertilizerSelectionService;
        this.micronutrientFertilizerSelectionService = micronutrientFertilizerSelectionService;
        this.plantingFormulatedFertilizerRecommendationService = plantingFormulatedFertilizerRecommendationService;
        this.coverageFormulatedFertilizerRecommendationService = coverageFormulatedFertilizerRecommendationService;
        this.cropSpacingCalculationService = cropSpacingCalculationService;
        this.fertilizerOpportunityCostService = fertilizerOpportunityCostService;
    }

    FertilizationRecommendationContext calculate(CropFertilizationTableModel table,
                                                 CropModel crop,
                                                 Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                 Optional<PhysicalAnalysisExtractModel> physicalExtract,
                                                 SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                                 boolean effectiveGypsumRecommendation,
                                                 UserModel user,
                                                 FertilizerSourceOption sourceOption,
                                                 Boolean useOrganicFertilizer,
                                                 Nutriente organicFertilizerReferenceNutrient,
                                                 Boolean useOrganoMineralFertilizer,
                                                 Boolean useGreenFertilizer,
                                                 String greenFertilizerSpecies,
                                                 Double greenFertilizerGreenMass,
                                                 Double greenFertilizerMoisturePercentage,
                                                 Double greenFertilizerDryMass,
                                                 Boolean useBioFertilizer,
                                                 List<String> warnings,
                                                 List<RecommendationCalculationService.SoilChemicalDiagnosisItem> chemicalDiagnosis,
                                                 List<RecommendationCalculationService.FoliarDiagnosisItem> foliarDiagnosis,
                                                 List<RecommendationCalculationService.CorrectiveFertilizationRow> correctiveFertilizationRows) {
        List<RecommendationCalculationService.FertilizationRecommendationRow> recommendationRows = new ArrayList<>();
        List<RecommendationCalculationService.FertilizerSuggestion> fertilizerSuggestions = new ArrayList<>();
        Map<AppliedMicronutrient, Double> correctiveFteCredits = correctiveFteMicronutrientCredits(
                correctiveFertilizationRows, user, sourceOption, warnings);

        Optional<ContentRangeModel> nRange = selectNitrogenRange(table);
        Optional<ContentRangeModel> pRange = selectNutrientRange(table, Nutriente.FOSFORO, extractPhosphorusValue(fertilityExtract), warnings, "fósforo (P) disponível em mg/dm³");
        Optional<ContentRangeModel> kRange = selectNutrientRange(table, Nutriente.POTASSIO, extractPotassiumValue(fertilityExtract), warnings, "potássio (K) trocável em mmolc/dm³");

        Double requiredN = nRange.map(ContentRangeModel::getApplication).orElse(null);
        Double requiredP2O5 = pRange.map(ContentRangeModel::getApplication).orElse(null);
        Double requiredK2O = kRange.map(ContentRangeModel::getApplication).orElse(null);
        Long nRangeId = nRange.map(ContentRangeModel::getId).orElse(null);
        Long pRangeId = pRange.map(ContentRangeModel::getId).orElse(null);
        Long kRangeId = kRange.map(ContentRangeModel::getId).orElse(null);
        SulfurPlantingRequirement sulfurRequirement = effectiveGypsumRecommendation
                ? SulfurPlantingRequirement.suppliedByGypsum()
                : resolveSulfurRequirement(fertilityExtract, physicalExtract, soilInterpretationTable, chemicalDiagnosis, warnings);

        if (nRange.isEmpty()) warnings.add("Não foi encontrado intervalo para NITROGENIO na tabela selecionada.");
        if (pRange.isEmpty()) warnings.add("Não foi encontrado intervalo para FOSFORO na tabela selecionada.");
        if (kRange.isEmpty()) warnings.add("Não foi encontrado intervalo para POTASSIO na tabela selecionada.");

        AlternativeFertilizationCalculationService.AlternativeFertilizationCalculationResult alternativeFertilizationResult =
                alternativeFertilizationCalculationService.calculate(
                        requiredN, requiredP2O5, requiredK2O, crop, chemicalDiagnosis, foliarDiagnosis,
                        soilInterpretationTable, user, sourceOption, useOrganicFertilizer,
                        organicFertilizerReferenceNutrient, useOrganoMineralFertilizer, useGreenFertilizer, greenFertilizerSpecies,
                        greenFertilizerGreenMass, greenFertilizerMoisturePercentage, greenFertilizerDryMass,
                        useBioFertilizer, correctiveFteCredits, warnings);
        Double mineralRequiredN = alternativeFertilizationResult.remainingRequiredN();
        Double mineralRequiredP2O5 = alternativeFertilizationResult.remainingRequiredP2O5();
        Double mineralRequiredK2O = alternativeFertilizationResult.remainingRequiredK2O();

        FertilizerSelection planting = selectBestPlantingFertilizer(user, sourceOption, mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O, warnings);
        SulfurPlantingPlan sulfurPlan = effectiveGypsumRecommendation
                ? SulfurPlantingPlan.empty()
                : buildSulfurPlantingPlan(sulfurRequirement, planting, mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O,
                        user, sourceOption, warnings);
        if (!sulfurPlan.replacesPlanting()) {
            planting.suggestion().ifPresent(fertilizerSuggestions::add);
        }
        sulfurPlan.suggestions().forEach(fertilizerSuggestions::add);
        SimplePlantingPackageResult simplePlantingPackage = buildSimplePlantingPackage(
                sulfurRequirement, effectiveGypsumRecommendation, mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O, crop, user, sourceOption, warnings);
        recommendationRows.addAll(simplePlantingPackage.rows());
        simplePlantingPackage.suggestions().forEach(fertilizerSuggestions::add);
        NutrientBalanceAccumulator nutrientBalance = new NutrientBalanceAccumulator(mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O, sulfurRequirement.totalRequiredS());
        boolean simpleOnlyPlanting = FormulatedFertilizerSelectionService.hasSinglePositiveNutrient(
                mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O);
        nutrientBalance.addPlanting(
                sulfurPlan.replacesPlanting() ? sulfurPlan.providedN() : simpleOnlyPlanting ? simplePlantingPackage.providedN() : planting.providedN(),
                sulfurPlan.replacesPlanting() ? sulfurPlan.providedP2O5() : simpleOnlyPlanting ? simplePlantingPackage.providedP2O5() : planting.providedP2O5(),
                sulfurPlan.replacesPlanting() ? sulfurPlan.providedK2O() : simpleOnlyPlanting ? simplePlantingPackage.providedK2O() : planting.providedK2O(),
                sulfurPlan.replacesPlanting() ? sulfurPlan.providedS() : simpleOnlyPlanting ? simplePlantingPackage.providedS() : planting.providedS());
        CoverageDemand coverageDemand = buildCoverageDemand(
                List.of(nRange.orElse(null), pRange.orElse(null), kRange.orElse(null)),
                nutrientBalance,
                warnings);

        if (!sulfurPlan.replacesPlanting()) {
            recommendationRows.add(RecommendationCalculationService.FertilizationRecommendationRow.builder()
                    .phase("Opção 1 - Plantio com formulado")
                    .nutrients(String.format("N: %.2f kg/ha, P2O5: %.2f kg/ha, K2O: %.2f kg/ha, S: %.2f kg/ha",
                            nvl(mineralRequiredN), nvl(mineralRequiredP2O5), nvl(mineralRequiredK2O), sulfurRequirement.requiredS()))
                    .suggestedFertilizer(planting.name())
                    .fertilizerQuantityKgHa(planting.quantityKgHa())
                    .providedN(planting.providedN())
                    .providedP2O5(planting.providedP2O5())
                    .providedK2O(planting.providedK2O())
                    .providedS(planting.providedS())
                    .balanceN(planting.balanceN())
                    .balanceP2O5(planting.balanceP2O5())
                    .balanceK2O(planting.balanceK2O())
                    .balanceS(round2(planting.providedS() - sulfurRequirement.requiredS()))
                    .limitingNutrient(planting.limitingNutrient())
                    .targetNeedKgHa(planting.targetNeedKgHa())
                    .productConcentrationPercent(planting.productConcentrationPercent())
                    .calculationMemory(planting.calculationMemory())
                    .warning(planting.warning())
                    .applicationMode("Aplicação no plantio, conforme recomendação técnica.")
                    .source("Tabela de adubação de culturas ID " + table.getId())
                    .build());
        }
        recommendationRows.addAll(sulfurPlan.rows());

        List<RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow> coverageFormulatedRows =
                coverageFormulatedFertilizerRecommendationService.calculate(
                        user, sourceOption, coverageDemand.toFormulatedRecommendations(), crop, warnings);
        recommendationRows.addAll(buildCoverageOptionOneRows(
                coverageDemand, coverageFormulatedRows, crop, user, sourceOption, fertilizerSuggestions, warnings));
        recommendationRows.addAll(buildCoverageOptionTwoRows(
                coverageDemand, crop, user, sourceOption, fertilizerSuggestions, nutrientBalance, warnings));
        recommendationRows.add(RecommendationCalculationService.FertilizationRecommendationRow.builder()
                .phase("Balanço global NPK")
                .nutrients("Consolidado após plantio e coberturas recomendadas")
                .suggestedFertilizer("Não se aplica")
                .applicationMode("Memória de cálculo consolidada em kg/ha.")
                .providedN(nutrientBalance.providedTotalN())
                .providedP2O5(nutrientBalance.providedTotalP2O5())
                .providedK2O(nutrientBalance.providedTotalK2O())
                .providedS(nutrientBalance.providedTotalS())
                .balanceN(nutrientBalance.balanceN())
                .balanceP2O5(nutrientBalance.balanceP2O5())
                .balanceK2O(nutrientBalance.balanceK2O())
                .balanceS(nutrientBalance.balanceS())
                .source("Balanço global calculado pelo backend")
                .build());
        List<RecommendationCalculationService.NutrientBalanceRow> nutrientBalanceRows = nutrientBalance.toRows();

        List<RecommendationCalculationService.PlantingFormulatedFertilizerRecommendationRow> plantingFormulatedRows =
                plantingFormulatedFertilizerRecommendationService.calculate(
                        user, sourceOption, mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O, crop, warnings);
        List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> micronutrientFertilizerRows =
                alternativeFertilizationResult.directRecommendationRows();
        if (micronutrientFertilizerRows == null || micronutrientFertilizerRows.isEmpty()) {
            micronutrientFertilizerRows = buildMicronutrientFertilizerRows(
                    fertilityExtract, soilInterpretationTable, user, sourceOption, crop, warnings, chemicalDiagnosis,
                    correctiveFteCredits);
        }

        return new FertilizationRecommendationContext(
                FertilizationRecommendationOrder.sort(recommendationRows), fertilizerSuggestions, nutrientBalanceRows,
                alternativeFertilizationResult.alternativeRows(),
                micronutrientFertilizerRows,
                plantingFormulatedRows,
                coverageFormulatedRows,
                mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O, sulfurRequirement.totalRequiredS(), nRangeId, pRangeId, kRangeId);
    }

    private SulfurPlantingRequirement resolveSulfurRequirement(
            Optional<FertilityAnalysisExtractModel> fertilityExtract,
            Optional<PhysicalAnalysisExtractModel> physicalExtract,
            SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
            List<RecommendationCalculationService.SoilChemicalDiagnosisItem> chemicalDiagnosis,
            List<String> warnings) {
        if (fertilityExtract.isEmpty() || fertilityExtract.get().getEnxofre() == null) {
            addWarning(warnings, "Enxofre no plantio não calculado porque não há teor de S no extrato de fertilidade selecionado.");
            return SulfurPlantingRequirement.notCalculated();
        }
        if (!isArableLayer020(fertilityExtract.get())) {
            addWarning(warnings, "Enxofre no plantio não calculado: a análise de fertilidade usada não corresponde à camada arável 0-20 cm.");
            return SulfurPlantingRequirement.notCalculated();
        }
        if (physicalExtract.isEmpty() || physicalExtract.get().getTeorArgila() == null) {
            addWarning(warnings, "Enxofre no plantio não calculado porque não há textura/argila da camada arável 0-20 cm.");
            return SulfurPlantingRequirement.notCalculated();
        }
        if (!isArableLayer020(physicalExtract.get())) {
            addWarning(warnings, "Enxofre no plantio não calculado: a análise física usada não corresponde à camada arável 0-20 cm.");
            return SulfurPlantingRequirement.notCalculated();
        }
        String interpretation = interpretationFor(chemicalDiagnosis, "Enxofre");
        if (interpretation == null || "Não classificado".equalsIgnoreCase(interpretation)) {
            addWarning(warnings, "Enxofre no plantio não calculado porque o teor de S não foi classificado por critério suficiente.");
            return SulfurPlantingRequirement.notCalculated();
        }
        Optional<SulfurDoseModel> sulfurDose = sulfurDoseRepository.findByTable(soilInterpretationTable);
        if (sulfurDose.isEmpty()) {
            addWarning(warnings, "Enxofre no plantio não calculado porque não há tabela auxiliar Doses de S vinculada à tabela de interpretação selecionada.");
            return SulfurPlantingRequirement.notCalculated();
        }
        boolean less400 = physicalExtract.get().getTeorArgila() < 400d;
        Double rawDose = selectSulfurDose(sulfurDose.get(), less400, interpretation);
        if (rawDose == null) {
            addWarning(warnings, "Enxofre no plantio não calculado porque a classe " + interpretation + " não possui dose preenchida na tabela auxiliar Doses de S.");
            return SulfurPlantingRequirement.notCalculated();
        }
        double dose = round2(Math.max(0d, Math.min(60d, rawDose)));
        if (Double.compare(dose, rawDose) != 0) {
            addWarning(warnings, "Dose de S ajustada para o limite operacional de 0 a 60 kg/ha; dose cadastrada: " + rawDose + " kg/ha.");
        }
        SulfurPartition partition = partitionSulfur(dose);
        return new SulfurPlantingRequirement(partition.plantingS(), partition.coverageS(), dose, interpretation,
                "S disponível em 0-20 cm = " + formatNumber(fertilityExtract.get().getEnxofre())
                        + " mg/dm³; argila 0-20 cm = " + formatNumber(physicalExtract.get().getTeorArgila())
                        + " g/kg; classe " + interpretation + "; Doses de S ID " + sulfurDose.get().getId()
                        + "; S total = " + formatNumber(dose) + " kg/ha; S no plantio = "
                        + formatNumber(partition.plantingS()) + " kg/ha; S transferido para cobertura = "
                        + formatNumber(partition.coverageS()) + " kg/ha.",
                true);
    }

    static SulfurPartition partitionSulfur(double totalS) {
        double safeTotal = Double.isFinite(totalS) ? Math.max(0d, totalS) : 0d;
        double planting = Math.min(safeTotal, MAX_PLANTING_S_KG_HA);
        return new SulfurPartition(round2Static(planting), round2Static(Math.max(0d, safeTotal - planting)));
    }

    private static double round2Static(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    static double capCoverageSulfur(double sulfurDeficit) {
        return round2Static(Math.min(MAX_COVERAGE_S_KG_HA,
                Double.isFinite(sulfurDeficit) ? Math.max(0d, sulfurDeficit) : 0d));
    }

    private SulfurPlantingPlan buildSulfurPlantingPlan(
            SulfurPlantingRequirement requirement,
            FertilizerSelection planting,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<String> warnings) {
        if (!requirement.calculated()) {
            return SulfurPlantingPlan.empty();
        }
        if (isFormulated(planting)) {
            return buildFormulatedSulfurPlan(requirement, planting, user, sourceOption, warnings);
        }
        return buildSimpleSulfurPlan(requirement, requiredN, requiredP2O5, requiredK2O, user, sourceOption, warnings);
    }

    private SulfurPlantingPlan buildFormulatedSulfurPlan(
            SulfurPlantingRequirement requirement,
            FertilizerSelection planting,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<String> warnings) {
        List<RecommendationCalculationService.FertilizationRecommendationRow> rows = new ArrayList<>();
        List<RecommendationCalculationService.FertilizerSuggestion> suggestions = new ArrayList<>();
        double deficit = round2(requirement.requiredS() - planting.providedS());
        double tolerance = tolerance(requirement.requiredS());
        if (requirement.requiredS() <= 0d) {
            rows.add(sulfurRow("Opção 1 - Plantio com formulado - S", "Não se aplica", 0d, 0d, 0d, 0d, requirement,
                    "Teor de S sem demanda de complemento no plantio.", null));
            return new SulfurPlantingPlan(rows, suggestions, 0d, 0d, 0d, 0d, false);
        }
        if (deficit > tolerance) {
            SimpleMineralFertilizerModel simpleSuper = selectNamedOrBest(selectSimpleFertilizers(user, sourceOption),
                    "superfosfato simples", f -> nvl(f.getP2O5()) > 0d && nvl(f.getS()) > 0d,
                    Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getS())));
            if (simpleSuper == null) {
                String warning = "Superfosfato simples não encontrado com teores cadastrados de P2O5 e S para completar o S do plantio.";
                addWarning(warnings, warning);
                rows.add(sulfurRow("Opção 1 - Plantio com formulado - S", "Alternativa técnica necessária", null,
                        0d, 0d, 0d, requirement, "Saldo de S mantido como pendência explícita.", warning));
                return new SulfurPlantingPlan(rows, suggestions, 0d, 0d, 0d, 0d, false);
            }
            double dose = round2(100d * deficit / nvl(simpleSuper.getS()));
            double providedP2O5 = round2(dose * nvl(simpleSuper.getP2O5()) / 100d);
            rows.add(sulfurRow("Opção 1 - Plantio com formulado - S", simpleSuper.getName(), dose,
                    round2(dose * nvl(simpleSuper.getN()) / 100d), providedP2O5,
                    round2(dose * nvl(simpleSuper.getS()) / 100d), requirement,
                    "Aplicar conjuntamente no plantio; o P2O5 fornecido deve ser creditado no balanço.",
                    "Complemento calculado por 100 * déficit de S / teor % S cadastrado do superfosfato simples."));
            addSuggestion(suggestions, simpleSuper, "SIMPLES", "Complemento de S do formulado no plantio por superfosfato simples.");
            return new SulfurPlantingPlan(rows, suggestions,
                    round2(dose * nvl(simpleSuper.getN()) / 100d), providedP2O5,
                    round2(dose * nvl(simpleSuper.getK2O()) / 100d), round2(dose * nvl(simpleSuper.getS()) / 100d), false);
        }
        if (planting.providedS() - requirement.requiredS() > tolerance) {
            String warning = "O formulado selecionado fornece S acima da tolerância de 10%; avaliar formulado alternativo sem gerar complemento inconsistente.";
            addWarning(warnings, warning);
            rows.add(sulfurRow("Opção 1 - Plantio com formulado - S", "Alternativa técnica necessária", null, 0d, 0d, 0d, requirement,
                    "Não foi aplicado complemento de S.", warning));
        }
        return new SulfurPlantingPlan(rows, suggestions, 0d, 0d, 0d, 0d, false);
    }

    private SulfurPlantingPlan buildSimpleSulfurPlan(
            SulfurPlantingRequirement requirement,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<String> warnings) {
        SimplePackage pkg = new SimplePackage();
        List<SimpleMineralFertilizerModel> simples = selectSimpleFertilizers(user, sourceOption);
        SimpleMineralFertilizerModel ammoniumSulfate = selectNamedOrBest(simples, "sulfato de amonio",
                f -> nvl(f.getN()) > 0d && nvl(f.getS()) > 0d,
                Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getN())));
        if (nvl(requiredN) > 0d && ammoniumSulfate != null) {
            double doseByN = 100d * nvl(requiredN) / nvl(ammoniumSulfate.getN());
            double suppliedS = doseByN * nvl(ammoniumSulfate.getS()) / 100d;
            boolean suppliesAllPlantingSulfur = suppliedS + tolerance(requirement.requiredS()) >= requirement.requiredS();
            pkg.add(ammoniumSulfate.getName(), doseByN, nvl(ammoniumSulfate.getN()),
                    nvl(ammoniumSulfate.getP2O5()), nvl(ammoniumSulfate.getK2O()), nvl(ammoniumSulfate.getS()),
                    suppliesAllPlantingSulfur
                            ? "Sulfato de amônio calculado pela necessidade de N e capaz de fornecer toda a parcela de S do plantio."
                            : "Sulfato de amônio calculado exclusivamente pela necessidade de N; o S fornecido foi creditado e o saldo será completado por superfosfato simples.");
        }
        double sDeficit = requirement.requiredS() - pkg.s;
        SimpleMineralFertilizerModel simpleSuper = selectNamedOrBest(simples, "superfosfato simples",
                f -> nvl(f.getP2O5()) > 0d && nvl(f.getS()) > 0d,
                Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getS())));
        if (sDeficit > tolerance(requirement.requiredS()) && simpleSuper != null) {
            double doseByS = 100d * sDeficit / nvl(simpleSuper.getS());
            pkg.add(simpleSuper.getName(), doseByS, nvl(simpleSuper.getN()), nvl(simpleSuper.getP2O5()),
                    nvl(simpleSuper.getK2O()), nvl(simpleSuper.getS()),
                    "Superfosfato simples calculado pelo saldo de S do plantio; P2O5 fornecido creditado no balanço.");
        }
        double p2o5Remaining = Math.max(0d, nvl(requiredP2O5) - pkg.p2o5);
        if (p2o5Remaining > tolerance(nvl(requiredP2O5))) {
            SimpleMineralFertilizerModel phosphorus = selectNamedOrBest(simples, "map", f -> nvl(f.getP2O5()) > 0d,
                    Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getP2O5())));
            if (phosphorus != null) {
                pkg.add(phosphorus.getName(), 100d * p2o5Remaining / nvl(phosphorus.getP2O5()),
                        nvl(phosphorus.getN()), nvl(phosphorus.getP2O5()), nvl(phosphorus.getK2O()), nvl(phosphorus.getS()),
                        "Complemento de P2O5 remanescente após superfosfato simples.");
            }
        }
        if (nvl(requiredK2O) > 0d) {
            SimpleMineralFertilizerModel potassium = selectNamedOrBest(simples, "cloreto de potassio", f -> nvl(f.getK2O()) > 0d,
                    Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getK2O())));
            if (potassium != null) {
                pkg.add(potassium.getName(), 100d * nvl(requiredK2O) / nvl(potassium.getK2O()),
                        nvl(potassium.getN()), nvl(potassium.getP2O5()), nvl(potassium.getK2O()), nvl(potassium.getS()),
                        "Fornecimento de K2O por fonte simples cadastrada.");
            }
        }
        if (!withinTolerance(pkg.n, nvl(requiredN)) || !withinTolerance(pkg.p2o5, nvl(requiredP2O5))
                || !withinTolerance(pkg.k2o, nvl(requiredK2O)) || !withinTolerance(pkg.s, requirement.requiredS())) {
            String warning = String.format(Locale.US,
                    "Não foi possível fechar adubos simples no plantio dentro da tolerância de 10%%. Saldos: N %+.2f, P2O5 %+.2f, K2O %+.2f, S %+.2f kg/ha. Avaliar parcelamento em cobertura ou fonte comercial alternativa.",
                    pkg.n - nvl(requiredN), pkg.p2o5 - nvl(requiredP2O5), pkg.k2o - nvl(requiredK2O), pkg.s - requirement.requiredS());
            addWarning(warnings, warning);
            return new SulfurPlantingPlan(List.of(sulfurRow("Opção 2 - Plantio com adubos simples - S", "Alternativa técnica necessária", null, 0d, 0d, 0d,
                    requirement, "Balanço com adubos simples não fechado.", warning)), List.of(), 0d, 0d, 0d, 0d, false);
        }
        return pkg.toPlan(requirement, nvl(requiredN), nvl(requiredP2O5), nvl(requiredK2O));
    }

    private RecommendationCalculationService.FertilizationRecommendationRow sulfurRow(
            String phase,
            String source,
            Double doseKgHa,
            double providedN,
            double providedP2O5,
            double providedS,
            SulfurPlantingRequirement requirement,
            String application,
            String warning) {
        return RecommendationCalculationService.FertilizationRecommendationRow.builder()
                .phase(phase)
                .nutrients(String.format(Locale.US, "S: %.2f kg/ha", requirement.requiredS()))
                .suggestedFertilizer(source)
                .fertilizerQuantityKgHa(doseKgHa)
                .applicationMode(application)
                .source("Tabela auxiliar Doses de S; camada arável 0-20 cm.")
                .providedN(round2(providedN))
                .providedP2O5(round2(providedP2O5))
                .providedK2O(0d)
                .providedS(providedS)
                .balanceS(round2(providedS - requirement.requiredS()))
                .limitingNutrient("S")
                .targetNeedKgHa(requirement.requiredS())
                .productConcentrationPercent(sulfurPercent(source))
                .calculationMemory(requirement.memory() + (warning == null ? "" : " " + warning))
                .warning(warning)
                .build();
    }

    private RecommendationCalculationService.FertilizerSuggestion fixedSuggestion(
            String type, String name, double n, double p2o5, double k2o, double s, String reason) {
        return RecommendationCalculationService.FertilizerSuggestion.builder()
                .fertilizerType(type)
                .fertilizerName(name)
                .n(n)
                .p2o5(p2o5)
                .k2o(k2o)
                .s(s)
                .reason(reason)
                .build();
    }

    private boolean isFormulated(FertilizerSelection planting) {
        return planting != null && planting.suggestion().isPresent()
                && "FORMULADO".equalsIgnoreCase(planting.suggestion().get().getFertilizerType());
    }

    private Double selectSulfurDose(SulfurDoseModel doses, boolean less400, String interpretation) {
        if (doses == null || interpretation == null) return null;
        return switch (interpretation) {
            case "Muito baixo" -> less400 ? doses.getLess400VeryLowDose() : doses.getGreater400VeryLowDose();
            case "Baixo" -> less400 ? doses.getLess400LowDose() : doses.getGreater400LowDose();
            case "Médio" -> less400 ? doses.getLess400MediumDose() : doses.getGreater400MediumDose();
            case "Alto" -> less400 ? doses.getLess400HighDose() : doses.getGreater400HighDose();
            case "Muito alto" -> less400 ? doses.getLess400VeryHighDose() : doses.getGreater400VeryHighDose();
            default -> null;
        };
    }

    private boolean isArableLayer020(FertilityAnalysisExtractModel extract) {
        if (extract == null) return false;
        if (extract.getRangeExtract() != null) {
            return isZeroTwenty(extract.getRangeExtract().getProfundidade_inicial(), extract.getRangeExtract().getProfundidade_final());
        }
        if (extract.getLayerExtract() != null) {
            return isZeroTwenty(extract.getLayerExtract().getProfundidade_inicial(), extract.getLayerExtract().getProfundidade_final());
        }
        return false;
    }

    private boolean isArableLayer020(PhysicalAnalysisExtractModel extract) {
        if (extract == null) return false;
        if (extract.getRangeExtract() != null) {
            return isZeroTwenty(extract.getRangeExtract().getProfundidade_inicial(), extract.getRangeExtract().getProfundidade_final());
        }
        if (extract.getLayerExtract() != null) {
            return isZeroTwenty(extract.getLayerExtract().getProfundidade_inicial(), extract.getLayerExtract().getProfundidade_final());
        }
        return false;
    }

    private boolean isZeroTwenty(Integer start, Integer end) {
        return start != null && end != null && start == 0 && end == 20;
    }

    private boolean withinTolerance(double provided, double required) {
        return Math.abs(provided - required) <= tolerance(required);
    }

    private double tolerance(double required) {
        return Math.max(0.01d, Math.abs(required) * 0.10d);
    }

    private Double sulfurPercent(String source) {
        return switch (source) {
            case "Sulfato de amônio" -> 22d;
            case "Superfosfato simples" -> 11d;
            case "Gesso agrícola" -> 15d;
            case "Enxofre elementar" -> 100d;
            default -> null;
        };
    }

    private String formatNumber(Double value) {
        return value == null ? "não informado" : String.format(Locale.US, "%.2f", value);
    }

    private List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> buildMicronutrientFertilizerRows(
            Optional<FertilityAnalysisExtractModel> fertilityExtract,
            SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
            UserModel user,
            FertilizerSourceOption sourceOption,
            CropModel crop,
            List<String> warnings,
            List<RecommendationCalculationService.SoilChemicalDiagnosisItem> chemicalDiagnosis,
            Map<AppliedMicronutrient, Double> correctiveFteCredits) {
        if (fertilityExtract.isEmpty()) {
            addWarning(warnings, "Micronutrientes não calculados porque não há extrato de fertilidade selecionado.");
            return List.of();
        }
        Optional<MicronutrientDoseModel> micronutrientDose = micronutrientDoseRepository.findByTable(soilInterpretationTable);
        if (micronutrientDose.isEmpty()) {
            addWarning(warnings, "Micronutrientes não calculados porque não há tabela auxiliar Doses de Micronutrientes vinculada à tabela de interpretação selecionada.");
            return List.of();
        }

        Map<AppliedMicronutrient, MicronutrientRecommendationInput> inputs = new LinkedHashMap<>();
        addMicronutrientInput(inputs, AppliedMicronutrient.B, "Boro", fertilityExtract.get().getBoro(),
                doseFor(micronutrientDose.get(), AppliedMicronutrient.B, interpretationFor(chemicalDiagnosis, "Boro")), chemicalDiagnosis, warnings);
        addMicronutrientInput(inputs, AppliedMicronutrient.Cu, "Cobre", fertilityExtract.get().getCobre(),
                doseFor(micronutrientDose.get(), AppliedMicronutrient.Cu, interpretationFor(chemicalDiagnosis, "Cobre")), chemicalDiagnosis, warnings);
        addMicronutrientInput(inputs, AppliedMicronutrient.Fe, "Ferro", fertilityExtract.get().getFerro(),
                doseFor(micronutrientDose.get(), AppliedMicronutrient.Fe, interpretationFor(chemicalDiagnosis, "Ferro")), chemicalDiagnosis, warnings);
        addMicronutrientInput(inputs, AppliedMicronutrient.Mn, "Manganês", fertilityExtract.get().getManganes(),
                doseFor(micronutrientDose.get(), AppliedMicronutrient.Mn, interpretationFor(chemicalDiagnosis, "Manganês")), chemicalDiagnosis, warnings);
        addMicronutrientInput(inputs, AppliedMicronutrient.Zn, "Zinco", fertilityExtract.get().getZinco(),
                doseFor(micronutrientDose.get(), AppliedMicronutrient.Zn, interpretationFor(chemicalDiagnosis, "Zinco")), chemicalDiagnosis, warnings);

        Map<AppliedMicronutrient, Double> requestedDoses = new LinkedHashMap<>();
        inputs.forEach((micronutrient, input) -> requestedDoses.put(micronutrient, input.doseKgHa()));
        Map<AppliedMicronutrient, Double> doses = plantingMicronutrientBalancesAfterCorrectiveFte(requestedDoses, correctiveFteCredits, warnings);
        if (doses.isEmpty()) {
            return List.of();
        }

        List<MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult> selections =
                micronutrientFertilizerSelectionService.select(user, sourceOption, doses);
        List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> rows = new ArrayList<>();
        for (MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection : selections) {
            MicronutrientRecommendationInput input = inputs.get(selection.micronutrient());
            if (input == null) continue;
            if (selection.technicalMessage() != null) {
                addWarning(warnings, selection.technicalMessage());
            }
            rows.add(toMicronutrientRow(selection, input, crop));
        }
        if (correctiveFteCredits == null || correctiveFteCredits.isEmpty()) {
            addFteBr12Row(rows, inputs, user, sourceOption, crop, warnings);
        }
        return rows;
    }

    private Map<AppliedMicronutrient, Double> correctiveFteMicronutrientCredits(
            List<RecommendationCalculationService.CorrectiveFertilizationRow> correctiveRows,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<String> warnings) {
        Map<AppliedMicronutrient, Double> credits = new LinkedHashMap<>();
        if (correctiveRows == null || correctiveRows.isEmpty()) {
            return credits;
        }
        List<SimpleMineralFertilizerModel> fertilizers = selectSimpleFertilizers(user, sourceOption);
        for (RecommendationCalculationService.CorrectiveFertilizationRow row : correctiveRows) {
            if (row == null || nvl(row.getDose()) <= 0d || !isFteBr12OrBr24(row.getSuggestedSource())) {
                continue;
            }
            SimpleMineralFertilizerModel fte = findFteByName(fertilizers, row.getSuggestedSource()).orElse(null);
            if (fte == null) {
                addWarning(warnings, "FTE corretivo " + row.getSuggestedSource()
                        + " consta na recomendação, mas seus teores cadastrados não foram localizados para abater micronutrientes do plantio.");
                continue;
            }
            addCorrectiveFteCredit(credits, AppliedMicronutrient.B, row.getDose(), fte.getB());
            addCorrectiveFteCredit(credits, AppliedMicronutrient.Cu, row.getDose(), fte.getCu());
            addCorrectiveFteCredit(credits, AppliedMicronutrient.Fe, row.getDose(), fte.getFe());
            addCorrectiveFteCredit(credits, AppliedMicronutrient.Mn, row.getDose(), fte.getMn());
            addCorrectiveFteCredit(credits, AppliedMicronutrient.Zn, row.getDose(), fte.getZn());
        }
        return credits;
    }

    private Optional<SimpleMineralFertilizerModel> findFteByName(List<SimpleMineralFertilizerModel> fertilizers, String sourceName) {
        String normalizedSource = normalizeText(sourceName);
        return fertilizers.stream()
                .filter(f -> normalizeText(f.getName()).equals(normalizedSource))
                .findFirst()
                .or(() -> fertilizers.stream()
                        .filter(f -> isFteBr12OrBr24(f.getName()) && fteType(f.getName()).equals(fteType(sourceName)))
                        .findFirst());
    }

    private void addCorrectiveFteCredit(Map<AppliedMicronutrient, Double> credits,
                                        AppliedMicronutrient micronutrient,
                                        Double productDoseKgHa,
                                        Double concentrationPercent) {
        double provided = nvl(productDoseKgHa) * nvl(concentrationPercent) / 100d;
        if (provided > 0d) {
            credits.merge(micronutrient, round2(provided), (current, added) -> round2(current + added));
        }
    }

    private Map<AppliedMicronutrient, Double> plantingMicronutrientBalancesAfterCorrectiveFte(
            Map<AppliedMicronutrient, Double> requestedDoses,
            Map<AppliedMicronutrient, Double> correctiveFteCredits,
            List<String> warnings) {
        if (requestedDoses == null || requestedDoses.isEmpty() || correctiveFteCredits == null || correctiveFteCredits.isEmpty()) {
            return requestedDoses;
        }
        Map<AppliedMicronutrient, Double> remaining = new LinkedHashMap<>();
        for (Map.Entry<AppliedMicronutrient, Double> entry : requestedDoses.entrySet()) {
            double requested = nvl(entry.getValue());
            double credit = nvl(correctiveFteCredits.get(entry.getKey()));
            double balance = round2(requested - credit);
            if (balance > 0d) {
                remaining.put(entry.getKey(), balance);
                addWarning(warnings, "Micronutriente " + entry.getKey().name() + " no plantio ajustado para complemento de "
                        + formatNumber(balance) + " kg/ha após crédito de FTE corretivo de " + formatNumber(credit) + " kg/ha.");
            } else if (requested > 0d && credit > 0d) {
                addWarning(warnings, "Micronutriente " + entry.getKey().name()
                        + " não foi recomendado no plantio porque o FTE corretivo supriu a dose calculada.");
            }
        }
        return remaining;
    }

    private boolean isFteBr12OrBr24(String value) {
        return FteProductEligibility.isHistoricalSupportedFte(value);
    }

    private String fteType(String value) {
        String normalized = normalizeText(value);
        if (normalized.contains("br 12") || normalized.contains("br-12")) return "BR-12";
        if (normalized.contains("br 24") || normalized.contains("br-24")) return "BR-24";
        return "";
    }

    private void addFteBr12Row(
            List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> rows,
            Map<AppliedMicronutrient, MicronutrientRecommendationInput> inputs,
            UserModel user,
            FertilizerSourceOption sourceOption,
            CropModel crop,
            List<String> warnings) {
        MicronutrientRecommendationInput boronInput = inputs.get(AppliedMicronutrient.B);
        AppliedMicronutrient baseNutrient = boronInput != null
                && ("Baixo".equals(boronInput.interpretation()) || "Muito baixo".equals(boronInput.interpretation()))
                && nvl(boronInput.doseKgHa()) > 0d
                ? AppliedMicronutrient.B
                : AppliedMicronutrient.Zn;
        MicronutrientRecommendationInput baseInput = inputs.get(baseNutrient);
        if (baseInput == null || nvl(baseInput.doseKgHa()) <= 0d) {
            return;
        }
        List<SimpleMineralFertilizerModel> fertilizers = selectSimpleFertilizers(user, sourceOption);
        SimpleMineralFertilizerModel fteBr12 = fertilizers.stream()
                .filter(f -> nvl(f.getZn()) > 0d && micronutrientConcentration(f, baseNutrient) > 0d)
                .filter(f -> FteProductEligibility.isBr12EligibleForNewRecommendation(f.getName()))
                .findFirst()
                .orElse(null);
        if (fteBr12 == null) {
            addWarning(warnings, "FTE BR 12 não encontrado com teores positivos de " + baseNutrient.name()
                    + " e Zn; nenhuma alternativa FTE foi selecionada.");
        } else {
            rows.add(toFteRow(fteBr12, baseNutrient, baseInput, crop));
        }
    }

    private RecommendationCalculationService.MicronutrientFertilizerRecommendationRow toFteRow(
            SimpleMineralFertilizerModel fertilizer,
            AppliedMicronutrient baseNutrient,
            MicronutrientRecommendationInput baseInput,
            CropModel crop) {
        double concentration = micronutrientConcentration(fertilizer, baseNutrient);
        FteDoseCalculator.FteDoseResult calculation = FteDoseCalculator.calculate(
                nvl(baseInput.doseKgHa()), concentration, nvl(fertilizer.getZn()),
                RecommendationCalculationService.MAX_ZINC_FROM_FTE_KG_HA);
        double dose = calculation.productDoseKgHa();
        boolean limitedByZinc = calculation.limitedByZinc();
        CropSpacingCalculationService.CropSpacingDoseResult spacingDose =
                cropSpacingCalculationService.calculate(crop, dose);
        CropSpacingCalculationService.DoseUnitMetadata doseUnitMetadata =
                cropSpacingCalculationService.resolveDoseUnitMetadata(spacingDose);
        String balance = String.format(Locale.US,
                " Balanço fornecido pelo FTE: B %.2f, Cu %.2f, Fe %.2f, Mn %.2f, Mo %.2f, Zn %.2f kg/ha; saldo de Zn %.2f kg/ha.",
                dose * nvl(fertilizer.getB()) / 100d,
                dose * nvl(fertilizer.getCu()) / 100d,
                dose * nvl(fertilizer.getFe()) / 100d,
                dose * nvl(fertilizer.getMn()) / 100d,
                dose * nvl(fertilizer.getMo()) / 100d,
                dose * nvl(fertilizer.getZn()) / 100d,
                dose * nvl(fertilizer.getZn()) / 100d - (baseNutrient == AppliedMicronutrient.Zn ? nvl(baseInput.doseKgHa()) : 0d));
        return RecommendationCalculationService.MicronutrientFertilizerRecommendationRow.builder()
                .micronutrient(baseNutrient)
                .micronutrientDoseKgHa(round2(nvl(baseInput.doseKgHa())))
                .fertilizerId(fertilizer.getId())
                .fertilizerName(fertilizer.getName())
                .micronutrientConcentrationPercent(round2(concentration))
                .fertilizerDoseKgHa(dose)
                .doseUnitMode(doseUnitMetadata.doseUnitMode())
                .doseUnitLabel(doseUnitMetadata.doseUnitLabel())
                .gramsPerLinearMeter(spacingDose.gramsPerLinearMeter())
                .gramsPerPit(spacingDose.gramsPerPit())
                .technicalObservation("FTE BR 12 calculado por " + baseNutrient.name()
                        + ". Dose teórica = 100 * dose recomendada / teor % do nutriente-base; teto por Zn = 100 * 7,50 / %Zn. "
                        + (limitedByZinc ? "Dose limitada pelo teto de Zn." : "Dose teórica mantida.") + balance
                        + " Misturar e aplicar conjuntamente com os adubos simples ou formulados para correção da fertilidade do solo.")
                .build();
    }

    private double micronutrientConcentration(SimpleMineralFertilizerModel fertilizer,
                                               AppliedMicronutrient micronutrient) {
        if (fertilizer == null || micronutrient == null) return 0d;
        return switch (micronutrient) {
            case B -> nvl(fertilizer.getB());
            case Cu -> nvl(fertilizer.getCu());
            case Fe -> nvl(fertilizer.getFe());
            case Mn -> nvl(fertilizer.getMn());
            case Zn -> nvl(fertilizer.getZn());
            default -> 0d;
        };
    }

    private void addMicronutrientInput(
            Map<AppliedMicronutrient, MicronutrientRecommendationInput> inputs,
            AppliedMicronutrient micronutrient,
            String label,
            Double analyzedValue,
            Double doseKgHa,
            List<RecommendationCalculationService.SoilChemicalDiagnosisItem> chemicalDiagnosis,
            List<String> warnings) {
        if (analyzedValue == null) {
            return;
        }
        String interpretation = interpretationFor(chemicalDiagnosis, label);
        if (interpretation == null) {
            addWarning(warnings, "Dose de " + label + " não calculada porque o teor analisado não foi classificado pela tabela de teores diversos.");
            return;
        }
        if (doseKgHa == null) {
            addWarning(warnings, "Dose de " + label + " não calculada porque a classe " + interpretation
                    + " não possui dose preenchida na tabela auxiliar Doses de Micronutrientes.");
            return;
        }
        inputs.put(micronutrient, new MicronutrientRecommendationInput(label, analyzedValue, interpretation, doseKgHa));
    }

    private RecommendationCalculationService.MicronutrientFertilizerRecommendationRow toMicronutrientRow(
            MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection,
            MicronutrientRecommendationInput input,
            CropModel crop) {
        CropSpacingCalculationService.CropSpacingDoseResult spacingDose =
                cropSpacingCalculationService.calculate(crop, selection.fertilizerDoseKgHa());
        CropSpacingCalculationService.DoseUnitMetadata doseUnitMetadata =
                cropSpacingCalculationService.resolveDoseUnitMetadata(spacingDose);
        SimpleMineralFertilizerModel fertilizer = selection.selectedFertilizer();
        return RecommendationCalculationService.MicronutrientFertilizerRecommendationRow.builder()
                .micronutrient(selection.micronutrient())
                .micronutrientDoseKgHa(selection.micronutrientDoseKgHa())
                .fertilizerId(fertilizer != null ? fertilizer.getId() : null)
                .fertilizerName(fertilizer != null ? fertilizer.getName() : null)
                .micronutrientConcentrationPercent(selection.selectedConcentrationPercent())
                .fertilizerDoseKgHa(selection.fertilizerDoseKgHa())
                .doseUnitMode(doseUnitMetadata.doseUnitMode())
                .doseUnitLabel(doseUnitMetadata.doseUnitLabel())
                .gramsPerLinearMeter(spacingDose.gramsPerLinearMeter())
                .gramsPerPit(spacingDose.gramsPerPit())
                .technicalObservation(buildMicronutrientObservation(selection, input, spacingDose))
                .build();
    }

    private String buildMicronutrientObservation(
            MicronutrientFertilizerSelectionService.MicronutrientFertilizerSelectionResult selection,
            MicronutrientRecommendationInput input,
            CropSpacingCalculationService.CropSpacingDoseResult spacingDose) {
        String base = String.format(Locale.US,
                "Dose selecionada pela faixa %s do teor de %s (%.2f mg/dm³). Dose do produto = 100 * %.2f / %.2f.",
                input.interpretation(), input.label(), input.analyzedValue(), nvl(selection.micronutrientDoseKgHa()),
                nvl(selection.selectedConcentrationPercent()));
        String source = selection.selectedFertilizer() != null
                ? " Fonte mineral simples selecionada pelo maior teor cadastrado de " + selection.micronutrient() + "."
                : " Fonte mineral simples não selecionada.";
        String spacing = spacingDose.technicalWarning() != null
                ? " Conversão por espaçamento não calculada: " + spacingDose.technicalWarning()
                : " Conversão por espaçamento calculada conforme cadastro da cultura.";
        String technicalMessage = selection.technicalMessage() != null ? " " + selection.technicalMessage() : "";
        return base + source + spacing + technicalMessage;
    }

    private String interpretationFor(
            List<RecommendationCalculationService.SoilChemicalDiagnosisItem> chemicalDiagnosis,
            String label) {
        if (chemicalDiagnosis == null) return null;
        String normalizedLabel = normalizeText(label);
        return chemicalDiagnosis.stream()
                .filter(item -> item != null && normalizeText(item.getAttribute()).equals(normalizedLabel))
                .map(RecommendationCalculationService.SoilChemicalDiagnosisItem::getInterpretation)
                .filter(interpretation -> interpretation != null && !interpretation.isBlank())
                .findFirst()
                .orElse(null);
    }

    private Double doseFor(MicronutrientDoseModel doses, AppliedMicronutrient micronutrient, String interpretation) {
        if (doses == null || micronutrient == null || interpretation == null) return null;
        return switch (micronutrient) {
            case B -> switch (interpretation) {
                case "Baixo" -> doses.getBoronLowDose();
                case "Médio" -> doses.getBoronMediumDose();
                case "Alto" -> doses.getBoronHighDose();
                default -> null;
            };
            case Cu -> switch (interpretation) {
                case "Baixo" -> doses.getCopperLowDose();
                case "Médio" -> doses.getCopperMediumDose();
                case "Alto" -> doses.getCopperHighDose();
                default -> null;
            };
            case Fe -> switch (interpretation) {
                case "Baixo" -> doses.getIronLowDose();
                case "Médio" -> doses.getIronMediumDose();
                case "Alto" -> doses.getIronHighDose();
                default -> null;
            };
            case Mn -> switch (interpretation) {
                case "Baixo" -> doses.getManganeseLowDose();
                case "Médio" -> doses.getManganeseMediumDose();
                case "Alto" -> doses.getManganeseHighDose();
                default -> null;
            };
            case Zn -> switch (interpretation) {
                case "Baixo" -> doses.getZincLowDose();
                case "Médio" -> doses.getZincMediumDose();
                case "Alto" -> doses.getZincHighDose();
                default -> null;
            };
            default -> null;
        };
    }

    private void addWarning(List<String> warnings, String warning) {
        if (warnings != null && warning != null && !warning.isBlank()) {
            warnings.add(warning);
        }
    }

    SimpleMineralFertilizerModel selectCorrectiveSource(UserModel user, FertilizerSourceOption sourceOption, String nutrientTarget) {
        List<SimpleMineralFertilizerModel> fertilizers = selectSimpleFertilizers(user, sourceOption);
        if (fertilizers == null) return null;
        return fertilizers.stream()
                .filter(f -> correctiveSourcePercentage(f, nutrientTarget) > 0d)
                .max(Comparator.comparing((SimpleMineralFertilizerModel f) -> correctiveSourcePercentage(f, nutrientTarget))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()))
                .orElse(null);
    }

    private double correctiveSourcePercentage(SimpleMineralFertilizerModel fertilizer, String nutrientTarget) {
        if (fertilizer == null || nutrientTarget == null) return 0d;
        return switch (nutrientTarget) {
            case "P2O5" -> nvl(fertilizer.getP2O5());
            case "K2O" -> nvl(fertilizer.getK2O());
            case "S" -> nvl(fertilizer.getS());
            default -> 0d;
        };
    }

    private Optional<ContentRangeModel> selectNitrogenRange(CropFertilizationTableModel t) {
        var l = contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(t, Nutriente.NITROGENIO);
        return l.stream().findFirst();
    }

    private Optional<ContentRangeModel> selectNutrientRange(CropFertilizationTableModel t, Nutriente n, Optional<Double> value, List<String> w, String label) {
        var ranges = contentRangeRepository.findAllByTableAndNutrientOrderByOrderAsc(t, n);
        if (ranges.isEmpty()) return Optional.empty();
        if (value.isEmpty()) {
            w.add("Não foi possível classificar teor de " + label + "; primeiro intervalo da tabela foi utilizado.");
            return Optional.of(ranges.get(0));
        }
        double v = value.get();
        return ranges.stream().filter(r -> (r.getSmallest() == null || v >= r.getSmallest()) && (r.getLargest() == null || v < r.getLargest())).findFirst().or(() -> Optional.of(ranges.get(0)));
    }

    private Optional<Double> extractPhosphorusValue(Optional<FertilityAnalysisExtractModel> e) {
        return e.map(x -> x.getFosforoMehlich1() != null ? x.getFosforoMehlich1() : x.getFosforoResina());
    }

    private Optional<Double> extractPotassiumValue(Optional<FertilityAnalysisExtractModel> e) {
        return e.map(FertilityAnalysisExtractModel::getPotassio);
    }

    private FertilizerSelection selectBestPlantingFertilizer(UserModel user, FertilizerSourceOption sourceOption, Double n, Double p, Double k, List<String> w) {
        FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionResult formulatedSelection =
                formulatedFertilizerSelectionService.selectCandidates(user, sourceOption, n, p, k);
        if (formulatedSelection.technicalMessage() != null) {
            w.add(formulatedSelection.technicalMessage());
        }
        if (FormulatedFertilizerSelectionService.hasSinglePositiveNutrient(n, p, k)) {
            return new FertilizerSelection(
                    "Adubos formulados NPK não recomendados", null,
                    0d, 0d, 0d, 0d,
                    -nvl(n), -nvl(p), -nvl(k), null,
                    null, null, null,
                    FormulatedFertilizerSelectionService.SINGLE_NUTRIENT_PLANTING_MESSAGE,
                    Optional.empty());
        }
        if (formulatedSelection.candidates() != null && !formulatedSelection.candidates().isEmpty()) {
            FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate selected =
                    formulatedSelection.candidates().get(0);
            return buildFormulatedSelection(selected, n, p, k, w);
        }

        var simples = selectSimpleFertilizers(user, sourceOption);
        var bestS = simples.stream().filter(f -> f.getN() > 0 || f.getP2O5() > 0 || f.getK2O() > 0).max((a, b) -> compareScore(a.getN(), a.getP2O5(), a.getK2O(), b.getN(), b.getP2O5(), b.getK2O(), n, p, k, a.getId(), b.getId()));
        if (bestS.isPresent()) {
            var f = bestS.get();
            Optional<FertilizerDoseCalculation> calc = calculateByGreatestFactor(n, p, k, f.getN(), f.getP2O5(), f.getK2O(), "concentração do nutriente alvo em fertilizante simples");
            var s = RecommendationCalculationService.FertilizerSuggestion.builder().fertilizerId(f.getId()).fertilizerType("SIMPLES").fertilizerName(f.getName()).n(f.getN()).p2o5(f.getP2O5()).k2o(f.getK2O()).s(f.getS()).reason("Fallback por ausência de formulado adequado; dose calculada pelo nutriente alvo identificado.").build();
            if (calc.isEmpty()) {
                String warning = "Fertilizante simples selecionado, mas sem nutriente alvo com necessidade e concentração válidas para calcular dose comercial.";
                w.add(warning);
                return buildSelection(s.getFertilizerName(), (Double) null, n, p, k, f.getN(), f.getP2O5(), f.getK2O(), null, w, warning, Optional.of(s));
            }
            w.add("Quantidade de adubo simples calculada pela concentração do nutriente alvo identificado.");
            return buildSelection(s.getFertilizerName(), calc.get(), n, p, k, f.getN(), f.getP2O5(), f.getK2O(), w, null, Optional.of(s));
        }
        w.add("Nenhum adubo mineral adequado foi encontrado para a origem de adubos selecionada.");
        return new FertilizerSelection("Não encontrado", null, 0d, 0d, 0d, 0d, null, null, null, null, null, null, null, null, Optional.empty());
    }

    private FertilizerSelection buildFormulatedSelection(
            FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate selected,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O,
            List<String> warnings) {
        FormulatedMineralFertilizerModel fertilizer = selected.formulated();
        String warning = null;
        if (nvl(selected.balanceN()) < 0 || nvl(selected.balanceP2O5()) < 0 || nvl(selected.balanceK2O()) < 0) {
            warning = buildFormulatedPlantingDeficitWarning(selected);
            warnings.add(warning);
        }

        RecommendationCalculationService.FertilizerSuggestion suggestion =
                RecommendationCalculationService.FertilizerSuggestion.builder()
                        .fertilizerId(fertilizer != null ? fertilizer.getId() : null)
                        .fertilizerType("FORMULADO")
                        .fertilizerName(formatFormulatedFertilizerName(fertilizer))
                        .n(fertilizer != null ? fertilizer.getN() : null)
                        .p2o5(fertilizer != null ? fertilizer.getP2O5() : null)
                        .k2o(fertilizer != null ? fertilizer.getK2O() : null)
                        .s(fertilizer != null ? fertilizer.getS() : null)
                        .reason("Seleção de formulado NPK por " + formulatedSelectionType(selected) + ".")
                        .build();

        double providedS = round2(nvl(selected.fertilizerDoseKgHa()) * nvl(fertilizer != null ? fertilizer.getS() : null) / 100d);
        return new FertilizerSelection(
                suggestion.getFertilizerName(),
                round2(nvl(selected.fertilizerDoseKgHa())),
                round2(nvl(selected.providedN())),
                round2(nvl(selected.providedP2O5())),
                round2(nvl(selected.providedK2O())),
                providedS,
                selected.balanceN(),
                selected.balanceP2O5(),
                selected.balanceK2O(),
                selected.limitingNutrient(),
                selected.maximizationFallback() ? targetNeedForLimitingNutrient(selected.limitingNutrient(), requiredN, requiredP2O5, requiredK2O) : null,
                selected.concentrationSum(),
                buildFormulatedCalculationMemory(selected),
                warning,
                Optional.of(suggestion));
    }

    private String buildFormulatedPlantingDeficitWarning(
            FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate selected) {
        List<String> coverageDeficits = new ArrayList<>();
        if (nvl(selected.deficitN()) > 0d) {
            coverageDeficits.add(String.format(Locale.US, "N %.2f kg/ha", nvl(selected.deficitN())));
        }
        if (nvl(selected.deficitK2O()) > 0d) {
            coverageDeficits.add(String.format(Locale.US, "K2O %.2f kg/ha", nvl(selected.deficitK2O())));
        }

        List<String> observations = new ArrayList<>();
        if (!coverageDeficits.isEmpty()) {
            observations.add("Déficits de " + String.join(" e ", coverageDeficits) + " serão repassados para cobertura");
        }
        if (nvl(selected.deficitP2O5()) > 0d) {
            observations.add(String.format(Locale.US,
                    "déficit de P2O5 %.2f kg/ha exige ajuste técnico no plantio",
                    nvl(selected.deficitP2O5())));
        }

        String detail = observations.isEmpty()
                ? "Saldos negativos identificados sem déficit positivo após arredondamento; revisar memória de cálculo"
                : String.join("; ", observations);
        return "Fertilizante formulado selecionado não atende todos os nutrientes no plantio. " + detail + ".";
    }

    private String formatFormulatedFertilizerName(FormulatedMineralFertilizerModel fertilizer) {
        if (fertilizer == null) {
            return "NPK formulado";
        }
        return String.format(Locale.US, "NPK %.2f-%.2f-%.2f", fertilizer.getN(), fertilizer.getP2O5(), fertilizer.getK2O());
    }

    private String formulatedSelectionType(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate selected) {
        if (selected.maximizationFallback()) {
            return "maximização";
        }
        return selected.approximateFallback() ? "relação aproximada" : "relação equivalente";
    }

    private Double targetNeedForLimitingNutrient(String nutrient, Double requiredN, Double requiredP2O5, Double requiredK2O) {
        if ("N".equals(nutrient)) return round2(nvl(requiredN));
        if ("P2O5".equals(nutrient)) return round2(nvl(requiredP2O5));
        if ("K2O".equals(nutrient)) return round2(nvl(requiredK2O));
        return null;
    }

    private String buildFormulatedCalculationMemory(FormulatedFertilizerSelectionService.FormulatedFertilizerSelectionCandidate selected) {
        return String.format(Locale.US,
                "Estratégia de seleção: %s; relação do formulado: %.2f-%.2f-%.2f; soma de concentrações NPK: %.2f%%; dose calculada: %.2f kg/ha; cobertura estimada: %.2f%%; fornecido: N %.2f, P2O5 %.2f, K2O %.2f kg/ha; déficit/excedente: N %+.2f, P2O5 %+.2f, K2O %+.2f kg/ha.",
                formulatedSelectionType(selected),
                selected.relation() != null ? selected.relation().getN() : 0d,
                selected.relation() != null ? selected.relation().getP() : 0d,
                selected.relation() != null ? selected.relation().getK() : 0d,
                nvl(selected.concentrationSum()),
                nvl(selected.fertilizerDoseKgHa()),
                nvl(selected.coveragePercent()),
                nvl(selected.providedN()),
                nvl(selected.providedP2O5()),
                nvl(selected.providedK2O()),
                nvl(selected.balanceN()),
                nvl(selected.balanceP2O5()),
                nvl(selected.balanceK2O()));
    }

    private FertilizerSelection buildSelection(String name, FertilizerDoseCalculation calc, Double rn, Double rp, Double rk, double fn, double fp, double fk, List<String> warnings, String warning, Optional<RecommendationCalculationService.FertilizerSuggestion> suggestion) {
        Double q = calc != null ? calc.quantityKgHa() : null;
        return buildSelection(name, q, rn, rp, rk, fn, fp, fk, calc, warnings, warning, suggestion);
    }

    private FertilizerSelection buildSelection(String name, Double q, Double rn, Double rp, Double rk, double fn, double fp, double fk, FertilizerDoseCalculation calc, List<String> warnings, String warning, Optional<RecommendationCalculationService.FertilizerSuggestion> suggestion) {
        double pn = q == null ? 0d : round2(q * fn / 100d), pp = q == null ? 0d : round2(q * fp / 100d), pk = q == null ? 0d : round2(q * fk / 100d);
        double bn = round2(pn - nvl(rn)), bp = round2(pp - nvl(rp)), bk = round2(pk - nvl(rk));
        String effectiveWarning = warning;
        if (effectiveWarning == null && (bn < 0 || bp < 0 || bk < 0)) {
            effectiveWarning = String.format("Fertilizante selecionado não atende todos os nutrientes no plantio. Déficits: N %.2f kg/ha, P2O5 %.2f kg/ha, K2O %.2f kg/ha.", Math.max(0d, -bn), Math.max(0d, -bp), Math.max(0d, -bk));
            warnings.add(effectiveWarning);
        }
        String memory = calc == null ? null : buildCalculationMemory(calc, pn, pp, pk, bn, bp, bk);
        return new FertilizerSelection(name, q, pn, pp, pk, 0d, bn, bp, bk, calc != null ? calc.nutrient() : null, calc != null ? calc.targetNeedKgHa() : null, calc != null ? calc.concentrationPercent() : null, memory, effectiveWarning, suggestion);
    }

    private Optional<FertilizerDoseCalculation> calculateByGreatestFactor(Double rn, Double rp, Double rk, double n, double p, double k, String method) {
        List<FertilizerDoseCalculation> calculations = new ArrayList<>();
        addDoseCandidate(calculations, "N", rn, n, method);
        addDoseCandidate(calculations, "P2O5", rp, p, method);
        addDoseCandidate(calculations, "K2O", rk, k, method);
        return calculations.stream().max(Comparator.comparing(FertilizerDoseCalculation::quantityKgHa));
    }

    private void addDoseCandidate(List<FertilizerDoseCalculation> calculations, String nutrient, Double required, double concentration, String method) {
        if (nvl(required) <= 0d || concentration <= 0d) return;
        calculations.add(new FertilizerDoseCalculation(nutrient, round2(required), round2(concentration), round2(required / concentration * 100d), method));
    }

    private String buildCalculationMemory(FertilizerDoseCalculation calc, double providedN, double providedP2O5, double providedK2O, double balanceN, double balanceP2O5, double balanceK2O) {
        return String.format(Locale.US,
                "Nutriente limitante/alvo: %s; necessidade alvo: %.2f kg/ha; concentração do produto: %.2f%%; dose calculada: %.2f kg/ha (%s); fornecido: N %.2f, P2O5 %.2f, K2O %.2f kg/ha; déficit/excedente: N %+.2f, P2O5 %+.2f, K2O %+.2f kg/ha.",
                calc.nutrient(), calc.targetNeedKgHa(), calc.concentrationPercent(), calc.quantityKgHa(), calc.method(), providedN, providedP2O5, providedK2O, balanceN, balanceP2O5, balanceK2O);
    }

    private CoverageDemand buildCoverageDemand(
            List<ContentRangeModel> selectedRanges,
            NutrientBalanceAccumulator balance,
            List<String> warnings) {
        CoverageDemand demand = new CoverageDemand();
        for (ContentRangeModel range : selectedRanges) {
            if (range == null) continue;
            Nutriente nutrient = range.getNutrient();
            List<CoverageModel> coverages = coverageRepository.findAllByRangeOrderByOrderAsc(range);
            for (CoverageModel coverage : coverages) {
                if (coverage == null || coverage.getApplication() == null) continue;
                double application = round2(Math.max(0d, coverage.getApplication()));
                if (nutrient == Nutriente.FOSFORO) {
                    if (application > 0d) {
                        addWarning(warnings, "P2O5 cadastrado em cobertura foi ignorado para cultura anual; todo P2O5 deve permanecer no plantio.");
                    }
                    continue;
                }
                demand.add(coverage.getOrder(), nutrient, application);
            }
        }
        double sulfurDeficit = Math.max(0d, -balance.balanceS());
        double coverageSulfur = capCoverageSulfur(sulfurDeficit);
        if (sulfurDeficit > MAX_COVERAGE_S_KG_HA) {
            addWarning(warnings, String.format(Locale.US,
                    "O saldo de S transferido é %.2f kg/ha; a cobertura consolidada foi limitada a %.2f kg/ha de S e %.2f kg/ha permanecem pendentes.",
                    sulfurDeficit, MAX_COVERAGE_S_KG_HA, sulfurDeficit - MAX_COVERAGE_S_KG_HA));
        }
        demand.addPlantingDeficits(
                Math.max(0d, -balance.balanceN()),
                Math.max(0d, -balance.balanceK2O()),
                coverageSulfur);
        return demand;
    }

    private List<RecommendationCalculationService.FertilizationRecommendationRow> buildCoverageOptionOneRows(
            CoverageDemand demand,
            List<RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow> formulatedRows,
            CropModel crop,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<RecommendationCalculationService.FertilizerSuggestion> suggestions,
            List<String> warnings) {
        if (demand.isEmpty()) {
            return List.of();
        }
        List<RecommendationCalculationService.FertilizationRecommendationRow> rows = new ArrayList<>();
        RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow formulated =
                formulatedRows == null || formulatedRows.isEmpty() ? null : formulatedRows.get(0);
        double providedN = 0d;
        double providedP2O5 = 0d;
        double providedK2O = 0d;
        double providedS = 0d;
        if (formulated != null && formulated.getDoseKgHa() != null) {
            providedN = round2(nvl(formulated.getProvidedN()));
            providedP2O5 = round2(nvl(formulated.getProvidedP2O5()));
            providedK2O = round2(nvl(formulated.getProvidedK2O()));
            providedS = round2(nvl(formulated.getProvidedS()));
            rows.add(RecommendationCalculationService.FertilizationRecommendationRow.builder()
                    .phase("Opção 1 - Cobertura com formulado")
                    .nutrients(demand.nutrientsLabel())
                    .suggestedFertilizer(formulated.getFertilizerName())
                    .fertilizerQuantityKgHa(formulated.getDoseKgHa())
                    .applicationMode(buildConsolidatedCoverageApplicationMode(crop))
                    .source("Cobertura consolidada com relação N-00-K2O; P2O5 em cobertura não recomendado para cultura anual.")
                    .providedN(providedN)
                    .providedP2O5(providedP2O5)
                    .providedK2O(providedK2O)
                    .providedS(providedS)
                    .balanceN(round2(providedN - demand.totalN()))
                    .balanceP2O5(providedP2O5)
                    .balanceK2O(round2(providedK2O - demand.totalK2O()))
                    .balanceS(round2(providedS - demand.totalS()))
                    .limitingNutrient(formulated.getLimitingNutrient())
                    .targetNeedKgHa(round2(demand.totalN() + demand.totalK2O()))
                    .productConcentrationPercent(round2(nvl(formulated.getNitrogenPercent()) + nvl(formulated.getK2oPercent())))
                    .calculationMemory("Dose calculada por formulado N-00-K2O; déficits de plantio incorporados à cobertura.")
                    .warning(formulated.getTechnicalObservation())
                    .build());
            suggestions.add(RecommendationCalculationService.FertilizerSuggestion.builder()
                    .fertilizerId(formulated.getFertilizerId())
                    .fertilizerType("FORMULADO")
                    .fertilizerName(formulated.getFertilizerName())
                    .n(formulated.getNitrogenPercent())
                    .p2o5(formulated.getP2o5Percent())
                    .k2o(formulated.getK2oPercent())
                    .s(null)
                    .reason("Opção 1 - cobertura com formulado N-00-K2O, usando saldos negativos de plantio.")
                    .build());
        } else {
            addWarning(warnings, "Opção 1 de cobertura com formulado não encontrou formulado N-00-K2O compatível; os saldos devem ser fechados por adubos simples.");
        }

        CoverageDemand remaining = demand.minusProvided(providedN, providedK2O, providedS);
        rows.addAll(buildSimpleCoveragePackageRows(
                "Opção 1 - Complemento de cobertura",
                remaining,
                crop,
                user,
                sourceOption,
                suggestions,
                null,
                warnings,
                "Complemento com adubos simples após formulado de cobertura."));
        return rows;
    }

    private List<RecommendationCalculationService.FertilizationRecommendationRow> buildCoverageOptionTwoRows(
            CoverageDemand demand,
            CropModel crop,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<RecommendationCalculationService.FertilizerSuggestion> suggestions,
            NutrientBalanceAccumulator balance,
            List<String> warnings) {
        if (demand.isEmpty()) {
            return List.of();
        }
        return buildSimpleCoveragePackageRows(
                "Opção 2 - Cobertura com adubos simples",
                demand,
                crop,
                user,
                sourceOption,
                suggestions,
                balance,
                warnings,
                "Cobertura com ureia, cloreto de potássio e fonte simples de S quando houver saldo pendente.");
    }

    private List<RecommendationCalculationService.FertilizationRecommendationRow> buildSimpleCoveragePackageRows(
            String phasePrefix,
            CoverageDemand demand,
            CropModel crop,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<RecommendationCalculationService.FertilizerSuggestion> suggestions,
            NutrientBalanceAccumulator balance,
            List<String> warnings,
            String source) {
        List<RecommendationCalculationService.FertilizationRecommendationRow> rows = new ArrayList<>();
        if (demand.isEmpty()) {
            return rows;
        }
        List<SimpleMineralFertilizerModel> simples = selectSimpleFertilizers(user, sourceOption);
        SimpleCoveragePackage pkg = new SimpleCoveragePackage(demand);

        double remainingN = demand.totalN();
        if (demand.totalS() > 0d) {
            SimpleMineralFertilizerModel sulfur = selectNamedOrBest(simples, "sulfato de amonio", f -> nvl(f.getS()) > 0d && nvl(f.getN()) > 0d,
                    Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getS())).thenComparing(f -> nvl(f.getN())));
            if (sulfur != null) {
                double doseByS = 100d * demand.totalS() / nvl(sulfur.getS());
                double suppliedN = doseByS * nvl(sulfur.getN()) / 100d;
                if (suppliedN > demand.totalN() + tolerance(demand.totalN())) {
                    addWarning(warnings, phasePrefix + ": sulfato de amônio por S excederia a tolerância de N; a fonte não foi forçada.");
                    sulfur = null;
                }
            }
            if (sulfur == null) {
                sulfur = selectNamedOrBest(simples, "sulfato", f -> nvl(f.getS()) > 0d && nvl(f.getN()) <= 0d,
                        Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getS())));
            }
            if (sulfur != null && sulfur.getS() > 0d) {
                double dose = round2(100d * demand.totalS() / sulfur.getS());
                pkg.add(sulfur, dose, phasePrefix + " - S", "S", "Dose de fonte de S = 100 * S pendente / %S cadastrado.");
                remainingN = Math.max(0d, round2(demand.totalN() - dose * nvl(sulfur.getN()) / 100d));
                addSuggestion(suggestions, sulfur, "SIMPLES", phasePrefix + " - fonte de S em cobertura.");
            } else {
                addWarning(warnings, phasePrefix + ": não foi encontrada fonte simples cadastrada com S para fechar o saldo de enxofre.");
            }
        }

        if (remainingN > tolerance(demand.totalN())) {
            SimpleMineralFertilizerModel urea = selectNamedOrBest(simples, "ureia", f -> nvl(f.getN()) > 0d,
                    Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getN())));
            if (urea != null && urea.getN() > 0d) {
                double dose = round2(100d * remainingN / urea.getN());
                pkg.add(urea, dose, phasePrefix + " - N", "N", "Dose de ureia = 100 * N remanescente / %N cadastrado.");
                addSuggestion(suggestions, urea, "SIMPLES", phasePrefix + " - N em cobertura.");
            } else {
                addWarning(warnings, phasePrefix + ": não foi encontrada ureia ou fonte simples cadastrada com N para cobertura.");
            }
        }

        if (demand.totalK2O() > 0d) {
            SimpleMineralFertilizerModel kcl = selectNamedOrBest(simples, "cloreto de potassio", f -> nvl(f.getK2O()) > 0d,
                    Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getK2O())));
            if (kcl != null && kcl.getK2O() > 0d) {
                double dose = round2(100d * demand.totalK2O() / kcl.getK2O());
                pkg.add(kcl, dose, phasePrefix + " - K2O", "K2O", "Dose de KCl = 100 * K2O recomendado / %K2O cadastrado.");
                addSuggestion(suggestions, kcl, "SIMPLES", phasePrefix + " - K2O em cobertura.");
            } else {
                addWarning(warnings, phasePrefix + ": não foi encontrado cloreto de potássio ou fonte simples cadastrada com K2O para cobertura.");
            }
        }

        if (!pkg.withinTolerance()) {
            addWarning(warnings, String.format(Locale.US,
                    "%s não fechou dentro da tolerância de 10%%. Saldos: N %+.2f, K2O %+.2f, S %+.2f kg/ha.",
                    phasePrefix, pkg.balanceN(), pkg.balanceK2O(), pkg.balanceS()));
        }
        if (balance != null) {
            balance.addCoverageTotals(demand.recommendedN(), demand.recommendedK2O(), demand.plantingDeficitN(), demand.plantingDeficitK2O(),
                    demand.totalS(), pkg.providedN(), 0d, pkg.providedK2O(), pkg.providedS());
        }
        rows.addAll(pkg.toRows(crop, source));
        return rows;
    }

    private SimplePlantingPackageResult buildSimplePlantingPackage(
            SulfurPlantingRequirement sulfurRequirement,
            boolean effectiveGypsumRecommendation,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O,
            CropModel crop,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<String> warnings) {
        List<SimpleMineralFertilizerModel> fertilizers = selectSimpleFertilizers(user, sourceOption);
        SimplePlantingPackage best = effectiveGypsumRecommendation
                ? buildSimplePlantingPackageWithoutSulfurLimitation(fertilizers, requiredN, requiredP2O5, requiredK2O)
                : chooseBestSimplePlantingPackage(
                buildSimplePlantingPackageByNitrogenFirst(fertilizers, sulfurRequirement, requiredN, requiredP2O5, requiredK2O),
                buildSimplePlantingPackageByPhosphorusSulfurFirst(fertilizers, sulfurRequirement, requiredN, requiredP2O5, requiredK2O),
                nvl(requiredN), nvl(requiredP2O5), nvl(requiredK2O), sulfurRequirement.requiredS());

        if (best == null || best.rows.isEmpty()) {
            addWarning(warnings, "Opção 2 - Plantio com adubos simples não calculada: fontes cadastradas insuficientes para N, P2O5, K2O, S e micronutrientes.");
            return new SimplePlantingPackageResult(List.of(), List.of(), 0d, 0d, 0d, 0d, 0d, 0d);
        }

        if (!withinTolerance(best.n, nvl(requiredN)) || !withinTolerance(best.p2o5, nvl(requiredP2O5))
                || !withinTolerance(best.k2o, nvl(requiredK2O))
                || (!effectiveGypsumRecommendation && !withinTolerance(best.s, sulfurRequirement.requiredS()))) {
            String warning = effectiveGypsumRecommendation
                    ? String.format(Locale.US,
                    "Opção 2 - Plantio com adubos simples não fechou dentro da tolerância de 10%%. Saldos: N %+.2f, P2O5 %+.2f, K2O %+.2f kg/ha. S não é limitante devido à gessagem.",
                    best.n - nvl(requiredN), best.p2o5 - nvl(requiredP2O5), best.k2o - nvl(requiredK2O))
                    : String.format(Locale.US,
                    "Opção 2 - Plantio com adubos simples não fechou dentro da tolerância de 10%%. Saldos: N %+.2f, P2O5 %+.2f, K2O %+.2f, S %+.2f kg/ha.",
                    best.n - nvl(requiredN), best.p2o5 - nvl(requiredP2O5), best.k2o - nvl(requiredK2O), best.s - sulfurRequirement.requiredS());
            addWarning(warnings, warning);
            best.warning = warning;
        }

        double transferredN = Math.max(0d, round2(nvl(requiredN) - best.n));
        List<RecommendationCalculationService.FertilizationRecommendationRow> rows = best.toRows(
                crop, nvl(requiredN), nvl(requiredP2O5), nvl(requiredK2O), sulfurRequirement.requiredS(), transferredN);
        return new SimplePlantingPackageResult(
                rows, best.suggestions, transferredN, 0d,
                best.n, best.p2o5, best.k2o, best.s);
    }

    private SimplePlantingPackage buildSimplePlantingPackageWithoutSulfurLimitation(
            List<SimpleMineralFertilizerModel> fertilizers,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O) {
        SimplePlantingPackage pkg = new SimplePlantingPackage("prioridade econômica sem S limitante após gessagem");
        List<SimpleMineralFertilizerModel> nitrogenSourcesWithoutSulfur = fertilizers.stream()
                .filter(NutrientFertilizationCalculationService::isNitrogenSourceEligibleAfterGypsum)
                .toList();
        List<SimpleMineralFertilizerModel> phosphorusSourcesWithoutSulfur = fertilizers.stream()
                .filter(fertilizer -> nvl(fertilizer.getP2O5()) > 0d && nvl(fertilizer.getS()) <= 0d)
                .filter(NutrientFertilizationCalculationService::isPreferredPhosphorusSourceAfterGypsum)
                .toList();
        SimpleMineralFertilizerModel nitrogen = fertilizerOpportunityCostService.selectLowestCostSimpleSource(
                nitrogenSourcesWithoutSulfur, FertilizerOpportunityCostService.Nutrient.N).orElseGet(() ->
                namedSource(nitrogenSourcesWithoutSulfur, "ureia", f -> nvl(f.getN()) > 0d));
        SimpleMineralFertilizerModel phosphorus = fertilizerOpportunityCostService.selectLowestCostSimpleSource(
                phosphorusSourcesWithoutSulfur, FertilizerOpportunityCostService.Nutrient.P2O5).orElseGet(() ->
                namedSource(phosphorusSourcesWithoutSulfur, "superfosfato triplo", f -> nvl(f.getP2O5()) > 0d));
        SimpleMineralFertilizerModel potassium = namedSource(
                fertilizers, "cloreto de potassio", f -> nvl(f.getK2O()) > 0d);

        addByNutrient(pkg, nitrogen, nvl(requiredN), "N",
                "N atendido pela fonte de menor custo unitário disponível; S fornecido é excedente não limitante.");
        addByNutrient(pkg, phosphorus, Math.max(0d, nvl(requiredP2O5) - pkg.p2o5), "P2O5",
                "P2O5 atendido pela fonte de menor custo unitário disponível; S fornecido é excedente não limitante.");
        addByNutrient(pkg, potassium, nvl(requiredK2O), "K2O", "Todo K2O aplicado como cloreto de potássio.");
        return pkg;
    }

    static boolean isNitrogenSourceEligibleAfterGypsum(SimpleMineralFertilizerModel fertilizer) {
        return fertilizer != null && nvlStatic(fertilizer.getN()) > 0d && nvlStatic(fertilizer.getS()) <= 0d;
    }

    static boolean isPreferredPhosphorusSourceAfterGypsum(SimpleMineralFertilizerModel fertilizer) {
        if (fertilizer == null || nvlStatic(fertilizer.getP2O5()) <= 0d || nvlStatic(fertilizer.getS()) > 0d) {
            return false;
        }
        String normalizedName = normalizeText(fertilizer != null ? fertilizer.getName() : null).trim();
        return normalizedName.contains("superfosfato triplo")
                || normalizedName.equals("map")
                || normalizedName.startsWith("map ")
                || normalizedName.endsWith(" map");
    }

    private static double nvlStatic(Double value) {
        return value == null ? 0d : value;
    }

    private SimplePlantingPackage chooseBestSimplePlantingPackage(
            SimplePlantingPackage first,
            SimplePlantingPackage second,
            double requiredN,
            double requiredP2O5,
            double requiredK2O,
            double requiredS) {
        if (first == null) return second;
        if (second == null) return first;
        return simplePlantingScore(first, requiredN, requiredP2O5, requiredK2O, requiredS)
                <= simplePlantingScore(second, requiredN, requiredP2O5, requiredK2O, requiredS) ? first : second;
    }

    private double simplePlantingScore(SimplePlantingPackage pkg, double requiredN, double requiredP2O5, double requiredK2O, double requiredS) {
        if (pkg == null || pkg.rows.isEmpty()) return Double.MAX_VALUE;
        return Math.abs(pkg.n - requiredN)
                + Math.abs(pkg.p2o5 - requiredP2O5)
                + Math.abs(pkg.k2o - requiredK2O)
                + Math.abs(pkg.s - requiredS)
                + Math.max(0, pkg.rows.size() - 3) * 5d;
    }

    private SimplePlantingPackage buildSimplePlantingPackageByNitrogenFirst(
            List<SimpleMineralFertilizerModel> fertilizers,
            SulfurPlantingRequirement sulfurRequirement,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O) {
        SimplePlantingPackage pkg = new SimplePlantingPackage("prioridade N via sulfato de amônio");
        SimpleMineralFertilizerModel ammoniumSulfate = namedSource(fertilizers, "sulfato de amonio", f -> nvl(f.getN()) > 0d && nvl(f.getS()) > 0d);
        SimpleMineralFertilizerModel simpleSuper = namedSource(fertilizers, "superfosfato simples", f -> nvl(f.getP2O5()) > 0d && nvl(f.getS()) > 0d);
        SimpleMineralFertilizerModel map = namedSource(fertilizers, "map", f -> nvl(f.getP2O5()) > 0d && nvl(f.getN()) > 0d);
        SimpleMineralFertilizerModel urea = namedSource(fertilizers, "ureia", f -> nvl(f.getN()) > 0d);
        SimpleMineralFertilizerModel kcl = namedSource(fertilizers, "cloreto de potassio", f -> nvl(f.getK2O()) > 0d);

        addByNutrient(pkg, ammoniumSulfate, nvl(requiredN), "N", "Todo o N possível como sulfato de amônio para iniciar o atendimento de S.");
        addByNutrient(pkg, simpleSuper, Math.max(0d, Math.min(nvl(requiredP2O5) - pkg.p2o5, sulfurRequirement.requiredS() - pkg.s)), "S",
                "Complemento de S com superfosfato simples usando P2O5 demandado no plantio.");
        addByNutrient(pkg, map, Math.max(0d, nvl(requiredP2O5) - pkg.p2o5), "P2O5",
                "Complemento de P2O5 remanescente com MAP.");
        addByNutrient(pkg, urea, Math.max(0d, nvl(requiredN) - pkg.n), "N",
                "Complemento de N remanescente com ureia.");
        addByNutrient(pkg, kcl, nvl(requiredK2O), "K2O", "Todo K2O aplicado como cloreto de potássio.");
        return pkg;
    }

    private SimplePlantingPackage buildSimplePlantingPackageByPhosphorusSulfurFirst(
            List<SimpleMineralFertilizerModel> fertilizers,
            SulfurPlantingRequirement sulfurRequirement,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O) {
        SimplePlantingPackage pkg = new SimplePlantingPackage("prioridade S/P2O5 via superfosfato simples");
        SimpleMineralFertilizerModel simpleSuper = namedSource(fertilizers, "superfosfato simples", f -> nvl(f.getP2O5()) > 0d && nvl(f.getS()) > 0d);
        SimpleMineralFertilizerModel ammoniumSulfate = namedSource(fertilizers, "sulfato de amonio", f -> nvl(f.getN()) > 0d && nvl(f.getS()) > 0d);
        SimpleMineralFertilizerModel map = namedSource(fertilizers, "map", f -> nvl(f.getP2O5()) > 0d && nvl(f.getN()) > 0d);
        SimpleMineralFertilizerModel urea = namedSource(fertilizers, "ureia", f -> nvl(f.getN()) > 0d);
        SimpleMineralFertilizerModel kcl = namedSource(fertilizers, "cloreto de potassio", f -> nvl(f.getK2O()) > 0d);

        addByNutrient(pkg, simpleSuper, Math.max(nvl(requiredP2O5), sulfurRequirement.requiredS()), "S",
                "Tentativa inversa: maior parte do S via superfosfato simples, limitada pelo balanço de P2O5.");
        addByNutrient(pkg, ammoniumSulfate, Math.max(0d, Math.min(nvl(requiredN) - pkg.n, sulfurRequirement.requiredS() - pkg.s)), "S",
                "Complemento de S com sulfato de amônio.");
        addByNutrient(pkg, map, Math.max(0d, nvl(requiredP2O5) - pkg.p2o5), "P2O5",
                "Complemento de P2O5 remanescente com MAP.");
        addByNutrient(pkg, urea, Math.max(0d, nvl(requiredN) - pkg.n), "N",
                "Complemento de N remanescente com ureia.");
        addByNutrient(pkg, kcl, nvl(requiredK2O), "K2O", "Todo K2O aplicado como cloreto de potássio.");
        return pkg;
    }

    private void addByNutrient(SimplePlantingPackage pkg,
                               SimpleMineralFertilizerModel fertilizer,
                               double targetKgHa,
                               String nutrient,
                               String memory) {
        if (pkg == null || fertilizer == null || targetKgHa <= 0d) return;
        double percent = fertilizerPercent(fertilizer, nutrient);
        if (percent <= 0d) return;
        double dose = round2(100d * targetKgHa / percent);
        pkg.add(fertilizer, dose, nutrient, memory);
    }

    private double fertilizerPercent(SimpleMineralFertilizerModel fertilizer, String nutrient) {
        if (fertilizer == null || nutrient == null) return 0d;
        return switch (nutrient) {
            case "N" -> nvl(fertilizer.getN());
            case "P2O5" -> nvl(fertilizer.getP2O5());
            case "K2O" -> nvl(fertilizer.getK2O());
            case "S" -> nvl(fertilizer.getS());
            default -> 0d;
        };
    }

    private void addSuggestion(List<RecommendationCalculationService.FertilizerSuggestion> suggestions,
                               SimpleMineralFertilizerModel fertilizer,
                               String type,
                               String reason) {
        suggestions.add(RecommendationCalculationService.FertilizerSuggestion.builder()
                .fertilizerId(fertilizer.getId())
                .fertilizerType(type)
                .fertilizerName(fertilizer.getName())
                .n(fertilizer.getN())
                .p2o5(fertilizer.getP2O5())
                .k2o(fertilizer.getK2O())
                .s(fertilizer.getS())
                .reason(reason)
                .build());
    }

    private SimpleMineralFertilizerModel selectNamedOrBest(
            List<SimpleMineralFertilizerModel> fertilizers,
            String normalizedName,
            java.util.function.Predicate<SimpleMineralFertilizerModel> predicate,
            Comparator<SimpleMineralFertilizerModel> comparator) {
        if (fertilizers == null) return null;
        Optional<SimpleMineralFertilizerModel> named = fertilizers.stream()
                .filter(predicate)
                .filter(f -> normalizeText(f.getName()).contains(normalizedName))
                .max(comparator);
        return named.orElseGet(() -> fertilizers.stream().filter(predicate).max(comparator).orElse(null));
    }

    private SimpleMineralFertilizerModel namedSource(
            List<SimpleMineralFertilizerModel> fertilizers,
            String normalizedName,
            java.util.function.Predicate<SimpleMineralFertilizerModel> predicate) {
        if (fertilizers == null) return null;
        Optional<SimpleMineralFertilizerModel> named = fertilizers.stream()
                .filter(predicate)
                .filter(f -> normalizeText(f.getName()).contains(normalizedName))
                .findFirst();
        return named.orElseGet(() -> fertilizers.stream()
                .filter(predicate)
                .max(Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getN()) + nvl(f.getP2O5()) + nvl(f.getK2O()) + nvl(f.getS()))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()))
                .orElse(null));
    }

    private String buildConsolidatedCoverageApplicationMode(CropModel crop) {
        String dates = buildCropPhenologyReference(crop);
        return "Aplicação em cobertura consolidada. " + (dates.isBlank()
                ? "Sem data/fase específica suficiente no cadastro; seguir a fase técnica da cultura."
                : "Usar datas fenológicas informadas como referência técnica: " + dates + ".");
    }

    private List<RecommendationCalculationService.FertilizationRecommendationRow> buildCoverageRows(
            ContentRangeModel range,
            CropModel crop,
            UserModel user,
            FertilizerSourceOption sourceOption,
            List<RecommendationCalculationService.FertilizerSuggestion> suggestions,
            NutrientBalanceAccumulator balance,
            CoverageNpkAccumulator coverageNpkAccumulator,
            List<String> warnings) {
        List<RecommendationCalculationService.FertilizationRecommendationRow> rows = new ArrayList<>();
        Nutriente nutrient = range.getNutrient();
        List<CoverageModel> coverages = coverageRepository.findAllByRangeOrderByOrderAsc(range);

        for (CoverageModel c : coverages) {
            if (c.getApplication() == null) continue;

            double targetApplication = round2(c.getApplication());
            coverageNpkAccumulator.add(c.getOrder(), nutrient, targetApplication);
            String fertName = targetApplication > 0d ? "Fonte não definida" : "Não se aplica";
            Double q = null;
            String limitingNutrient = null;
            Double targetNeed = null;
            Double concentration = null;
            String calculationMemory = null;
            String warning = null;
            double providedN = 0d;
            double providedP2O5 = 0d;
            double providedK2O = 0d;

            if (targetApplication <= 0d) {
                q = 0d;
                targetNeed = 0d;
                balance.addCoverage(nutrient, 0d, 0d, 0d, 0d);
                calculationMemory = "Dose de cobertura cadastrada como 0,00 kg/ha na tabela técnica da cultura.";
            } else {
                var simples = selectSimpleFertilizers(user, sourceOption);
                SimpleMineralFertilizerModel best = selectCoverageFertilizer(simples, nutrient);
                if (best != null) {
                    double pct = coveragePercentage(best, nutrient);
                    if (pct > 0d) {
                        q = round2(targetApplication / pct * 100d);
                        limitingNutrient = nutrientLabel(nutrient);
                        targetNeed = targetApplication;
                        concentration = round2(pct);
                        providedN = round2(q * nvl(best.getN()) / 100d);
                        providedP2O5 = round2(q * nvl(best.getP2O5()) / 100d);
                        providedK2O = round2(q * nvl(best.getK2O()) / 100d);
                        balance.addCoverage(nutrient, targetApplication, providedN, providedP2O5, providedK2O);
                        calculationMemory = buildCalculationMemory(new FertilizerDoseCalculation(limitingNutrient, targetNeed, concentration, q, "dose de cobertura cadastrada na tabela técnica e concentração do nutriente alvo em fertilizante simples"), providedN, providedP2O5, providedK2O, balance.balanceN(), balance.balanceP2O5(), balance.balanceK2O());
                    } else {
                        warning = "Adubo mineral simples encontrado para cobertura de " + nutrient + ", mas sem concentração válida do nutriente alvo; dose não calculada.";
                        warnings.add(warning);
                    }
                    fertName = best.getName();
                    suggestions.add(RecommendationCalculationService.FertilizerSuggestion.builder()
                            .fertilizerId(best.getId())
                            .fertilizerType("SIMPLES")
                            .fertilizerName(best.getName())
                            .n(best.getN())
                            .p2o5(best.getP2O5())
                            .k2o(best.getK2O())
                            .s(best.getS())
                            .reason("Cobertura por " + nutrient + " baseada na dose cadastrada na tabela técnica da cultura.")
                            .build());
                } else {
                    balance.addCoverage(nutrient, targetApplication, 0d, 0d, 0d);
                    warnings.add("Não foi encontrado adubo mineral simples para cobertura de " + nutrient + "; a fonte deve ser definida conforme disponibilidade e seleção de adubos.");
                }
            }

            rows.add(RecommendationCalculationService.FertilizationRecommendationRow.builder()
                    .phase("Cobertura " + c.getOrder() + " - " + nutrient)
                    .nutrients(nutrient + ": " + String.format(Locale.US, "%.2f", targetApplication) + " kg/ha")
                    .suggestedFertilizer(fertName)
                    .fertilizerQuantityKgHa(q)
                    .applicationMode(buildCoverageApplicationMode(c, crop))
                    .source("Tabela de adubação da cultura; aplicação cadastrada " + String.format(Locale.US, "%.2f", targetApplication) + " kg/ha")
                    .providedN(providedN)
                    .providedP2O5(providedP2O5)
                    .providedK2O(providedK2O)
                    .balanceN(balance.balanceN())
                    .balanceP2O5(balance.balanceP2O5())
                    .balanceK2O(balance.balanceK2O())
                    .limitingNutrient(limitingNutrient)
                    .targetNeedKgHa(targetNeed)
                    .productConcentrationPercent(concentration)
                    .calculationMemory(calculationMemory)
                    .warning(warning)
                    .build());
        }
        return rows;
    }

    private String buildCoverageApplicationMode(CoverageModel coverage, CropModel crop) {
        String base = "Aplicação de cobertura programada conforme tabela técnica da cultura.";
        String dates = buildCropPhenologyReference(crop);
        if (dates.isBlank()) {
            return base + " Sem data ou fase específica suficiente no modelo de cobertura; seguir a fase técnica definida para a " + coverageOrderLabel(coverage) + ".";
        }
        return base + " Sem data específica no modelo de cobertura; usar as datas fenológicas informadas como referência técnica: " + dates + ".";
    }

    private String coverageOrderLabel(CoverageModel coverage) {
        return coverage.getOrder() == null ? "cobertura cadastrada" : coverage.getOrder() + "ª cobertura";
    }

    private String buildCropPhenologyReference(CropModel crop) {
        if (crop == null) return "";
        List<String> dates = new ArrayList<>();
        addCropDate(dates, "plantio", crop.getPlantingDate());
        addCropDate(dates, "emergência", crop.getEmergenceDate());
        addCropDate(dates, "botonamento", crop.getButtoningDate());
        addCropDate(dates, "florescimento", crop.getFloweringDate());
        addCropDate(dates, "colheita", crop.getHarvestDate());
        return String.join(", ", dates);
    }

    private void addCropDate(List<String> dates, String label, com.migueltcc.fertintelligence.composedAttributes.crop.Date date) {
        if (date == null || date.getDay() <= 0 || date.getMonth() <= 0 || date.getYear() <= 0) return;
        dates.add(String.format(Locale.US, "%s %02d/%02d/%04d", label, date.getDay(), date.getMonth(), date.getYear()));
    }

    private SimpleMineralFertilizerModel selectCoverageFertilizer(List<SimpleMineralFertilizerModel> fertilizers, Nutriente nutrient) {
        if (nutrient == Nutriente.NITROGENIO) return fertilizers.stream().filter(f -> nvl(f.getN()) > 0d).max(Comparator.comparing(f -> nvl(f.getN()))).orElse(null);
        if (nutrient == Nutriente.POTASSIO) return fertilizers.stream().filter(f -> nvl(f.getK2O()) > 0d).max(Comparator.comparing(f -> nvl(f.getK2O()))).orElse(null);
        return fertilizers.stream().filter(f -> nvl(f.getP2O5()) > 0d).max(Comparator.comparing(f -> nvl(f.getP2O5()))).orElse(null);
    }

    private double coveragePercentage(SimpleMineralFertilizerModel fertilizer, Nutriente nutrient) {
        if (fertilizer == null) return 0d;
        if (nutrient == Nutriente.NITROGENIO) return nvl(fertilizer.getN());
        if (nutrient == Nutriente.POTASSIO) return nvl(fertilizer.getK2O());
        return nvl(fertilizer.getP2O5());
    }

    private String nutrientLabel(Nutriente nutrient) {
        if (nutrient == Nutriente.NITROGENIO) return "N";
        if (nutrient == Nutriente.POTASSIO) return "K2O";
        return "P2O5";
    }

    private List<SimpleMineralFertilizerModel> selectSimpleFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
        return switch (sourceOption) {
            case PRIVATE -> simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user);
            case PUBLIC -> simpleMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> dedup(dedup(simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user), simpleMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO), SimpleMineralFertilizerModel::getId), simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO), SimpleMineralFertilizerModel::getId);
        };
    }

    private int compareScore(double an, double ap, double ak, double bn, double bp, double bk, Double rn, Double rp, Double rk, Long aid, Long bid) {
        int as = (nvl(rn) > 0 && an > 0 ? 1 : 0) + (nvl(rp) > 0 && ap > 0 ? 1 : 0) + (nvl(rk) > 0 && ak > 0 ? 1 : 0);
        int bs = (nvl(rn) > 0 && bn > 0 ? 1 : 0) + (nvl(rp) > 0 && bp > 0 ? 1 : 0) + (nvl(rk) > 0 && bk > 0 ? 1 : 0);
        if (as != bs) return Integer.compare(as, bs);
        if (nvl(rp) > 0 && Double.compare(ap, bp) != 0) return Double.compare(ap, bp);
        return Long.compare(bid, aid);
    }

    private double nvl(Double v) {
        return v == null ? 0d : v;
    }

    private double round2(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static String normalizeText(String value) {
        if (value == null) return "";
        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private <T> List<T> dedup(List<T> a, List<T> b, Function<T, Long> id) {
        Map<Long, T> m = new LinkedHashMap<>();
        a.forEach(x -> m.putIfAbsent(id.apply(x), x));
        b.forEach(x -> m.putIfAbsent(id.apply(x), x));
        return new ArrayList<>(m.values());
    }

    private class NutrientBalanceAccumulator {
        private final double requiredN;
        private final double requiredP2O5;
        private final double requiredK2O;
        private final double requiredS;
        private double plantingN;
        private double plantingP2O5;
        private double plantingK2O;
        private double plantingS;
        private double coverageRecommendedN;
        private double coverageRecommendedP2O5;
        private double coverageRecommendedK2O;
        private double coverageProvidedN;
        private double coverageProvidedP2O5;
        private double coverageProvidedK2O;
        private double coverageRecommendedS;
        private double coverageProvidedS;

        NutrientBalanceAccumulator(Double requiredN, Double requiredP2O5, Double requiredK2O, Double requiredS) {
            this.requiredN = nvl(requiredN);
            this.requiredP2O5 = nvl(requiredP2O5);
            this.requiredK2O = nvl(requiredK2O);
            this.requiredS = nvl(requiredS);
        }

        void addPlanting(Double providedN, Double providedP2O5, Double providedK2O, Double providedS) {
            plantingN = nvl(providedN);
            plantingP2O5 = nvl(providedP2O5);
            plantingK2O = nvl(providedK2O);
            plantingS = nvl(providedS);
        }

        void addCoverage(Nutriente nutrient, double recommendedApplication, double providedN, double providedP2O5, double providedK2O) {
            if (nutrient == Nutriente.NITROGENIO) coverageRecommendedN = round2(coverageRecommendedN + recommendedApplication);
            else if (nutrient == Nutriente.POTASSIO) coverageRecommendedK2O = round2(coverageRecommendedK2O + recommendedApplication);
            else coverageRecommendedP2O5 = round2(coverageRecommendedP2O5 + recommendedApplication);
            coverageProvidedN = round2(coverageProvidedN + providedN);
            coverageProvidedP2O5 = round2(coverageProvidedP2O5 + providedP2O5);
            coverageProvidedK2O = round2(coverageProvidedK2O + providedK2O);
        }

        void addCoverageTotals(double recommendedN,
                               double recommendedK2O,
                               double plantingDeficitN,
                               double plantingDeficitK2O,
                               double recommendedS,
                               double providedN,
                               double providedP2O5,
                               double providedK2O,
                               double providedS) {
            coverageRecommendedN = round2(coverageRecommendedN + recommendedN + plantingDeficitN);
            coverageRecommendedK2O = round2(coverageRecommendedK2O + recommendedK2O + plantingDeficitK2O);
            coverageRecommendedS = round2(coverageRecommendedS + recommendedS);
            coverageProvidedN = round2(coverageProvidedN + providedN);
            coverageProvidedP2O5 = round2(coverageProvidedP2O5 + providedP2O5);
            coverageProvidedK2O = round2(coverageProvidedK2O + providedK2O);
            coverageProvidedS = round2(coverageProvidedS + providedS);
        }

        double requiredTotalN() { return round2(requiredN + coverageRecommendedN); }
        double requiredTotalP2O5() { return round2(requiredP2O5 + coverageRecommendedP2O5); }
        double requiredTotalK2O() { return round2(requiredK2O + coverageRecommendedK2O); }
        double requiredTotalS() { return round2(requiredS + coverageRecommendedS); }
        double providedTotalN() { return round2(plantingN + coverageProvidedN); }
        double providedTotalP2O5() { return round2(plantingP2O5 + coverageProvidedP2O5); }
        double providedTotalK2O() { return round2(plantingK2O + coverageProvidedK2O); }
        double providedTotalS() { return round2(plantingS + coverageProvidedS); }
        double balanceN() { return round2(providedTotalN() - requiredTotalN()); }
        double balanceP2O5() { return round2(providedTotalP2O5() - requiredTotalP2O5()); }
        double balanceK2O() { return round2(providedTotalK2O() - requiredTotalK2O()); }
        double balanceS() { return round2(providedTotalS() - requiredTotalS()); }

        List<RecommendationCalculationService.NutrientBalanceRow> toRows() {
            return List.of(
                    row("N", requiredTotalN(), plantingN, coverageRecommendedN, coverageProvidedN, providedTotalN(), balanceN()),
                    row("P2O5", requiredTotalP2O5(), plantingP2O5, coverageRecommendedP2O5, coverageProvidedP2O5, providedTotalP2O5(), balanceP2O5()),
                    row("K2O", requiredTotalK2O(), plantingK2O, coverageRecommendedK2O, coverageProvidedK2O, providedTotalK2O(), balanceK2O()),
                    row("S", requiredTotalS(), plantingS, coverageRecommendedS, coverageProvidedS, providedTotalS(), balanceS())
            );
        }

        private RecommendationCalculationService.NutrientBalanceRow row(String nutrient, double required, double planting, double coverageRecommended, double coverageProvided, double totalProvided, double balance) {
            return RecommendationCalculationService.NutrientBalanceRow.builder()
                    .nutrient(nutrient)
                    .requiredTotalKgHa(round2(required))
                    .providedByPlantingKgHa(round2(planting))
                    .recommendedCoverageKgHa(round2(coverageRecommended))
                    .providedByCoverageKgHa(round2(coverageProvided))
                    .providedTotalKgHa(round2(totalProvided))
                    .finalBalanceKgHa(round2(balance))
                    .status(balance < 0d ? "Déficit" : balance > 0d ? "Excedente" : "Atendido")
                    .build();
        }
    }

    private class CoverageDemand {
        private double recommendedN;
        private double recommendedK2O;
        private double plantingDeficitN;
        private double plantingDeficitK2O;
        private double plantingDeficitS;
        private Integer firstOrder;

        void add(Integer order, Nutriente nutrient, double applicationKgHa) {
            if (applicationKgHa <= 0d) return;
            if (firstOrder == null) firstOrder = order;
            if (nutrient == Nutriente.NITROGENIO) recommendedN = round2(recommendedN + applicationKgHa);
            if (nutrient == Nutriente.POTASSIO) recommendedK2O = round2(recommendedK2O + applicationKgHa);
        }

        void addPlantingDeficits(double n, double k2o, double s) {
            plantingDeficitN = round2(n);
            plantingDeficitK2O = round2(k2o);
            plantingDeficitS = round2(s);
        }

        double recommendedN() { return recommendedN; }
        double recommendedK2O() { return recommendedK2O; }
        double plantingDeficitN() { return plantingDeficitN; }
        double plantingDeficitK2O() { return plantingDeficitK2O; }
        double totalN() { return round2(recommendedN + plantingDeficitN); }
        double totalK2O() { return round2(recommendedK2O + plantingDeficitK2O); }
        double totalS() { return plantingDeficitS; }
        boolean isEmpty() { return totalN() <= 0d && totalK2O() <= 0d && totalS() <= 0d; }

        CoverageDemand minusProvided(double providedN, double providedK2O, double providedS) {
            CoverageDemand remaining = new CoverageDemand();
            remaining.firstOrder = firstOrder;
            remaining.recommendedN = round2(Math.max(0d, totalN() - providedN));
            remaining.recommendedK2O = round2(Math.max(0d, totalK2O() - providedK2O));
            remaining.plantingDeficitS = round2(Math.max(0d, totalS() - providedS));
            return remaining;
        }

        List<CoverageFormulatedFertilizerRecommendationService.CoverageNpkRecommendation> toFormulatedRecommendations() {
            if (isEmpty()) return List.of();
            return List.of(new CoverageFormulatedFertilizerRecommendationService.CoverageNpkRecommendation(
                    firstOrder == null ? 1 : firstOrder,
                    totalN(),
                    0d,
                    totalK2O(),
                    totalS()));
        }

        String nutrientsLabel() {
            return String.format(Locale.US,
                    "N: %.2f kg/ha, P2O5: 0.00 kg/ha, K2O: %.2f kg/ha, S: %.2f kg/ha",
                    totalN(), totalK2O(), totalS());
        }
    }

    private class SimpleCoveragePackage {
        private final CoverageDemand demand;
        private final List<RecommendationCalculationService.FertilizationRecommendationRow> rows = new ArrayList<>();
        private double providedN;
        private double providedK2O;
        private double providedS;

        SimpleCoveragePackage(CoverageDemand demand) {
            this.demand = demand;
        }

        void add(SimpleMineralFertilizerModel fertilizer, double dose, String phase, String limitingNutrient, String memory) {
            if (fertilizer == null || dose <= 0d) return;
            double n = round2(dose * nvl(fertilizer.getN()) / 100d);
            double k = round2(dose * nvl(fertilizer.getK2O()) / 100d);
            double s = round2(dose * nvl(fertilizer.getS()) / 100d);
            providedN = round2(providedN + n);
            providedK2O = round2(providedK2O + k);
            providedS = round2(providedS + s);
            rows.add(RecommendationCalculationService.FertilizationRecommendationRow.builder()
                    .phase(phase)
                    .nutrients(demand.nutrientsLabel())
                    .suggestedFertilizer(fertilizer.getName())
                    .fertilizerQuantityKgHa(round2(dose))
                    .applicationMode("Aplicação em cobertura.")
                    .source("Cobertura com adubos simples cadastrados.")
                    .providedN(n)
                    .providedP2O5(0d)
                    .providedK2O(k)
                    .providedS(s)
                    .limitingNutrient(limitingNutrient)
                    .targetNeedKgHa("N".equals(limitingNutrient) ? demand.totalN() : "K2O".equals(limitingNutrient) ? demand.totalK2O() : demand.totalS())
                    .productConcentrationPercent("N".equals(limitingNutrient) ? fertilizer.getN() : "K2O".equals(limitingNutrient) ? fertilizer.getK2O() : fertilizer.getS())
                    .calculationMemory(memory)
                    .build());
        }

        double providedN() { return round2(providedN); }
        double providedK2O() { return round2(providedK2O); }
        double providedS() { return round2(providedS); }
        double balanceN() { return round2(providedN - demand.totalN()); }
        double balanceK2O() { return round2(providedK2O - demand.totalK2O()); }
        double balanceS() { return round2(providedS - demand.totalS()); }

        boolean withinTolerance() {
            return NutrientFertilizationCalculationService.this.withinTolerance(providedN, demand.totalN())
                    && NutrientFertilizationCalculationService.this.withinTolerance(providedK2O, demand.totalK2O())
                    && NutrientFertilizationCalculationService.this.withinTolerance(providedS, demand.totalS());
        }

        List<RecommendationCalculationService.FertilizationRecommendationRow> toRows(CropModel crop, String source) {
            return rows.stream()
                    .map(row -> row.toBuilder()
                            .applicationMode(buildConsolidatedCoverageApplicationMode(crop))
                            .source(source)
                            .balanceN(balanceN())
                            .balanceP2O5(0d)
                            .balanceK2O(balanceK2O())
                            .balanceS(balanceS())
                            .build())
                    .toList();
        }
    }

    private class CoverageNpkAccumulator {
        private final Map<Integer, CoverageNpkDose> dosesByOrder = new LinkedHashMap<>();

        void add(Integer coverageOrder, Nutriente nutrient, double applicationKgHa) {
            CoverageNpkDose dose = dosesByOrder.computeIfAbsent(coverageOrder, CoverageNpkDose::new);
            dose.add(nutrient, applicationKgHa);
        }

        List<CoverageFormulatedFertilizerRecommendationService.CoverageNpkRecommendation> toRecommendations() {
            return dosesByOrder.values().stream()
                    .map(dose -> new CoverageFormulatedFertilizerRecommendationService.CoverageNpkRecommendation(
                            dose.coverageOrder,
                            round2(dose.requiredN),
                            round2(dose.requiredP2O5),
                            round2(dose.requiredK2O),
                            0d))
                    .toList();
        }
    }

    private class CoverageNpkDose {
        private final Integer coverageOrder;
        private double requiredN;
        private double requiredP2O5;
        private double requiredK2O;

        CoverageNpkDose(Integer coverageOrder) {
            this.coverageOrder = coverageOrder;
        }

        void add(Nutriente nutrient, double applicationKgHa) {
            if (nutrient == Nutriente.NITROGENIO) requiredN = round2(requiredN + applicationKgHa);
            else if (nutrient == Nutriente.POTASSIO) requiredK2O = round2(requiredK2O + applicationKgHa);
            else requiredP2O5 = round2(requiredP2O5 + applicationKgHa);
        }
    }

    private record FertilizerSelection(
            String name,
            Double quantityKgHa,
            Double providedN,
            Double providedP2O5,
            Double providedK2O,
            Double providedS,
            Double balanceN,
            Double balanceP2O5,
            Double balanceK2O,
            String limitingNutrient,
            Double targetNeedKgHa,
            Double productConcentrationPercent,
            String calculationMemory,
            String warning,
            Optional<RecommendationCalculationService.FertilizerSuggestion> suggestion) {
    }

    private record FertilizerDoseCalculation(String nutrient, double targetNeedKgHa, double concentrationPercent, double quantityKgHa, String method) {
    }

    private record MicronutrientRecommendationInput(
            String label,
            Double analyzedValue,
            String interpretation,
            Double doseKgHa) {
    }

    record SulfurPartition(double plantingS, double coverageS) {
    }

    private record SulfurPlantingRequirement(
            double requiredS,
            double coverageRequiredS,
            double totalRequiredS,
            String interpretation,
            String memory,
            boolean calculated) {
        static SulfurPlantingRequirement notCalculated() {
            return new SulfurPlantingRequirement(0d, 0d, 0d, null, "Dose de S não calculada por critério insuficiente.", false);
        }

        static SulfurPlantingRequirement suppliedByGypsum() {
            return new SulfurPlantingRequirement(0d, 0d, 0d, "Atendido pela gessagem", "S não limitante no plantio e na cobertura devido à gessagem efetiva.", true);
        }
    }

    private record SulfurPlantingPlan(
            List<RecommendationCalculationService.FertilizationRecommendationRow> rows,
            List<RecommendationCalculationService.FertilizerSuggestion> suggestions,
            double providedN,
            double providedP2O5,
            double providedK2O,
            double providedS,
            boolean replacesPlanting) {
        static SulfurPlantingPlan empty() {
            return new SulfurPlantingPlan(List.of(), List.of(), 0d, 0d, 0d, 0d, false);
        }
    }

    private record SimplePlantingPackageResult(
            List<RecommendationCalculationService.FertilizationRecommendationRow> rows,
            List<RecommendationCalculationService.FertilizerSuggestion> suggestions,
            double transferredN,
            double transferredK2O,
            double providedN,
            double providedP2O5,
            double providedK2O,
            double providedS) {
    }

    private class SimplePlantingPackage {
        private final String strategy;
        private final List<RecommendationCalculationService.FertilizationRecommendationRow> rows = new ArrayList<>();
        private final List<RecommendationCalculationService.FertilizerSuggestion> suggestions = new ArrayList<>();
        private double n;
        private double p2o5;
        private double k2o;
        private double s;
        private String warning;

        SimplePlantingPackage(String strategy) {
            this.strategy = strategy;
        }

        void add(SimpleMineralFertilizerModel fertilizer, double dose, String targetNutrient, String memory) {
            if (fertilizer == null || dose <= 0d) return;
            double providedN = round2(dose * nvl(fertilizer.getN()) / 100d);
            double providedP = round2(dose * nvl(fertilizer.getP2O5()) / 100d);
            double providedK = round2(dose * nvl(fertilizer.getK2O()) / 100d);
            double providedS = round2(dose * nvl(fertilizer.getS()) / 100d);
            n = round2(n + providedN);
            p2o5 = round2(p2o5 + providedP);
            k2o = round2(k2o + providedK);
            s = round2(s + providedS);
            rows.add(RecommendationCalculationService.FertilizationRecommendationRow.builder()
                    .phase("Opção 2 - Plantio com adubos simples - " + targetNutrient)
                    .nutrients(String.format(Locale.US, "Fornecido: N %.2f, P2O5 %.2f, K2O %.2f, S %.2f kg/ha",
                            providedN, providedP, providedK, providedS))
                    .suggestedFertilizer(fertilizer.getName())
                    .fertilizerQuantityKgHa(round2(dose))
                    .applicationMode("Aplicação no plantio com adubo mineral simples.")
                    .source("Balanço estruturado de adubos simples para N-P2O5-K2O-S no plantio.")
                    .providedN(providedN)
                    .providedP2O5(providedP)
                    .providedK2O(providedK)
                    .providedS(providedS)
                    .limitingNutrient(targetNutrient)
                    .targetNeedKgHa(targetNeedForRow(targetNutrient, providedN, providedP, providedK, providedS))
                    .productConcentrationPercent(fertilizerPercent(fertilizer, targetNutrient))
                    .calculationMemory(memory + " Estratégia: " + strategy + ". Dose = 100 * nutriente recomendado / teor % da fonte cadastrada.")
                    .build());
            addSuggestion(suggestions, fertilizer, "SIMPLES", "Opção 2 - plantio com adubos simples; " + memory);
        }

        List<RecommendationCalculationService.FertilizationRecommendationRow> toRows(
                CropModel crop,
                double requiredN,
                double requiredP2O5,
                double requiredK2O,
                double requiredS,
                double transferredN) {
            String transfer = transferredN > tolerance(requiredN)
                    ? String.format(Locale.US, " Saldo de N transferido para a segunda opção de cobertura: %.2f kg/ha.", transferredN)
                    : "";
            return rows.stream()
                    .map(row -> row.toBuilder()
                            .balanceN(round2(n - requiredN))
                            .balanceP2O5(round2(p2o5 - requiredP2O5))
                            .balanceK2O(round2(k2o - requiredK2O))
                            .balanceS(round2(s - requiredS))
                            .applicationMode(row.getApplicationMode() + " " + buildCropPhenologyReference(crop) + transfer)
                            .warning(warning)
                            .build())
                    .toList();
        }

        private double targetNeedForRow(String targetNutrient, double providedN, double providedP, double providedK, double providedS) {
            return switch (targetNutrient) {
                case "N" -> providedN;
                case "P2O5" -> providedP;
                case "K2O" -> providedK;
                case "S" -> providedS;
                default -> 0d;
            };
        }
    }

    private class SimplePackage {
        private final List<RecommendationCalculationService.FertilizationRecommendationRow> rows = new ArrayList<>();
        private final List<RecommendationCalculationService.FertilizerSuggestion> suggestions = new ArrayList<>();
        private double n;
        private double p2o5;
        private double k2o;
        private double s;

        void add(String name, double rawDose, double nPct, double pPct, double kPct, double sPct, String reason) {
            double dose = round2(rawDose);
            if (dose <= 0d) return;
            double providedN = round2(dose * nPct / 100d);
            double providedP = round2(dose * pPct / 100d);
            double providedK = round2(dose * kPct / 100d);
            double providedS = round2(dose * sPct / 100d);
            n = round2(n + providedN);
            p2o5 = round2(p2o5 + providedP);
            k2o = round2(k2o + providedK);
            s = round2(s + providedS);
            rows.add(RecommendationCalculationService.FertilizationRecommendationRow.builder()
                    .phase(name.equals("Cloreto de potássio") ? "Opção 2 - Plantio com adubos simples - K2O" : "Opção 2 - Plantio com adubos simples - " + (sPct > 0d ? "S" : "NPK"))
                    .nutrients(String.format(Locale.US, "Fornecido: N %.2f, P2O5 %.2f, K2O %.2f, S %.2f kg/ha",
                            providedN, providedP, providedK, providedS))
                    .suggestedFertilizer(name)
                    .fertilizerQuantityKgHa(dose)
                    .applicationMode("Aplicação no plantio com adubo mineral simples.")
                    .source("Balanço de adubos simples para N-P2O5-K2O-S no plantio.")
                    .providedN(providedN)
                    .providedP2O5(providedP)
                    .providedK2O(providedK)
                    .providedS(providedS)
                    .limitingNutrient(sPct > 0d ? "S" : nPct > 0d ? "N" : pPct > 0d ? "P2O5" : "K2O")
                    .productConcentrationPercent(sPct > 0d ? sPct : nPct > 0d ? nPct : pPct > 0d ? pPct : kPct)
                    .calculationMemory(reason)
                    .build());
            suggestions.add(fixedSuggestion("SIMPLES", name, nPct, pPct, kPct, sPct, reason));
        }

        SulfurPlantingPlan toPlan(SulfurPlantingRequirement requirement, double requiredN, double requiredP2O5, double requiredK2O) {
            List<RecommendationCalculationService.FertilizationRecommendationRow> completedRows = rows.stream()
                    .map(row -> row.toBuilder()
                            .balanceN(round2(n - requiredN))
                            .balanceP2O5(round2(p2o5 - requiredP2O5))
                            .balanceK2O(round2(k2o - requiredK2O))
                            .balanceS(round2(s - requirement.requiredS()))
                            .build())
                    .toList();
            return new SulfurPlantingPlan(completedRows, suggestions, n, p2o5, k2o, s, true);
        }
    }
}
