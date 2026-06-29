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
            return new FormulatedFertilizerSelectionResult(
                    List.of(),
                    false,
                    appendTechnicalMessage(
                            noDirectMatchMessage,
                            "Nenhum formulado aproximado permaneceu dentro de +/-10% da recomendação para todos os nutrientes considerados."));
        }

        return new FormulatedFertilizerSelectionResult(candidates, false, recommendedRatio.technicalMessage());
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

        return new FormulatedFertilizerSelectionCandidate(
                fertilizer,
                formulatedRatio.ratio(),
                ratioService.calculateRatioSum(recommendedRatio),
                ratioService.calculateRatioSum(formulatedRatio.ratio()),
                concentrationSum,
                calculateFertilizerDoseKgHa(requiredNutrientSum(requiredN, requiredP2O5, requiredK2O), concentrationSum),
                false,
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

        Double fertilizerDoseKgHa = calculateFertilizerDoseKgHa(
                requiredNutrientSum(requiredN, requiredP2O5, requiredK2O),
                concentrationSum);
        if (fertilizerDoseKgHa == null
                || !hasSupplyWithinTolerance(fertilizer, fertilizerDoseKgHa, requiredN, requiredP2O5, requiredK2O)) {
            return null;
        }

        return new ApproximateCandidate(
                new FormulatedFertilizerSelectionCandidate(
                        fertilizer,
                        formulatedRatio.ratio(),
                        ratioService.calculateRatioSum(recommendedRatio),
                        ratioService.calculateRatioSum(formulatedRatio.ratio()),
                        concentrationSum,
                        fertilizerDoseKgHa,
                        true,
                        appendTechnicalMessage(baseTechnicalMessage, formulatedRatio.technicalMessage())),
                calculateRatioDistance(recommendedRatio, formulatedRatio.ratio()));
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
        return fertilizerDoseKgHa * normalizeRequiredDose(nutrientPercent) / 100d;
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

    private double normalizeRequiredDose(Double value) {
        if (value == null || !Double.isFinite(value) || value < 0d) {
            return 0d;
        }
        return value;
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
}
