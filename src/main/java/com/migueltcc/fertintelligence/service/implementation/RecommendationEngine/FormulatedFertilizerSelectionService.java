package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.FormulatedMineralFertilizerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class FormulatedFertilizerSelectionService {

    private static final int PRESENTATION_LIMIT = 2;

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
        if (!recommendedRatio.calculated() || fertilizers == null || fertilizers.isEmpty()) {
            return new FormulatedFertilizerSelectionResult(
                    List.of(),
                    false,
                    recommendedRatio.technicalMessage() != null
                            ? recommendedRatio.technicalMessage()
                            : "Nenhum adubo formulado aplicável foi informado para seleção.");
        }

        double recommendedSum = positiveValue(requiredN) + positiveValue(requiredP2O5) + positiveValue(requiredK2O);
        Double recommendedRatioSum = ratioService.calculateRatioSum(recommendedRatio.ratio());
        List<FormulatedFertilizerSelectionCandidate> directMatches =
                selectDirectMatches(fertilizers, recommendedRatio.ratio(), recommendedRatioSum, recommendedSum);
        if (!directMatches.isEmpty()) {
            return new FormulatedFertilizerSelectionResult(directMatches, false, null);
        }

        List<FormulatedFertilizerSelectionCandidate> fallbackMatches =
                selectFallbackByApproximation(fertilizers, recommendedRatio.ratio(), recommendedRatioSum, recommendedSum);
        if (fallbackMatches.isEmpty()) {
            return new FormulatedFertilizerSelectionResult(
                    List.of(),
                    true,
                    "Nenhum adubo formulado aplicável foi encontrado: produtos sem N, P2O5 e K2O válidos ou com soma de concentração zero foram ignorados.");
        }

        return new FormulatedFertilizerSelectionResult(
                fallbackMatches,
                true,
                "Sem correspondência direta da relação N-P2O5-K2O recomendada; foi usado fallback por aproximação pelo somatório da relação ou das concentrações normalizadas.");
    }

    private FormulatedFertilizerSelectionCandidate toCandidate(FormulatedMineralFertilizerModel fertilizer,
                                                              NPKrelation recommendedRatio,
                                                              Double recommendedRatioSum,
                                                              double recommendedSum) {
        FormulatedFertilizerRatioService.RatioCalculationResult formulatedRatio =
                ratioService.calculateFormulatedRatio(fertilizer);
        if (!formulatedRatio.calculated()) {
            return null;
        }

        Double concentrationSum = ratioService.calculateFormulatedConcentrationSum(fertilizer);
        if (concentrationSum == null || concentrationSum <= 0d) {
            return null;
        }

        Double formulatedRatioSum = ratioService.calculateRatioSum(formulatedRatio.ratio());
        Double fertilizerDoseKgHa = calculateDoseKgHa(recommendedSum, concentrationSum);
        return new FormulatedFertilizerSelectionCandidate(
                fertilizer,
                formulatedRatio.ratio(),
                recommendedRatioSum,
                formulatedRatioSum,
                concentrationSum,
                fertilizerDoseKgHa,
                false,
                null);
    }

    private List<FormulatedFertilizerSelectionCandidate> selectDirectMatches(List<FormulatedMineralFertilizerModel> fertilizers,
                                                                             NPKrelation recommendedRatio,
                                                                             Double recommendedRatioSum,
                                                                             double recommendedSum) {
        return fertilizers.stream()
                .filter(this::hasValidNpkConcentrations)
                .map(fertilizer -> toCandidate(fertilizer, recommendedRatio, recommendedRatioSum, recommendedSum))
                .filter(candidate -> candidate != null
                        && ratioService.hasCompleteRatioMatch(recommendedRatio, candidate.relation()))
                .sorted(doseOrdering())
                .toList();
    }

    private List<FormulatedFertilizerSelectionCandidate> selectFallbackByApproximation(List<FormulatedMineralFertilizerModel> fertilizers,
                                                                                       NPKrelation recommendedRatio,
                                                                                       Double recommendedRatioSum,
                                                                                       double recommendedSum) {
        if (recommendedRatioSum == null || recommendedRatioSum <= 0d) {
            return List.of();
        }

        List<FormulatedFertilizerSelectionCandidate> approximateCandidates = fertilizers.stream()
                .filter(this::hasValidNpkConcentrations)
                .map(fertilizer -> toCandidate(fertilizer, recommendedRatio, recommendedRatioSum, recommendedSum))
                .filter(candidate -> candidate != null)
                .sorted(approximationOrdering(recommendedRatioSum))
                .limit(PRESENTATION_LIMIT)
                .map(this::markAsFallback)
                .toList();

        return approximateCandidates.stream()
                .sorted(doseOrdering())
                .toList();
    }

    private FormulatedFertilizerSelectionCandidate markAsFallback(FormulatedFertilizerSelectionCandidate candidate) {
        return new FormulatedFertilizerSelectionCandidate(
                candidate.formulated(),
                candidate.relation(),
                candidate.ratioSum(),
                candidate.formulatedRatioSum(),
                candidate.concentrationSum(),
                candidate.fertilizerDoseKgHa(),
                true,
                "Fallback por aproximação: sem relação N-P2O5-K2O direta, seleção pelo somatório da relação ou das concentrações normalizadas mais próximo do recomendado.");
    }

    private Comparator<FormulatedFertilizerSelectionCandidate> approximationOrdering(Double recommendedRatioSum) {
        return Comparator
                .comparing((FormulatedFertilizerSelectionCandidate candidate) -> approximationDistance(candidate, recommendedRatioSum))
                .thenComparing(FormulatedFertilizerSelectionCandidate::concentrationSum,
                        Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(candidate -> fertilizerName(candidate.formulated()), Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(candidate -> candidate.formulated().getId(), Comparator.nullsLast(Long::compareTo));
    }

    private double approximationDistance(FormulatedFertilizerSelectionCandidate candidate, Double recommendedRatioSum) {
        if (candidate == null || candidate.formulatedRatioSum() == null || recommendedRatioSum == null) {
            return Double.MAX_VALUE;
        }
        return Math.abs(candidate.formulatedRatioSum() - recommendedRatioSum);
    }

    private Comparator<FormulatedFertilizerSelectionCandidate> doseOrdering() {
        return Comparator
                .comparing(FormulatedFertilizerSelectionCandidate::fertilizerDoseKgHa,
                        Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(FormulatedFertilizerSelectionCandidate::concentrationSum,
                        Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(candidate -> fertilizerName(candidate.formulated()), Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(candidate -> candidate.formulated().getId(), Comparator.nullsLast(Long::compareTo));
    }

    private Double calculateDoseKgHa(double recommendedSum, Double concentrationSum) {
        if (recommendedSum <= 0d || concentrationSum == null || concentrationSum <= 0d) {
            return null;
        }
        return round2(100d * recommendedSum / concentrationSum);
    }

    private boolean hasValidNpkConcentrations(FormulatedMineralFertilizerModel fertilizer) {
        return fertilizer != null
                && isValidConcentration(fertilizer.getN())
                && isValidConcentration(fertilizer.getP2O5())
                && isValidConcentration(fertilizer.getK2O())
                && fertilizer.getN() >= 0d
                && fertilizer.getP2O5() >= 0d
                && fertilizer.getK2O() >= 0d
                && fertilizer.getN() + fertilizer.getP2O5() + fertilizer.getK2O() > 0d;
    }

    private boolean isValidConcentration(Double value) {
        return value != null && Double.isFinite(value) && value >= 0d;
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

    private double positiveValue(Double value) {
        return value == null || !Double.isFinite(value) || value < 0d ? 0d : value;
    }

    private double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private String fertilizerName(FormulatedMineralFertilizerModel fertilizer) {
        if (fertilizer == null) {
            return null;
        }
        return "NPK " + round2(fertilizer.getN()) + "-" + round2(fertilizer.getP2O5()) + "-" + round2(fertilizer.getK2O());
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
}
