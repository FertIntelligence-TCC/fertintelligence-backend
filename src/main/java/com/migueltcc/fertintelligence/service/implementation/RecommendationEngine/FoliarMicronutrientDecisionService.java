package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.ChelatedFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.ChelatedFertilizerRepository;
import com.migueltcc.fertintelligence.repository.MineralFertilizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoliarMicronutrientDecisionService {

    public static final double FOLIAR_LIMIT_KG_HA = 0.60d;
    public static final double CHELATE_COST_LIMIT = 1.25d;

    private final MineralFertilizerRepository mineralRepository;
    private final ChelatedFertilizerRepository chelatedRepository;

    public boolean isFoliarDose(double elementDoseKgHa) {
        return Double.isFinite(elementDoseKgHa) && elementDoseKgHa > 0d
                && elementDoseKgHa < FOLIAR_LIMIT_KG_HA;
    }

    public List<Alternative> calculate(UserModel user,
                                       FertilizerSourceOption sourceOption,
                                       AppliedMicronutrient micronutrient,
                                       double elementDoseKgHa) {
        if (!isFoliarDose(elementDoseKgHa) || micronutrient == null || micronutrient == AppliedMicronutrient.B) {
            return List.of();
        }
        Candidate mineral = bestMineral(user, sourceOption, micronutrient).orElse(null);
        Candidate chelated = bestChelated(user, sourceOption, micronutrient).orElse(null);
        Alternative mineralAlternative = alternative(micronutrient, elementDoseKgHa, SourceType.MINERAL_SIMPLE, mineral);
        Alternative chelatedAlternative = alternative(micronutrient, elementDoseKgHa, SourceType.CHELATED, chelated);
        Decision decision = decide(mineralAlternative, chelatedAlternative);
        return List.of(mineralAlternative.withState(decision.mineralState(), decision.message()),
                chelatedAlternative.withState(decision.chelatedState(), decision.message()));
    }

    static Decision decide(Alternative mineral, Alternative chelated) {
        if (mineral == null || chelated == null || mineral.costPerHa() == null || chelated.costPerHa() == null) {
            boolean neither = (mineral == null || mineral.costPerHa() == null)
                    && (chelated == null || chelated.costPerHa() == null);
            return new Decision(AlternativeState.UNDETERMINED, AlternativeState.UNDETERMINED,
                    neither
                            ? "Decisão econômica indeterminada por ausência de preços comerciais."
                            : "Decisão econômica indeterminada por ausência do preço da alternativa concorrente.");
        }
        double ratio = chelated.costPerHa() / mineral.costPerHa();
        if (ratio <= CHELATE_COST_LIMIT) {
            return new Decision(AlternativeState.NOT_SELECTED, AlternativeState.SELECTED,
                    "Quelato escolhido: CUMIC quelatado / CUMIC mineral = " + format(ratio) + " (limite 1,25).");
        }
        return new Decision(AlternativeState.SELECTED, AlternativeState.NOT_SELECTED,
                "Fonte mineral simples escolhida: CUMIC quelatado / CUMIC mineral = " + format(ratio)
                        + " (acima de 1,25).");
    }

    private Alternative alternative(AppliedMicronutrient micronutrient, double dose,
                                    SourceType type, Candidate candidate) {
        if (candidate == null) {
            return new Alternative(groupId(micronutrient), micronutrient, type, null, null, dose,
                    null, null, null, null, null, AlternativeState.UNDETERMINED, "Fonte elegível não cadastrada.");
        }
        double productDose = dose / (candidate.concentrationPercent() / 100d);
        CommercialUnit unit = cheapestUnit(candidate);
        Double pricePerProductKg = unit == null ? null
                : unit.price().divide(BigDecimal.valueOf(unit.massKg()), 12, RoundingMode.HALF_UP).doubleValue();
        Double pricePerNutrientKg = pricePerProductKg == null ? null
                : pricePerProductKg / (candidate.concentrationPercent() / 100d);
        Double costPerHa = pricePerProductKg == null ? null : productDose * pricePerProductKg;
        return new Alternative(groupId(micronutrient), micronutrient, type, candidate.name(),
                candidate.concentrationPercent(), dose, productDose,
                unit != null ? unit.label() : null, pricePerProductKg, pricePerNutrientKg, costPerHa,
                AlternativeState.UNDETERMINED, null);
    }

    private Optional<Candidate> bestMineral(UserModel user, FertilizerSourceOption option,
                                            AppliedMicronutrient micronutrient) {
        return minerals(user, option).stream()
                .map(value -> candidate(value.getName(), concentration(value, micronutrient),
                        value.getPrecoUnidadeComercial(), value.getUnidadeComercial(), value.getPesoUnidadeComercialKg(),
                        value.getPrecoSaco5Kg(), value.getPrecoSaco25Kg(), value.getPrecoSaco50Kg(), value.getPrecoSaco1000Kg()))
                .filter(candidate -> candidate != null && isPreferredMineral(candidate.name()))
                .max(Comparator.comparingDouble(Candidate::concentrationPercent))
                .or(() -> minerals(user, option).stream()
                        .map(value -> candidate(value.getName(), concentration(value, micronutrient),
                                value.getPrecoUnidadeComercial(), value.getUnidadeComercial(), value.getPesoUnidadeComercialKg(),
                                value.getPrecoSaco5Kg(), value.getPrecoSaco25Kg(), value.getPrecoSaco50Kg(), value.getPrecoSaco1000Kg()))
                        .filter(java.util.Objects::nonNull)
                        .max(Comparator.comparingDouble(Candidate::concentrationPercent)));
    }

    private Optional<Candidate> bestChelated(UserModel user, FertilizerSourceOption option,
                                             AppliedMicronutrient micronutrient) {
        return chelated(user, option).stream()
                .map(value -> candidate(value.getName(), concentration(value, micronutrient),
                        value.getPrecoUnidadeComercial(), value.getUnidadeComercial(), value.getPesoUnidadeComercialKg(),
                        value.getPrecoSaco5Kg(), value.getPrecoSaco25Kg(), value.getPrecoSaco50Kg(), value.getPrecoSaco1000Kg()))
                .filter(java.util.Objects::nonNull)
                .max(Comparator.comparingDouble(Candidate::concentrationPercent));
    }

    private Candidate candidate(String name, Double concentration, BigDecimal price, String unit, Double mass,
                                BigDecimal p5, BigDecimal p25, BigDecimal p50, BigDecimal p1000) {
        if (name == null || concentration == null || !Double.isFinite(concentration) || concentration <= 0d) return null;
        return new Candidate(name, concentration, price, unit, mass, p5, p25, p50, p1000);
    }

    private CommercialUnit cheapestUnit(Candidate candidate) {
        List<CommercialUnit> units = new ArrayList<>();
        if (candidate.commercialPrice() != null && candidate.commercialPrice().signum() > 0
                && candidate.commercialMassKg() != null && candidate.commercialMassKg() > 0d) {
            units.add(new CommercialUnit(candidate.commercialMassKg(), candidate.commercialPrice(),
                    candidate.commercialUnit() == null || candidate.commercialUnit().isBlank()
                            ? "unidade comercial" : candidate.commercialUnit()));
        }
        addUnit(units, 5d, candidate.p5(), "saco 5 kg");
        addUnit(units, 25d, candidate.p25(), "saco 25 kg");
        addUnit(units, 50d, candidate.p50(), "saco 50 kg");
        addUnit(units, 1000d, candidate.p1000(), "t");
        return units.stream().min(Comparator.comparing(unit ->
                unit.price().divide(BigDecimal.valueOf(unit.massKg()), 12, RoundingMode.HALF_UP))).orElse(null);
    }

    private void addUnit(List<CommercialUnit> units, double mass, BigDecimal price, String label) {
        if (price != null && price.signum() > 0) units.add(new CommercialUnit(mass, price, label));
    }

    private List<MineralFertilizerModel> minerals(UserModel user, FertilizerSourceOption value) {
        FertilizerSourceOption option = value == null ? FertilizerSourceOption.BOTH : value;
        return switch (option) {
            case PRIVATE -> mineralRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user);
            case PUBLIC -> mineralRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> mineralRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> merge(mineralRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user),
                    mineralRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO),
                    mineralRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO));
        };
    }

    private List<ChelatedFertilizerModel> chelated(UserModel user, FertilizerSourceOption value) {
        FertilizerSourceOption option = value == null ? FertilizerSourceOption.BOTH : value;
        return switch (option) {
            case PRIVATE -> chelatedRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user);
            case PUBLIC -> chelatedRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> chelatedRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> merge(chelatedRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user),
                    chelatedRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO),
                    chelatedRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO));
        };
    }

    @SafeVarargs
    private final <T> List<T> merge(List<T>... values) {
        List<T> result = new ArrayList<>();
        for (List<T> list : values) if (list != null) result.addAll(list);
        return result;
    }

    private boolean isPreferredMineral(String name) {
        String normalized = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase();
        return normalized.contains("sulfat");
    }

    private static Double concentration(MineralFertilizerModel value, AppliedMicronutrient nutrient) {
        return concentration(nutrient, value.getCu(), value.getFe(), value.getMn(), value.getZn());
    }

    private static Double concentration(ChelatedFertilizerModel value, AppliedMicronutrient nutrient) {
        return concentration(nutrient, value.getCu(), value.getFe(), value.getMn(), value.getZn());
    }

    private static Double concentration(AppliedMicronutrient nutrient, Double cu, Double fe, Double mn, Double zn) {
        return switch (nutrient) {
            case Cu -> cu;
            case Fe -> fe;
            case Mn -> mn;
            case Zn -> zn;
            default -> null;
        };
    }

    private static String groupId(AppliedMicronutrient micronutrient) {
        return "FOLIAR_" + micronutrient.name();
    }

    private static String format(double value) {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    public enum SourceType { MINERAL_SIMPLE, CHELATED }
    public enum AlternativeState { SELECTED, NOT_SELECTED, UNDETERMINED }

    public record Alternative(String strategyGroupId, AppliedMicronutrient micronutrient, SourceType sourceType,
                              String product, Double concentrationPercent, Double elementDoseKgHa,
                              Double productDoseKgHa, String commercialUnit, Double pricePerProductKg,
                              Double pricePerNutrientKg, Double costPerHa, AlternativeState state,
                              String decisionMessage) {
        Alternative withState(AlternativeState newState, String message) {
            return new Alternative(strategyGroupId, micronutrient, sourceType, product, concentrationPercent,
                    elementDoseKgHa, productDoseKgHa, commercialUnit, pricePerProductKg, pricePerNutrientKg,
                    costPerHa, newState, message);
        }
    }

    record Decision(AlternativeState mineralState, AlternativeState chelatedState, String message) {}
    private record Candidate(String name, double concentrationPercent, BigDecimal commercialPrice,
                             String commercialUnit, Double commercialMassKg, BigDecimal p5, BigDecimal p25,
                             BigDecimal p50, BigDecimal p1000) {}
    private record CommercialUnit(double massKg, BigDecimal price, String label) {}
}
