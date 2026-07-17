package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.FormulatedMineralFertilizerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class FormulatedFertilizerSelectionService {

    static final double EFFECTIVE_REQUIREMENT_TOLERANCE = 1.0e-6d;
    public static final String SINGLE_NUTRIENT_FORMULATED_REJECTION =
            "Não é possível usar formulado na adubação, pois apenas um dos nutrientes NPK é necessário. "
                    + "Nessa situação, optar, obrigatoriamente, pelo uso de adubos simples, aplicando tão somente o nutriente necessário.";
    private static final int PRESENTATION_LIMIT = 2;
    private static final double APPROXIMATE_SUPPLY_TOLERANCE = 0.10d;

    private final FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;
    private final FormulatedFertilizerRatioService ratioService;

    public FormulatedFertilizerSelectionService(FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository,
                                                FormulatedFertilizerRatioService ratioService) {
        this.formulatedMineralFertilizerRepository = formulatedMineralFertilizerRepository;
        this.ratioService = ratioService;
    }

    public List<FormulatedFertilizerSelectionCandidate> selectTopCandidates(UserModel user,
                                                                            FertilizerSourceOption sourceOption,
                                                                            Double requiredN,
                                                                            Double requiredP2O5,
                                                                            Double requiredK2O) {
        return limitForPresentation(selectCandidates(user, sourceOption, requiredN, requiredP2O5, requiredK2O).candidates());
    }

    public List<FormulatedFertilizerSelectionCandidate> selectAllCandidates(UserModel user,
                                                                            FertilizerSourceOption sourceOption,
                                                                            Double requiredN,
                                                                            Double requiredP2O5,
                                                                            Double requiredK2O) {
        return selectCandidates(user, sourceOption, requiredN, requiredP2O5, requiredK2O).candidates();
    }

    public FormulatedFertilizerSelectionResult selectCandidates(UserModel user,
                                                                FertilizerSourceOption sourceOption,
                                                                Double requiredN,
                                                                Double requiredP2O5,
                                                                Double requiredK2O) {
        return selectCandidates(selectFormulatedFertilizers(user, sourceOption), requiredN, requiredP2O5, requiredK2O);
    }

    public FormulatedFertilizerSelectionResult selectCoverageCandidates(UserModel user,
                                                                        FertilizerSourceOption sourceOption,
                                                                        Double requiredN,
                                                                        Double requiredK2O) {
        return selectCoverageCandidates(selectFormulatedFertilizers(user, sourceOption), requiredN, requiredK2O);
    }

    public List<FormulatedFertilizerSelectionCandidate> selectTopCandidates(List<FormulatedMineralFertilizerModel> fertilizers,
                                                                            Double requiredN,
                                                                            Double requiredP2O5,
                                                                            Double requiredK2O) {
        return limitForPresentation(selectCandidates(fertilizers, requiredN, requiredP2O5, requiredK2O).candidates());
    }

    public List<FormulatedFertilizerSelectionCandidate> selectAllCandidates(List<FormulatedMineralFertilizerModel> fertilizers,
                                                                            Double requiredN,
                                                                            Double requiredP2O5,
                                                                            Double requiredK2O) {
        return selectCandidates(fertilizers, requiredN, requiredP2O5, requiredK2O).candidates();
    }

    public FormulatedFertilizerSelectionResult selectCandidates(List<FormulatedMineralFertilizerModel> fertilizers,
                                                                Double requiredN,
                                                                Double requiredP2O5,
                                                                Double requiredK2O) {
        int requiredNutrientCount = countEffectiveRequiredNutrients(requiredN, requiredP2O5, requiredK2O);
        if (requiredNutrientCount == 1) {
            return new FormulatedFertilizerSelectionResult(
                    List.of(),
                    false,
                    SINGLE_NUTRIENT_FORMULATED_REJECTION);
        }
        if (requiredNutrientCount == 0) {
            return new FormulatedFertilizerSelectionResult(
                    List.of(),
                    false,
                    "Adubo formulado não avaliado: nenhum nutriente NPK possui necessidade efetiva positiva nesta fase.");
        }
        FormulatedFertilizerRatioService.RatioCalculationResult recommendedRatio =
                ratioService.calculateRecommendedRatio(requiredN, requiredP2O5, requiredK2O);
        if (!recommendedRatio.calculated()) {
            return new FormulatedFertilizerSelectionResult(
                    List.of(),
                    false,
                    recommendedRatio.technicalMessage());
        }
        if (fertilizers == null || fertilizers.isEmpty()) {
            return new FormulatedFertilizerSelectionResult(
                    List.of(),
                    false,
                    appendTechnicalMessage(
                            recommendedRatio.technicalMessage(),
                            "Sem adubo mineral formulado cadastrado para avaliar relação N-P2O5-K2O."));
        }

        List<FormulatedFertilizerSelectionCandidate> candidates = fertilizers.stream()
                .map(fertilizer -> toDirectMatchCandidate(
                        fertilizer,
                        recommendedRatio.ratio(),
                        recommendedRatio.technicalMessage(),
                        requiredN,
                        requiredP2O5,
                        requiredK2O))
                .filter(candidate -> candidate != null)
                .sorted(candidateComparator())
                .toList();

        if (candidates.isEmpty()) {
            String noDirectMatchMessage = appendTechnicalMessage(
                    recommendedRatio.technicalMessage(),
                    "Sem adubo mineral formulado com relação N-P2O5-K2O idêntica à recomendação calculada.");
            List<FormulatedFertilizerSelectionCandidate> approximateCandidates =
                    selectApproximateCandidates(
                            fertilizers,
                            recommendedRatio.ratio(),
                            noDirectMatchMessage,
                            requiredN,
                            requiredP2O5,
                            requiredK2O);
            if (!approximateCandidates.isEmpty()) {
                return new FormulatedFertilizerSelectionResult(
                        approximateCandidates,
                        true,
                        appendTechnicalMessage(
                                noDirectMatchMessage,
                                "Seleção aproximada aplicada somente a formulados com fornecimento estimado dentro de +/-10% da recomendação."));
            }
            List<FormulatedFertilizerSelectionCandidate> maximizationCandidates =
                    selectMaximizationCandidates(
                            fertilizers,
                            recommendedRatio.ratio(),
                            noDirectMatchMessage,
                            requiredN,
                            requiredP2O5,
                            requiredK2O);
            if (!maximizationCandidates.isEmpty()) {
                return new FormulatedFertilizerSelectionResult(
                        maximizationCandidates,
                        true,
                        appendTechnicalMessage(
                                noDirectMatchMessage,
                                "Seleção por maximização aplicada após falha da seleção aproximada; quando possível a dose prioriza P2O5 exato, sem exceder nutrientes acima de 10%, e os déficits de N/K2O seguem para cobertura."));
            }
            return new FormulatedFertilizerSelectionResult(
                    List.of(),
                    false,
                    appendTechnicalMessage(
                            noDirectMatchMessage,
                            "Nenhum formulado aproximado ou aplicável à maximização permaneceu válido sem exceder nutriente acima da tolerância de 10%. Usar adubos simples ou outra alternativa válida."));
        }

        return new FormulatedFertilizerSelectionResult(candidates, false, recommendedRatio.technicalMessage());
    }

    static int countEffectiveRequiredNutrients(Double requiredN, Double requiredP2O5, Double requiredK2O) {
        return (isEffectiveRequirement(requiredN) ? 1 : 0)
                + (isEffectiveRequirement(requiredP2O5) ? 1 : 0)
                + (isEffectiveRequirement(requiredK2O) ? 1 : 0);
    }

    private static boolean isEffectiveRequirement(Double value) {
        return value != null && Double.isFinite(value) && value > EFFECTIVE_REQUIREMENT_TOLERANCE;
    }

    public FormulatedFertilizerSelectionResult selectCoverageCandidates(List<FormulatedMineralFertilizerModel> fertilizers,
                                                                        Double requiredN,
                                                                        Double requiredK2O) {
        if (countEffectiveRequiredNutrients(requiredN, 0d, requiredK2O) < 2) {
            return selectCandidates(List.of(), requiredN, 0d, requiredK2O);
        }
        List<FormulatedMineralFertilizerModel> safeFertilizers = fertilizers != null ? fertilizers : List.of();
        List<FormulatedMineralFertilizerModel> withoutPhosphorus = safeFertilizers.stream()
                .filter(this::doesNotExceedCoveragePhosphorus)
                .toList();
        FormulatedFertilizerSelectionResult phosphorusFreeSelection =
                selectCandidates(withoutPhosphorus, requiredN, 0d, requiredK2O);
        if (phosphorusFreeSelection.candidates() != null && !phosphorusFreeSelection.candidates().isEmpty()) {
            return phosphorusFreeSelection;
        }

        List<FormulatedMineralFertilizerModel> withPhosphorus = safeFertilizers.stream()
                .filter(fertilizer -> normalizeRequiredDose(fertilizer != null ? fertilizer.getP2O5() : null) > 0d)
                .toList();
        FormulatedFertilizerSelectionResult phosphorusSelection =
                selectCandidates(withPhosphorus, requiredN, 0d, requiredK2O);
        if (phosphorusSelection.candidates() == null || phosphorusSelection.candidates().isEmpty()) {
            return new FormulatedFertilizerSelectionResult(
                    List.of(),
                    false,
                    appendTechnicalMessage(
                            phosphorusFreeSelection.technicalMessage(),
                            phosphorusSelection.technicalMessage()));
        }

        String concessionMessage = "Concessão técnica: cobertura de cultura anual tem P2O5 recomendado igual a 0; "
                + "foi permitido formulado contendo P2O5 somente porque nenhuma alternativa aceitável sem P2O5 foi encontrada. "
                + "Critério utilizado: aplicar a seleção N-P2O5-K2O com P2O5 requerido zerado sobre os formulados contendo P2O5 disponíveis, "
                + "mantendo déficits e excedentes explícitos.";
        return new FormulatedFertilizerSelectionResult(
                phosphorusSelection.candidates().stream()
                        .map(candidate -> appendCandidateTechnicalMessage(candidate, concessionMessage))
                        .toList(),
                true,
                appendTechnicalMessage(
                        appendTechnicalMessage(phosphorusFreeSelection.technicalMessage(), phosphorusSelection.technicalMessage()),
                        concessionMessage));
    }

    private FormulatedFertilizerSelectionCandidate toDirectMatchCandidate(FormulatedMineralFertilizerModel fertilizer,
                                                                          NPKrelation recommendedRatio,
                                                                          String recommendedRatioMessage,
                                                                          Double requiredN,
                                                                          Double requiredP2O5,
                                                                          Double requiredK2O) {
        FormulatedFertilizerRatioService.RatioCalculationResult formulatedRatio =
                ratioService.calculateFormulatedRatio(fertilizer);
        if (!formulatedRatio.calculated()
                || !ratioService.hasCompleteRatioMatch(recommendedRatio, formulatedRatio.ratio())) {
            return null;
        }

        Double concentrationSum = ratioService.calculateFormulatedConcentrationSum(fertilizer);
        if (concentrationSum == null || concentrationSum <= 0d) {
            return null;
        }

        Double fertilizerDoseKgHa = calculateFertilizerDoseKgHa(
                requiredNutrientSum(requiredN, requiredP2O5, requiredK2O),
                concentrationSum);
        return buildCandidate(
                fertilizer,
                recommendedRatio,
                formulatedRatio.ratio(),
                concentrationSum,
                fertilizerDoseKgHa,
                false,
                false,
                null,
                requiredN,
                requiredP2O5,
                requiredK2O,
                appendTechnicalMessage(recommendedRatioMessage, formulatedRatio.technicalMessage()));
    }

    private List<FormulatedFertilizerSelectionCandidate> selectApproximateCandidates(
            List<FormulatedMineralFertilizerModel> fertilizers,
            NPKrelation recommendedRatio,
            String baseTechnicalMessage,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O) {
        return fertilizers.stream()
                .map(fertilizer -> toApproximateCandidate(
                        fertilizer,
                        recommendedRatio,
                        baseTechnicalMessage,
                        requiredN,
                        requiredP2O5,
                        requiredK2O))
                .filter(candidate -> candidate != null)
                .sorted(Comparator
                        .comparing(ApproximateCandidate::ratioDistance)
                        .thenComparing(ApproximateCandidate::selectionCandidate, candidateComparator()))
                .map(ApproximateCandidate::selectionCandidate)
                .toList();
    }

    private ApproximateCandidate toApproximateCandidate(FormulatedMineralFertilizerModel fertilizer,
                                                        NPKrelation recommendedRatio,
                                                        String baseTechnicalMessage,
                                                        Double requiredN,
                                                        Double requiredP2O5,
                                                        Double requiredK2O) {
        FormulatedFertilizerRatioService.RatioCalculationResult formulatedRatio =
                ratioService.calculateFormulatedRatio(fertilizer);
        if (!formulatedRatio.calculated()) {
            return null;
        }

        Double concentrationSum = ratioService.calculateFormulatedConcentrationSum(fertilizer);
        if (concentrationSum == null || concentrationSum <= 0d) {
            return null;
        }

        Double fertilizerDoseKgHa = calculateDoseByPhosphorus(fertilizer, requiredP2O5);
        if (fertilizerDoseKgHa == null
                || !hasSupplyWithinTolerance(fertilizer, fertilizerDoseKgHa, requiredN, requiredP2O5, requiredK2O)) {
            return null;
        }

        return new ApproximateCandidate(
                buildCandidate(
                        fertilizer,
                        recommendedRatio,
                        formulatedRatio.ratio(),
                        concentrationSum,
                        fertilizerDoseKgHa,
                        true,
                        false,
                        null,
                        requiredN,
                        requiredP2O5,
                        requiredK2O,
                        appendTechnicalMessage(baseTechnicalMessage, formulatedRatio.technicalMessage())),
                calculateRatioDistance(recommendedRatio, formulatedRatio.ratio()));
    }

    private Double calculateDoseByPhosphorus(FormulatedMineralFertilizerModel fertilizer, Double requiredP2O5) {
        double required = normalizeRequiredDose(requiredP2O5);
        double concentration = normalizeRequiredDose(fertilizer != null ? fertilizer.getP2O5() : null);
        if (required <= 0d || concentration <= 0d) return null;
        double dose = 100d * required / concentration;
        return Double.isFinite(dose) && dose > 0d ? dose : null;
    }

    private List<FormulatedFertilizerSelectionCandidate> selectMaximizationCandidates(
            List<FormulatedMineralFertilizerModel> fertilizers,
            NPKrelation recommendedRatio,
            String baseTechnicalMessage,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O) {
        return fertilizers.stream()
                .map(fertilizer -> toMaximizationCandidate(
                        fertilizer,
                        recommendedRatio,
                        baseTechnicalMessage,
                        requiredN,
                        requiredP2O5,
                        requiredK2O))
                .filter(candidate -> candidate != null)
                .sorted(Comparator
                        .comparing(FormulatedFertilizerSelectionCandidate::coveragePercent,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(candidate -> totalDeficit(candidate.deficitN(), candidate.deficitP2O5(), candidate.deficitK2O()))
                        .thenComparing(FormulatedFertilizerSelectionCandidate::concentrationSum,
                                Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(candidate -> candidate.formulated() != null ? candidate.formulated().getId() : null,
                                Comparator.nullsLast(Long::compareTo)))
                .limit(1)
                .toList();
    }

    private FormulatedFertilizerSelectionCandidate toMaximizationCandidate(FormulatedMineralFertilizerModel fertilizer,
                                                                           NPKrelation recommendedRatio,
                                                                           String baseTechnicalMessage,
                                                                           Double requiredN,
                                                                           Double requiredP2O5,
                                                                           Double requiredK2O) {
        FormulatedFertilizerRatioService.RatioCalculationResult formulatedRatio =
                ratioService.calculateFormulatedRatio(fertilizer);
        if (!formulatedRatio.calculated()) {
            return null;
        }

        Double concentrationSum = ratioService.calculateFormulatedConcentrationSum(fertilizer);
        if (concentrationSum == null || concentrationSum <= 0d) {
            return null;
        }

        NutrientLimit limitingNutrient = findPhosphorusLimit(fertilizer, requiredP2O5)
                .orElseGet(() -> findMostLimitingNutrient(fertilizer, requiredN, requiredP2O5, requiredK2O));
        if (limitingNutrient == null) {
            return null;
        }

        double fertilizerDoseKgHa = limitingNutrient.doseKgHa();
        double providedN = calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer.getN());
        double providedP2O5 = calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer.getP2O5());
        double providedK2O = calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer.getK2O());
        if (exceedsMaximumSupplyTolerance(providedN, requiredN)
                || exceedsMaximumSupplyTolerance(providedP2O5, requiredP2O5)
                || exceedsMaximumSupplyTolerance(providedK2O, requiredK2O)) {
            return null;
        }

        return new FormulatedFertilizerSelectionCandidate(
                fertilizer,
                formulatedRatio.ratio(),
                ratioService.calculateRatioSum(recommendedRatio),
                ratioService.calculateRatioSum(formulatedRatio.ratio()),
                concentrationSum,
                fertilizerDoseKgHa,
                false,
                true,
                limitingNutrient.nutrient(),
                calculateCoveragePercent(providedN, providedP2O5, providedK2O, requiredN, requiredP2O5, requiredK2O),
                providedN,
                providedP2O5,
                providedK2O,
                calculateBalance(providedN, requiredN),
                calculateBalance(providedP2O5, requiredP2O5),
                calculateBalance(providedK2O, requiredK2O),
                calculateDeficit(providedN, requiredN),
                calculateDeficit(providedP2O5, requiredP2O5),
                calculateDeficit(providedK2O, requiredK2O),
                appendTechnicalMessage(baseTechnicalMessage, formulatedRatio.technicalMessage()));
    }

    private FormulatedFertilizerSelectionCandidate buildCandidate(
            FormulatedMineralFertilizerModel fertilizer,
            NPKrelation recommendedRatio,
            NPKrelation formulatedRatio,
            Double concentrationSum,
            Double fertilizerDoseKgHa,
            boolean approximateFallback,
            boolean maximizationFallback,
            String limitingNutrient,
            Double requiredN,
            Double requiredP2O5,
            Double requiredK2O,
            String technicalMessage) {
        double providedN = calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer != null ? fertilizer.getN() : null);
        double providedP2O5 = calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer != null ? fertilizer.getP2O5() : null);
        double providedK2O = calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer != null ? fertilizer.getK2O() : null);
        return new FormulatedFertilizerSelectionCandidate(
                fertilizer,
                formulatedRatio,
                ratioService.calculateRatioSum(recommendedRatio),
                ratioService.calculateRatioSum(formulatedRatio),
                concentrationSum,
                fertilizerDoseKgHa,
                approximateFallback,
                maximizationFallback,
                limitingNutrient,
                calculateCoveragePercent(providedN, providedP2O5, providedK2O, requiredN, requiredP2O5, requiredK2O),
                providedN,
                providedP2O5,
                providedK2O,
                calculateBalance(providedN, requiredN),
                calculateBalance(providedP2O5, requiredP2O5),
                calculateBalance(providedK2O, requiredK2O),
                calculateDeficit(providedN, requiredN),
                calculateDeficit(providedP2O5, requiredP2O5),
                calculateDeficit(providedK2O, requiredK2O),
                technicalMessage);
    }

    private boolean hasSupplyWithinTolerance(FormulatedMineralFertilizerModel fertilizer,
                                             Double fertilizerDoseKgHa,
                                             Double requiredN,
                                             Double requiredP2O5,
                                             Double requiredK2O) {
        if (fertilizer == null || fertilizerDoseKgHa == null || !Double.isFinite(fertilizerDoseKgHa)) {
            return false;
        }
        return isSuppliedWithinTolerance(calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer.getN()), requiredN)
                && isSuppliedWithinTolerance(calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer.getP2O5()), requiredP2O5)
                && isSuppliedWithinTolerance(calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer.getK2O()), requiredK2O);
    }

    private double calculateProvidedNutrient(Double fertilizerDoseKgHa, Double nutrientPercent) {
        if (fertilizerDoseKgHa == null || !Double.isFinite(fertilizerDoseKgHa)) {
            return 0d;
        }
        return fertilizerDoseKgHa * normalizeRequiredDose(nutrientPercent) / 100d;
    }

    private double calculateBalance(double supplied, Double required) {
        return round2(supplied - normalizeRequiredDose(required));
    }

    private double calculateDeficit(double supplied, Double required) {
        return round2(Math.max(0d, normalizeRequiredDose(required) - supplied));
    }

    private Double calculateCoveragePercent(FormulatedMineralFertilizerModel fertilizer,
                                            Double fertilizerDoseKgHa,
                                            Double requiredN,
                                            Double requiredP2O5,
                                            Double requiredK2O) {
        if (fertilizer == null || fertilizerDoseKgHa == null || !Double.isFinite(fertilizerDoseKgHa)) {
            return null;
        }
        return calculateCoveragePercent(
                calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer.getN()),
                calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer.getP2O5()),
                calculateProvidedNutrient(fertilizerDoseKgHa, fertilizer.getK2O()),
                requiredN,
                requiredP2O5,
                requiredK2O);
    }

    private Double calculateCoveragePercent(double providedN,
                                            double providedP2O5,
                                            double providedK2O,
                                            Double requiredN,
                                            Double requiredP2O5,
                                            Double requiredK2O) {
        double requiredSum = requiredNutrientSum(requiredN, requiredP2O5, requiredK2O);
        if (requiredSum <= 0d) {
            return null;
        }
        double covered = Math.min(providedN, normalizeRequiredDose(requiredN))
                + Math.min(providedP2O5, normalizeRequiredDose(requiredP2O5))
                + Math.min(providedK2O, normalizeRequiredDose(requiredK2O));
        return round2(covered / requiredSum * 100d);
    }

    private boolean isSuppliedWithinTolerance(double supplied, Double required) {
        double normalizedRequired = normalizeRequiredDose(required);
        if (normalizedRequired == 0d) {
            return supplied == 0d;
        }
        double minimum = normalizedRequired * (1d - APPROXIMATE_SUPPLY_TOLERANCE);
        double maximum = normalizedRequired * (1d + APPROXIMATE_SUPPLY_TOLERANCE);
        return supplied >= minimum && supplied <= maximum;
    }

    private boolean exceedsMaximumSupplyTolerance(double supplied, Double required) {
        double normalizedRequired = normalizeRequiredDose(required);
        if (normalizedRequired <= 0d) {
            return false;
        }
        return supplied > normalizedRequired * (1d + APPROXIMATE_SUPPLY_TOLERANCE);
    }

    private double calculateRatioDistance(NPKrelation recommendedRatio, NPKrelation formulatedRatio) {
        if (recommendedRatio == null || formulatedRatio == null) {
            return Double.MAX_VALUE;
        }
        double nDistance = recommendedRatio.getN() - formulatedRatio.getN();
        double pDistance = recommendedRatio.getP() - formulatedRatio.getP();
        double kDistance = recommendedRatio.getK() - formulatedRatio.getK();
        return Math.sqrt(nDistance * nDistance + pDistance * pDistance + kDistance * kDistance);
    }

    private double requiredNutrientSum(Double requiredN, Double requiredP2O5, Double requiredK2O) {
        return normalizeRequiredDose(requiredN)
                + normalizeRequiredDose(requiredP2O5)
                + normalizeRequiredDose(requiredK2O);
    }

    private Double calculateFertilizerDoseKgHa(double requiredNutrientSum, Double concentrationSum) {
        if (concentrationSum == null || concentrationSum <= 0d) {
            return null;
        }
        return 100d * requiredNutrientSum / concentrationSum;
    }

    private NutrientLimit findMostLimitingNutrient(FormulatedMineralFertilizerModel fertilizer,
                                                   Double requiredN,
                                                   Double requiredP2O5,
                                                   Double requiredK2O) {
        List<NutrientLimit> limits = new ArrayList<>();
        addNutrientLimit(limits, "N", requiredN, fertilizer != null ? fertilizer.getN() : null);
        addNutrientLimit(limits, "P2O5", requiredP2O5, fertilizer != null ? fertilizer.getP2O5() : null);
        addNutrientLimit(limits, "K2O", requiredK2O, fertilizer != null ? fertilizer.getK2O() : null);
        return limits.stream()
                .min(Comparator.comparing(NutrientLimit::doseKgHa)
                        .thenComparing(NutrientLimit::nutrient))
                .orElse(null);
    }

    private java.util.Optional<NutrientLimit> findPhosphorusLimit(FormulatedMineralFertilizerModel fertilizer, Double requiredP2O5) {
        double required = normalizeRequiredDose(requiredP2O5);
        double concentration = normalizeRequiredDose(fertilizer != null ? fertilizer.getP2O5() : null);
        if (required <= 0d || concentration <= 0d) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(new NutrientLimit("P2O5", required / concentration * 100d));
    }

    private void addNutrientLimit(List<NutrientLimit> limits, String nutrient, Double required, Double concentration) {
        if (normalizeRequiredDose(required) <= 0d || normalizeRequiredDose(concentration) <= 0d) {
            return;
        }
        limits.add(new NutrientLimit(nutrient, normalizeRequiredDose(required) / normalizeRequiredDose(concentration) * 100d));
    }

    private double normalizeRequiredDose(Double value) {
        if (value == null || !Double.isFinite(value) || value < 0d) {
            return 0d;
        }
        return value;
    }

    private boolean doesNotExceedCoveragePhosphorus(FormulatedMineralFertilizerModel fertilizer) {
        return normalizeRequiredDose(fertilizer != null ? fertilizer.getP2O5() : null) == 0d;
    }

    private FormulatedFertilizerSelectionCandidate appendCandidateTechnicalMessage(
            FormulatedFertilizerSelectionCandidate candidate,
            String technicalMessage) {
        if (candidate == null) {
            return null;
        }
        return new FormulatedFertilizerSelectionCandidate(
                candidate.formulated(),
                candidate.relation(),
                candidate.ratioSum(),
                candidate.formulatedRatioSum(),
                candidate.concentrationSum(),
                candidate.fertilizerDoseKgHa(),
                candidate.approximateFallback(),
                candidate.maximizationFallback(),
                candidate.limitingNutrient(),
                candidate.coveragePercent(),
                candidate.providedN(),
                candidate.providedP2O5(),
                candidate.providedK2O(),
                candidate.balanceN(),
                candidate.balanceP2O5(),
                candidate.balanceK2O(),
                candidate.deficitN(),
                candidate.deficitP2O5(),
                candidate.deficitK2O(),
                appendTechnicalMessage(candidate.technicalMessage(), technicalMessage));
    }

    private double round2(double value) {
        return Math.round(value * 100d) / 100d;
    }

    private double totalDeficit(Double deficitN, Double deficitP2O5, Double deficitK2O) {
        return normalizeRequiredDose(deficitN) + normalizeRequiredDose(deficitP2O5) + normalizeRequiredDose(deficitK2O);
    }

    private Comparator<FormulatedFertilizerSelectionCandidate> candidateComparator() {
        return Comparator
                .comparing(FormulatedFertilizerSelectionCandidate::fertilizerDoseKgHa,
                        Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(FormulatedFertilizerSelectionCandidate::concentrationSum,
                        Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(candidate -> candidate.formulated() != null ? candidate.formulated().getId() : null,
                        Comparator.nullsLast(Long::compareTo));
    }

    private String appendTechnicalMessage(String first, String second) {
        if (first == null || first.isBlank()) {
            return second;
        }
        if (second == null || second.isBlank()) {
            return first;
        }
        return first + " " + second;
    }

    private List<FormulatedFertilizerSelectionCandidate> limitForPresentation(
            List<FormulatedFertilizerSelectionCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        return candidates.stream().limit(PRESENTATION_LIMIT).toList();
    }

    private List<FormulatedMineralFertilizerModel> selectFormulatedFertilizers(UserModel user,
                                                                               FertilizerSourceOption sourceOption) {
        FertilizerSourceOption resolvedOption = sourceOption != null ? sourceOption : FertilizerSourceOption.BOTH;
        return switch (resolvedOption) {
            case PRIVATE -> formulatedMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByIdAsc(user);
            case PUBLIC -> formulatedMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByIdAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> formulatedMineralFertilizerRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> dedup(
                    dedup(formulatedMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByIdAsc(user),
                            formulatedMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByIdAsc(Cargo.USUARIO_SUPREMO),
                            FormulatedMineralFertilizerModel::getId),
                    formulatedMineralFertilizerRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO),
                    FormulatedMineralFertilizerModel::getId);
        };
    }

    private <T> List<T> dedup(List<T> first, List<T> second, Function<T, Long> id) {
        Map<Long, T> byId = new LinkedHashMap<>();
        List<T> withoutId = new ArrayList<>();
        addDedupItems(byId, withoutId, first, id);
        addDedupItems(byId, withoutId, second, id);
        withoutId.addAll(byId.values());
        return withoutId;
    }

    private <T> void addDedupItems(Map<Long, T> byId, List<T> withoutId, List<T> items, Function<T, Long> id) {
        if (items == null) return;
        for (T item : items) {
            Long itemId = id.apply(item);
            if (itemId == null) {
                withoutId.add(item);
            } else {
                byId.putIfAbsent(itemId, item);
            }
        }
    }

    public record FormulatedFertilizerSelectionCandidate(
            FormulatedMineralFertilizerModel formulated,
            NPKrelation relation,
            Double ratioSum,
            Double formulatedRatioSum,
            Double concentrationSum,
            Double fertilizerDoseKgHa,
            boolean approximateFallback,
            boolean maximizationFallback,
            String limitingNutrient,
            Double coveragePercent,
            Double providedN,
            Double providedP2O5,
            Double providedK2O,
            Double balanceN,
            Double balanceP2O5,
            Double balanceK2O,
            Double deficitN,
            Double deficitP2O5,
            Double deficitK2O,
            String technicalMessage) {
    }

    public record FormulatedFertilizerSelectionResult(
            List<FormulatedFertilizerSelectionCandidate> candidates,
            boolean fallbackUsed,
            String technicalMessage) {
    }

    private record ApproximateCandidate(
            FormulatedFertilizerSelectionCandidate selectionCandidate,
            double ratioDistance) {
    }

    private record NutrientLimit(String nutrient, double doseKgHa) {
    }
}
