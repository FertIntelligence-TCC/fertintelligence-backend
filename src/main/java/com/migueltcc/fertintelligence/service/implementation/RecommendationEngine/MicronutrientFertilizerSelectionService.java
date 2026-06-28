package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
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
public class MicronutrientFertilizerSelectionService {

    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;

    public MicronutrientFertilizerSelectionService(SimpleMineralFertilizerRepository simpleMineralFertilizerRepository) {
        this.simpleMineralFertilizerRepository = simpleMineralFertilizerRepository;
    }

    public List<MicronutrientFertilizerSelectionResult> select(UserModel user,
                                                               FertilizerSourceOption sourceOption,
                                                               Map<AppliedMicronutrient, Double> micronutrientDosesKgHa) {
        return select(selectSimpleFertilizers(user, sourceOption), micronutrientDosesKgHa);
    }

    public List<MicronutrientFertilizerSelectionResult> select(List<SimpleMineralFertilizerModel> fertilizers,
                                                               Map<AppliedMicronutrient, Double> micronutrientDosesKgHa) {
        if (micronutrientDosesKgHa == null || micronutrientDosesKgHa.isEmpty()) {
            return List.of();
        }
        List<MicronutrientFertilizerSelectionResult> results = new ArrayList<>();
        addIfPresent(results, micronutrientDosesKgHa, AppliedMicronutrient.B, fertilizers);
        addIfPresent(results, micronutrientDosesKgHa, AppliedMicronutrient.Cu, fertilizers);
        addIfPresent(results, micronutrientDosesKgHa, AppliedMicronutrient.Fe, fertilizers);
        addIfPresent(results, micronutrientDosesKgHa, AppliedMicronutrient.Mn, fertilizers);
        addIfPresent(results, micronutrientDosesKgHa, AppliedMicronutrient.Mo, fertilizers);
        addIfPresent(results, micronutrientDosesKgHa, AppliedMicronutrient.Zn, fertilizers);
        return results;
    }

    public MicronutrientFertilizerSelectionResult selectBoron(Double doseKgHa, List<SimpleMineralFertilizerModel> fertilizers) {
        return selectOne(AppliedMicronutrient.B, doseKgHa, fertilizers);
    }

    public MicronutrientFertilizerSelectionResult selectCopper(Double doseKgHa, List<SimpleMineralFertilizerModel> fertilizers) {
        return selectOne(AppliedMicronutrient.Cu, doseKgHa, fertilizers);
    }

    public MicronutrientFertilizerSelectionResult selectIron(Double doseKgHa, List<SimpleMineralFertilizerModel> fertilizers) {
        return selectOne(AppliedMicronutrient.Fe, doseKgHa, fertilizers);
    }

    public MicronutrientFertilizerSelectionResult selectManganese(Double doseKgHa, List<SimpleMineralFertilizerModel> fertilizers) {
        return selectOne(AppliedMicronutrient.Mn, doseKgHa, fertilizers);
    }

    public MicronutrientFertilizerSelectionResult selectMolybdenum(Double doseKgHa, List<SimpleMineralFertilizerModel> fertilizers) {
        return selectOne(AppliedMicronutrient.Mo, doseKgHa, fertilizers);
    }

    public MicronutrientFertilizerSelectionResult selectZinc(Double doseKgHa, List<SimpleMineralFertilizerModel> fertilizers) {
        return selectOne(AppliedMicronutrient.Zn, doseKgHa, fertilizers);
    }

    private void addIfPresent(List<MicronutrientFertilizerSelectionResult> results,
                              Map<AppliedMicronutrient, Double> doses,
                              AppliedMicronutrient micronutrient,
                              List<SimpleMineralFertilizerModel> fertilizers) {
        if (doses.containsKey(micronutrient)) {
            results.add(selectOne(micronutrient, doses.get(micronutrient), fertilizers));
        }
    }

    private MicronutrientFertilizerSelectionResult selectOne(AppliedMicronutrient micronutrient,
                                                             Double doseKgHa,
                                                             List<SimpleMineralFertilizerModel> fertilizers) {
        double recommendedDose = nvl(doseKgHa);
        if (recommendedDose <= 0d) {
            return new MicronutrientFertilizerSelectionResult(
                    micronutrient, round2(recommendedDose), null, null, null,
                    "Dose de micronutriente ausente ou igual a zero; fonte sólida não calculada.");
        }

        SimpleMineralFertilizerModel selected = fertilizers == null ? null : fertilizers.stream()
                .filter(f -> concentration(f, micronutrient) > 0d)
                .max(Comparator.comparing((SimpleMineralFertilizerModel f) -> concentration(f, micronutrient))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()))
                .orElse(null);

        if (selected == null) {
            return new MicronutrientFertilizerSelectionResult(
                    micronutrient, round2(recommendedDose), null, null, null,
                    "Sem adubo mineral simples sólido cadastrado com teor positivo de " + micronutrient.name() + ".");
        }

        double concentrationPercent = concentration(selected, micronutrient);
        if (concentrationPercent <= 0d) {
            return new MicronutrientFertilizerSelectionResult(
                    micronutrient, round2(recommendedDose), selected, round2(concentrationPercent), null,
                    "Adubo selecionado sem teor positivo de " + micronutrient.name() + "; dose de produto não calculada.");
        }

        double fertilizerDoseKgHa = round2(100d * recommendedDose / concentrationPercent);
        return new MicronutrientFertilizerSelectionResult(
                micronutrient, round2(recommendedDose), selected, round2(concentrationPercent), fertilizerDoseKgHa, null);
    }

    private double concentration(SimpleMineralFertilizerModel fertilizer, AppliedMicronutrient micronutrient) {
        if (fertilizer == null || micronutrient == null) return 0d;
        return switch (micronutrient) {
            case B -> nvl(fertilizer.getB());
            case Cu -> nvl(fertilizer.getCu());
            case Fe -> nvl(fertilizer.getFe());
            case Mn -> nvl(fertilizer.getMn());
            case Mo -> nvl(fertilizer.getMo());
            case Zn -> nvl(fertilizer.getZn());
            default -> 0d;
        };
    }

    private List<SimpleMineralFertilizerModel> selectSimpleFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
        FertilizerSourceOption resolvedOption = sourceOption != null ? sourceOption : FertilizerSourceOption.BOTH;
        return switch (resolvedOption) {
            case PRIVATE -> simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user);
            case PUBLIC -> simpleMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> dedup(
                    dedup(simpleMineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user),
                            simpleMineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO),
                            SimpleMineralFertilizerModel::getId),
                    simpleMineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO),
                    SimpleMineralFertilizerModel::getId);
        };
    }

    private double nvl(Double value) {
        return value == null ? 0d : value;
    }

    private double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
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

    public record MicronutrientFertilizerSelectionResult(
            AppliedMicronutrient micronutrient,
            Double micronutrientDoseKgHa,
            SimpleMineralFertilizerModel selectedFertilizer,
            Double selectedConcentrationPercent,
            Double fertilizerDoseKgHa,
            String technicalMessage) {

        public boolean calculated() {
            return fertilizerDoseKgHa != null;
        }
    }
}
