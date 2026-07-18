package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.FormulatedMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.MineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ShoppingInputCostService {

    private static final Pattern NPK = Pattern.compile("(?:npk\\s*)?(\\d+(?:[.,]\\d+)?)\\s*[-–]\\s*(\\d+(?:[.,]\\d+)?)\\s*[-–]\\s*(\\d+(?:[.,]\\d+)?)", Pattern.CASE_INSENSITIVE);

    private final SimpleMineralFertilizerRepository simpleRepository;
    private final FormulatedMineralFertilizerRepository formulatedRepository;
    private final MineralFertilizerRepository mineralRepository;

    public CostEstimate estimate(RecommendationModel recommendation, String inputName, Double doseKgHa, Double areaHa) {
        if (!positive(doseKgHa)) return CostEstimate.unpriced(doseKgHa, areaHa);
        Optional<CommercialUnit> unit = resolveCommercialUnit(recommendation, inputName);
        if (unit.isEmpty()) return CostEstimate.unpriced(doseKgHa, areaHa);
        return calculate(doseKgHa, areaHa, unit.get());
    }

    static CostEstimate calculate(Double doseKgHa, Double areaHa, CommercialUnit unit) {
        if (!positive(doseKgHa) || unit == null || !positive(unit.massKg()) || unit.price() == null
                || unit.price().compareTo(BigDecimal.ZERO) <= 0) {
            return CostEstimate.unpriced(doseKgHa, areaHa);
        }
        BigDecimal dose = BigDecimal.valueOf(doseKgHa);
        BigDecimal mass = BigDecimal.valueOf(unit.massKg());
        BigDecimal costPerHa = dose.divide(mass, 12, RoundingMode.HALF_UP).multiply(unit.price());
        Double theoreticalKg = positive(areaHa) ? doseKgHa * areaHa : null;
        BigDecimal commercialQuantity = theoreticalKg != null
                ? BigDecimal.valueOf(theoreticalKg).divide(mass, 12, RoundingMode.HALF_UP) : null;
        BigDecimal totalCost = commercialQuantity != null ? commercialQuantity.multiply(unit.price()) : null;
        return new CostEstimate(doseKgHa, theoreticalKg, unit.massKg(), unit.price(), unit.symbol(),
                costPerHa.doubleValue(), commercialQuantity != null ? commercialQuantity.doubleValue() : null,
                totalCost != null ? totalCost.doubleValue() : null, true);
    }

    private Optional<CommercialUnit> resolveCommercialUnit(RecommendationModel recommendation, String inputName) {
        if (inputName == null || inputName.isBlank()) return Optional.empty();
        List<CommercialUnit> units = new ArrayList<>();
        for (SimpleMineralFertilizerModel value : simpleFertilizers(recommendation)) {
            if (sameName(inputName, value.getName())) addUnits(units, value.getPrecoSaco5Kg(), value.getPrecoSaco25Kg(), value.getPrecoSaco50Kg(), value.getPrecoSaco1000Kg());
        }
        for (MineralFertilizerModel value : mineralFertilizers(recommendation)) {
            if (sameName(inputName, value.getName())) addUnits(units, value.getPrecoSaco5Kg(), value.getPrecoSaco25Kg(), value.getPrecoSaco50Kg(), value.getPrecoSaco1000Kg());
        }
        double[] formula = parseFormula(inputName);
        if (formula != null) {
            List<FormulatedMineralFertilizerModel> matches = formulatedFertilizers(recommendation).stream()
                    .filter(value -> same(formula[0], value.getN()) && same(formula[1], value.getP2O5()) && same(formula[2], value.getK2O()))
                    .toList();
            if (hasSingleCompleteFormulaIdentity(matches)) {
                for (FormulatedMineralFertilizerModel value : matches) {
                    addUnits(units, value.getPrecoSaco5Kg(), value.getPrecoSaco25Kg(), value.getPrecoSaco50Kg(), value.getPrecoSaco1000Kg());
                }
            }
        }
        return units.stream().min(Comparator.comparing(unit -> unit.price().divide(BigDecimal.valueOf(unit.massKg()), 12, RoundingMode.HALF_UP)));
    }

    private List<SimpleMineralFertilizerModel> simpleFertilizers(RecommendationModel recommendation) {
        FertilizerSourceOption option = option(recommendation);
        return safeList(switch (option) {
            case PRIVATE -> simpleRepository.findAllByUserAndPublicoFalseOrderByNameAsc(recommendation.getCreator());
            case PUBLIC -> simpleRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> simpleRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> merge(simpleRepository.findAllByUserAndPublicoFalseOrderByNameAsc(recommendation.getCreator()),
                    simpleRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO),
                    simpleRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO));
        });
    }

    private List<FormulatedMineralFertilizerModel> formulatedFertilizers(RecommendationModel recommendation) {
        FertilizerSourceOption option = option(recommendation);
        return safeList(switch (option) {
            case PRIVATE -> formulatedRepository.findAllByUserAndPublicoFalseOrderByIdAsc(recommendation.getCreator());
            case PUBLIC -> formulatedRepository.findAllByPublicoTrueAndUser_CargoNotOrderByIdAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> formulatedRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> merge(formulatedRepository.findAllByUserAndPublicoFalseOrderByIdAsc(recommendation.getCreator()),
                    formulatedRepository.findAllByPublicoTrueAndUser_CargoNotOrderByIdAsc(Cargo.USUARIO_SUPREMO),
                    formulatedRepository.findAllByUser_CargoOrderByIdAsc(Cargo.USUARIO_SUPREMO));
        });
    }

    private List<MineralFertilizerModel> mineralFertilizers(RecommendationModel recommendation) {
        FertilizerSourceOption option = option(recommendation);
        return safeList(switch (option) {
            case PRIVATE -> mineralRepository.findAllByUserAndPublicoFalseOrderByNameAsc(recommendation.getCreator());
            case PUBLIC -> mineralRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> mineralRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> merge(mineralRepository.findAllByUserAndPublicoFalseOrderByNameAsc(recommendation.getCreator()),
                    mineralRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO),
                    mineralRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO));
        });
    }

    private FertilizerSourceOption option(RecommendationModel recommendation) {
        return recommendation != null && recommendation.getOrigemAdubos() != null ? recommendation.getOrigemAdubos() : FertilizerSourceOption.BOTH;
    }

    @SafeVarargs private final <T> List<T> merge(List<T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<T> list : lists) if (list != null) result.addAll(list);
        return result;
    }

    private <T> List<T> safeList(List<T> values) { return values != null ? values : List.of(); }

    private void addUnits(List<CommercialUnit> target, BigDecimal p5, BigDecimal p25, BigDecimal p50, BigDecimal p1000) {
        addUnit(target, 5d, p5, "sc"); addUnit(target, 25d, p25, "sc");
        addUnit(target, 50d, p50, "sc"); addUnit(target, 1000d, p1000, "t");
    }

    private void addUnit(List<CommercialUnit> target, double mass, BigDecimal price, String symbol) {
        if (price != null && price.compareTo(BigDecimal.ZERO) > 0) target.add(new CommercialUnit(mass, price, symbol));
    }

    private boolean sameName(String left, String right) { return normalize(left).equals(normalize(right)); }
    private boolean hasSingleCompleteFormulaIdentity(List<FormulatedMineralFertilizerModel> values) {
        if (values == null || values.isEmpty()) return false;
        FormulatedMineralFertilizerModel first = values.get(0);
        return values.stream().allMatch(value -> sameAdditionalNutrients(first, value));
    }
    private boolean sameAdditionalNutrients(FormulatedMineralFertilizerModel left, FormulatedMineralFertilizerModel right) {
        return sameNullable(left.getCa(), right.getCa()) && sameNullable(left.getMg(), right.getMg())
                && sameNullable(left.getS(), right.getS()) && sameNullable(left.getB(), right.getB())
                && sameNullable(left.getCu(), right.getCu()) && sameNullable(left.getFe(), right.getFe())
                && sameNullable(left.getMn(), right.getMn()) && sameNullable(left.getMo(), right.getMo())
                && sameNullable(left.getZn(), right.getZn());
    }
    private boolean sameNullable(Double left, Double right) {
        return Math.abs((left == null ? 0d : left) - (right == null ? 0d : right)) <= 1e-6;
    }
    private String normalize(String value) { return Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).trim(); }
    private boolean same(double left, Double right) { return right != null && Double.isFinite(right) && Math.abs(left - right) <= 1e-6; }
    private double[] parseFormula(String value) {
        Matcher matcher = NPK.matcher(value == null ? "" : value);
        if (!matcher.find()) return null;
        return new double[]{number(matcher.group(1)), number(matcher.group(2)), number(matcher.group(3))};
    }
    private double number(String value) { return Double.parseDouble(value.replace(',', '.')); }
    private static boolean positive(Double value) { return value != null && Double.isFinite(value) && value > 0d; }

    record CommercialUnit(double massKg, BigDecimal price, String symbol) { }
    public record CostEstimate(Double doseKgHa, Double theoreticalKg, Double commercialUnitMassKg,
                               BigDecimal commercialUnitPrice, String commercialUnitSymbol,
                               Double estimatedCostPerHa, Double theoreticalCommercialQuantity,
                               Double estimatedTotalCost, boolean priced) {
        static CostEstimate unpriced(Double dose, Double area) {
            return new CostEstimate(dose, positive(area) && positive(dose) ? dose * area : null,
                    null, null, null, null, null, null, false);
        }
    }
}
