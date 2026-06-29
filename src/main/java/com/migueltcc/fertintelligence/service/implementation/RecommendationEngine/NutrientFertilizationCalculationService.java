package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CoverageModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.ContentRangeRepository;
import com.migueltcc.fertintelligence.repository.CoverageRepository;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
class NutrientFertilizationCalculationService {

    private final ContentRangeRepository contentRangeRepository;
    private final CoverageRepository coverageRepository;
    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;
    private final AlternativeFertilizationCalculationService alternativeFertilizationCalculationService;
    private final FormulatedFertilizerSelectionService formulatedFertilizerSelectionService;
    private final PlantingFormulatedFertilizerRecommendationService plantingFormulatedFertilizerRecommendationService;
    private final CoverageFormulatedFertilizerRecommendationService coverageFormulatedFertilizerRecommendationService;

    NutrientFertilizationCalculationService(ContentRangeRepository contentRangeRepository,
                                            CoverageRepository coverageRepository,
                                            SimpleMineralFertilizerRepository simpleMineralFertilizerRepository,
                                            AlternativeFertilizationCalculationService alternativeFertilizationCalculationService,
                                            FormulatedFertilizerSelectionService formulatedFertilizerSelectionService,
                                            PlantingFormulatedFertilizerRecommendationService plantingFormulatedFertilizerRecommendationService,
                                            CoverageFormulatedFertilizerRecommendationService coverageFormulatedFertilizerRecommendationService) {
        this.contentRangeRepository = contentRangeRepository;
        this.coverageRepository = coverageRepository;
        this.simpleMineralFertilizerRepository = simpleMineralFertilizerRepository;
        this.alternativeFertilizationCalculationService = alternativeFertilizationCalculationService;
        this.formulatedFertilizerSelectionService = formulatedFertilizerSelectionService;
        this.plantingFormulatedFertilizerRecommendationService = plantingFormulatedFertilizerRecommendationService;
        this.coverageFormulatedFertilizerRecommendationService = coverageFormulatedFertilizerRecommendationService;
    }

    FertilizationRecommendationContext calculate(CropFertilizationTableModel table,
                                                 CropModel crop,
                                                 Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                 SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
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
                                                 List<RecommendationCalculationService.FoliarDiagnosisItem> foliarDiagnosis) {
        List<RecommendationCalculationService.FertilizationRecommendationRow> recommendationRows = new ArrayList<>();
        List<RecommendationCalculationService.FertilizerSuggestion> fertilizerSuggestions = new ArrayList<>();

        Optional<ContentRangeModel> nRange = selectNitrogenRange(table);
        Optional<ContentRangeModel> pRange = selectNutrientRange(table, Nutriente.FOSFORO, extractPhosphorusValue(fertilityExtract), warnings, "fósforo (P) disponível em mg/dm³");
        Optional<ContentRangeModel> kRange = selectNutrientRange(table, Nutriente.POTASSIO, extractPotassiumValue(fertilityExtract), warnings, "potássio (K) trocável em mmolc/dm³");

        Double requiredN = nRange.map(ContentRangeModel::getApplication).orElse(null);
        Double requiredP2O5 = pRange.map(ContentRangeModel::getApplication).orElse(null);
        Double requiredK2O = kRange.map(ContentRangeModel::getApplication).orElse(null);
        Long nRangeId = nRange.map(ContentRangeModel::getId).orElse(null);
        Long pRangeId = pRange.map(ContentRangeModel::getId).orElse(null);
        Long kRangeId = kRange.map(ContentRangeModel::getId).orElse(null);

        if (nRange.isEmpty()) warnings.add("Não foi encontrado intervalo para NITROGENIO na tabela selecionada.");
        if (pRange.isEmpty()) warnings.add("Não foi encontrado intervalo para FOSFORO na tabela selecionada.");
        if (kRange.isEmpty()) warnings.add("Não foi encontrado intervalo para POTASSIO na tabela selecionada.");

        AlternativeFertilizationCalculationService.AlternativeFertilizationCalculationResult alternativeFertilizationResult =
                alternativeFertilizationCalculationService.calculate(
                        requiredN, requiredP2O5, requiredK2O, crop, chemicalDiagnosis, foliarDiagnosis,
                        soilInterpretationTable, user, sourceOption, useOrganicFertilizer,
                        organicFertilizerReferenceNutrient, useOrganoMineralFertilizer, useGreenFertilizer, greenFertilizerSpecies,
                        greenFertilizerGreenMass, greenFertilizerMoisturePercentage, greenFertilizerDryMass,
                        useBioFertilizer, warnings);
        Double mineralRequiredN = alternativeFertilizationResult.remainingRequiredN();
        Double mineralRequiredP2O5 = alternativeFertilizationResult.remainingRequiredP2O5();
        Double mineralRequiredK2O = alternativeFertilizationResult.remainingRequiredK2O();

        FertilizerSelection planting = selectBestPlantingFertilizer(user, sourceOption, mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O, warnings);
        planting.suggestion().ifPresent(fertilizerSuggestions::add);
        NutrientBalanceAccumulator nutrientBalance = new NutrientBalanceAccumulator(mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O);
        nutrientBalance.addPlanting(planting.providedN(), planting.providedP2O5(), planting.providedK2O());
        CoverageNpkAccumulator coverageNpkAccumulator = new CoverageNpkAccumulator();

        recommendationRows.add(RecommendationCalculationService.FertilizationRecommendationRow.builder()
                .phase("Plantio")
                .nutrients(String.format("N: %.2f kg/ha, P2O5: %.2f kg/ha, K2O: %.2f kg/ha", nvl(mineralRequiredN), nvl(mineralRequiredP2O5), nvl(mineralRequiredK2O)))
                .suggestedFertilizer(planting.name())
                .fertilizerQuantityKgHa(planting.quantityKgHa())
                .providedN(planting.providedN())
                .providedP2O5(planting.providedP2O5())
                .providedK2O(planting.providedK2O())
                .balanceN(planting.balanceN())
                .balanceP2O5(planting.balanceP2O5())
                .balanceK2O(planting.balanceK2O())
                .limitingNutrient(planting.limitingNutrient())
                .targetNeedKgHa(planting.targetNeedKgHa())
                .productConcentrationPercent(planting.productConcentrationPercent())
                .calculationMemory(planting.calculationMemory())
                .warning(planting.warning())
                .applicationMode("Aplicação no plantio, conforme recomendação técnica.")
                .source("Tabela de adubação de culturas ID " + table.getId())
                .build());

        for (ContentRangeModel selectedRange : List.of(nRange.orElse(null), pRange.orElse(null), kRange.orElse(null))) {
            if (selectedRange != null) {
                recommendationRows.addAll(buildCoverageRows(
                        selectedRange, crop, user, sourceOption, fertilizerSuggestions, nutrientBalance, coverageNpkAccumulator, warnings));
            }
        }
        recommendationRows.add(RecommendationCalculationService.FertilizationRecommendationRow.builder()
                .phase("Balanço global NPK")
                .nutrients("Consolidado após plantio e coberturas recomendadas")
                .suggestedFertilizer("Não se aplica")
                .applicationMode("Memória de cálculo consolidada em kg/ha.")
                .providedN(nutrientBalance.providedTotalN())
                .providedP2O5(nutrientBalance.providedTotalP2O5())
                .providedK2O(nutrientBalance.providedTotalK2O())
                .balanceN(nutrientBalance.balanceN())
                .balanceP2O5(nutrientBalance.balanceP2O5())
                .balanceK2O(nutrientBalance.balanceK2O())
                .source("Balanço global calculado pelo backend")
                .build());
        List<RecommendationCalculationService.NutrientBalanceRow> nutrientBalanceRows = nutrientBalance.toRows();

        List<RecommendationCalculationService.PlantingFormulatedFertilizerRecommendationRow> plantingFormulatedRows =
                plantingFormulatedFertilizerRecommendationService.calculate(
                        user, sourceOption, mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O, crop, warnings);
        List<RecommendationCalculationService.CoverageFormulatedFertilizerRecommendationRow> coverageFormulatedRows =
                coverageFormulatedFertilizerRecommendationService.calculate(
                        user, sourceOption, coverageNpkAccumulator.toRecommendations(), crop, warnings);

        return new FertilizationRecommendationContext(
                recommendationRows, fertilizerSuggestions, nutrientBalanceRows,
                alternativeFertilizationResult.alternativeRows(),
                alternativeFertilizationResult.directRecommendationRows(),
                plantingFormulatedRows,
                coverageFormulatedRows,
                mineralRequiredN, mineralRequiredP2O5, mineralRequiredK2O, nRangeId, pRangeId, kRangeId);
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
            var s = RecommendationCalculationService.FertilizerSuggestion.builder().fertilizerId(f.getId()).fertilizerType("SIMPLES").fertilizerName(f.getName()).n(f.getN()).p2o5(f.getP2O5()).k2o(f.getK2O()).reason("Fallback por ausência de formulado adequado; dose calculada pelo nutriente alvo identificado.").build();
            if (calc.isEmpty()) {
                String warning = "Fertilizante simples selecionado, mas sem nutriente alvo com necessidade e concentração válidas para calcular dose comercial.";
                w.add(warning);
                return buildSelection(s.getFertilizerName(), (Double) null, n, p, k, f.getN(), f.getP2O5(), f.getK2O(), null, w, warning, Optional.of(s));
            }
            w.add("Quantidade de adubo simples calculada pela concentração do nutriente alvo identificado.");
            return buildSelection(s.getFertilizerName(), calc.get(), n, p, k, f.getN(), f.getP2O5(), f.getK2O(), w, null, Optional.of(s));
        }
        w.add("Nenhum adubo mineral adequado foi encontrado para a origem de adubos selecionada.");
        return new FertilizerSelection("Não encontrado", null, null, null, null, null, null, null, null, null, null, null, null, Optional.empty());
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
            warning = String.format(Locale.US,
                    "Fertilizante formulado selecionado não atende todos os nutrientes no plantio. Déficits: N %.2f kg/ha, P2O5 %.2f kg/ha, K2O %.2f kg/ha.",
                    nvl(selected.deficitN()), nvl(selected.deficitP2O5()), nvl(selected.deficitK2O()));
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
                        .reason("Seleção de formulado NPK por " + formulatedSelectionType(selected) + ".")
                        .build();

        return new FertilizerSelection(
                suggestion.getFertilizerName(),
                round2(nvl(selected.fertilizerDoseKgHa())),
                round2(nvl(selected.providedN())),
                round2(nvl(selected.providedP2O5())),
                round2(nvl(selected.providedK2O())),
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
        return new FertilizerSelection(name, q, pn, pp, pk, bn, bp, bk, calc != null ? calc.nutrient() : null, calc != null ? calc.targetNeedKgHa() : null, calc != null ? calc.concentrationPercent() : null, memory, effectiveWarning, suggestion);
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
        private double plantingN;
        private double plantingP2O5;
        private double plantingK2O;
        private double coverageRecommendedN;
        private double coverageRecommendedP2O5;
        private double coverageRecommendedK2O;
        private double coverageProvidedN;
        private double coverageProvidedP2O5;
        private double coverageProvidedK2O;

        NutrientBalanceAccumulator(Double requiredN, Double requiredP2O5, Double requiredK2O) {
            this.requiredN = nvl(requiredN);
            this.requiredP2O5 = nvl(requiredP2O5);
            this.requiredK2O = nvl(requiredK2O);
        }

        void addPlanting(Double providedN, Double providedP2O5, Double providedK2O) {
            plantingN = nvl(providedN);
            plantingP2O5 = nvl(providedP2O5);
            plantingK2O = nvl(providedK2O);
        }

        void addCoverage(Nutriente nutrient, double recommendedApplication, double providedN, double providedP2O5, double providedK2O) {
            if (nutrient == Nutriente.NITROGENIO) coverageRecommendedN = round2(coverageRecommendedN + recommendedApplication);
            else if (nutrient == Nutriente.POTASSIO) coverageRecommendedK2O = round2(coverageRecommendedK2O + recommendedApplication);
            else coverageRecommendedP2O5 = round2(coverageRecommendedP2O5 + recommendedApplication);
            coverageProvidedN = round2(coverageProvidedN + providedN);
            coverageProvidedP2O5 = round2(coverageProvidedP2O5 + providedP2O5);
            coverageProvidedK2O = round2(coverageProvidedK2O + providedK2O);
        }

        double requiredTotalN() { return round2(requiredN + coverageRecommendedN); }
        double requiredTotalP2O5() { return round2(requiredP2O5 + coverageRecommendedP2O5); }
        double requiredTotalK2O() { return round2(requiredK2O + coverageRecommendedK2O); }
        double providedTotalN() { return round2(plantingN + coverageProvidedN); }
        double providedTotalP2O5() { return round2(plantingP2O5 + coverageProvidedP2O5); }
        double providedTotalK2O() { return round2(plantingK2O + coverageProvidedK2O); }
        double balanceN() { return round2(providedTotalN() - requiredTotalN()); }
        double balanceP2O5() { return round2(providedTotalP2O5() - requiredTotalP2O5()); }
        double balanceK2O() { return round2(providedTotalK2O() - requiredTotalK2O()); }

        List<RecommendationCalculationService.NutrientBalanceRow> toRows() {
            return List.of(
                    row("N", requiredTotalN(), plantingN, coverageRecommendedN, coverageProvidedN, providedTotalN(), balanceN()),
                    row("P2O5", requiredTotalP2O5(), plantingP2O5, coverageRecommendedP2O5, coverageProvidedP2O5, providedTotalP2O5(), balanceP2O5()),
                    row("K2O", requiredTotalK2O(), plantingK2O, coverageRecommendedK2O, coverageProvidedK2O, providedTotalK2O(), balanceK2O())
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
                            round2(dose.requiredK2O)))
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
}
