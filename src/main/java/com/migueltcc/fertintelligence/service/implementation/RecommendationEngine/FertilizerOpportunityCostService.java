package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.FormulatedMineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.MineralFertilizerRepository;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class FertilizerOpportunityCostService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal MIX_FACTOR = BigDecimal.valueOf(1.1);

    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;
    private final FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository;
    private final MineralFertilizerRepository mineralFertilizerRepository;

    public FertilizerOpportunityCostService(SimpleMineralFertilizerRepository simpleMineralFertilizerRepository,
                                            FormulatedMineralFertilizerRepository formulatedMineralFertilizerRepository,
                                            MineralFertilizerRepository mineralFertilizerRepository) {
        this.simpleMineralFertilizerRepository = simpleMineralFertilizerRepository;
        this.formulatedMineralFertilizerRepository = formulatedMineralFertilizerRepository;
        this.mineralFertilizerRepository = mineralFertilizerRepository;
    }

    public OpportunityCostResult calculate(UserModel user,
                                           FertilizerSourceOption sourceOption,
                                           List<Long> formulatedFertilizerIds,
                                           List<String> warnings) {
        List<SimpleMineralFertilizerModel> simpleFertilizers = selectSimpleFertilizers(user, sourceOption);
        List<FormulatedMineralFertilizerModel> formulatedFertilizers = selectFormulatedFertilizers(user, sourceOption);
        List<MineralFertilizerModel> mineralFertilizers = selectMineralFertilizers(user, sourceOption);

        Map<Nutrient, NutrientPrice> nutrientPrices = calculateNutrientPrices(simpleFertilizers, mineralFertilizers);
        List<OpportunityCostDecision> decisions = new ArrayList<>();

        List<FormulatedMineralFertilizerModel> selectedFormulated = selectedFormulatedFertilizers(formulatedFertilizers, formulatedFertilizerIds);
        for (FormulatedMineralFertilizerModel fertilizer : selectedFormulated) {
            decisions.add(decideFormulated(fertilizer, nutrientPrices));
        }

        List<MineralFertilizerModel> fteCandidates = mineralFertilizers.stream()
                .filter(this::hasMicronutrientsOnly)
                .filter(fertilizer -> bestCommercialUnit(fertilizer.getPrecoSaco5Kg(), fertilizer.getPrecoSaco25Kg(),
                        fertilizer.getPrecoSaco50Kg(), fertilizer.getPrecoSaco1000Kg()).isPresent())
                .sorted(Comparator.comparing(MineralFertilizerModel::getName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(f -> f.getId() == null ? Long.MAX_VALUE : f.getId()))
                .limit(3)
                .toList();
        for (MineralFertilizerModel fertilizer : fteCandidates) {
            decisions.add(decideFte(fertilizer, nutrientPrices));
        }

        List<String> technicalWarnings = new ArrayList<>();
        if (nutrientPrices.isEmpty()) {
            technicalWarnings.add("Custo de oportunidade indeterminado por ausência de preços comerciais válidos nos adubos simples/concentrados.");
        }
        for (OpportunityCostDecision decision : decisions) {
            if (decision.indeterminate() && decision.justification() != null && !decision.justification().isBlank()) {
                technicalWarnings.add(decision.justification());
            }
        }
        if (warnings != null) {
            warnings.addAll(technicalWarnings);
        }
        return new OpportunityCostResult(nutrientPrices, decisions, technicalWarnings);
    }

    Optional<SimpleMineralFertilizerModel> selectLowestCostSimpleSource(
            List<SimpleMineralFertilizerModel> fertilizers, Nutrient nutrient) {
        if (fertilizers == null || fertilizers.isEmpty() || nutrient == null) return Optional.empty();
        NutrientPrice selected = calculateNutrientPrices(fertilizers, List.of()).get(nutrient);
        if (selected == null) return Optional.empty();
        return fertilizers.stream()
                .filter(fertilizer -> fertilizer != null && fertilizer.getName() != null)
                .filter(fertilizer -> fertilizer.getName().equals(selected.fertilizerName()))
                .findFirst();
    }

    private Map<Nutrient, NutrientPrice> calculateNutrientPrices(List<SimpleMineralFertilizerModel> simpleFertilizers,
                                                                 List<MineralFertilizerModel> mineralFertilizers) {
        Map<Nutrient, NutrientPrice> prices = new EnumMap<>(Nutrient.class);
        if (simpleFertilizers != null) {
            for (SimpleMineralFertilizerModel fertilizer : simpleFertilizers) {
                String name = normalize(fertilizer.getName());
                if (name.contains("sulfato de amonio") || name.contains("ureia")) {
                    register(prices, Nutrient.N, fertilizer.getN(), fertilizer.getName(), "SIMPLES",
                            fertilizer.getPrecoSaco5Kg(), fertilizer.getPrecoSaco25Kg(), fertilizer.getPrecoSaco50Kg(), fertilizer.getPrecoSaco1000Kg());
                }
                if (name.contains("superfosfato simples") || name.contains("superfosfato triplo")) {
                    register(prices, Nutrient.P2O5, fertilizer.getP2O5(), fertilizer.getName(), "SIMPLES",
                            fertilizer.getPrecoSaco5Kg(), fertilizer.getPrecoSaco25Kg(), fertilizer.getPrecoSaco50Kg(), fertilizer.getPrecoSaco1000Kg());
                }
                if (name.contains("cloreto de potassio")) {
                    register(prices, Nutrient.K2O, fertilizer.getK2O(), fertilizer.getName(), "SIMPLES",
                            fertilizer.getPrecoSaco5Kg(), fertilizer.getPrecoSaco25Kg(), fertilizer.getPrecoSaco50Kg(), fertilizer.getPrecoSaco1000Kg());
                }
                registerMicronutrients(prices, fertilizer.getName(), "SIMPLES", fertilizer.getB(), fertilizer.getCu(),
                        fertilizer.getFe(), fertilizer.getMn(), fertilizer.getMo(), fertilizer.getZn(),
                        fertilizer.getPrecoSaco5Kg(), fertilizer.getPrecoSaco25Kg(), fertilizer.getPrecoSaco50Kg(), fertilizer.getPrecoSaco1000Kg());
            }
        }
        if (mineralFertilizers != null) {
            for (MineralFertilizerModel fertilizer : mineralFertilizers) {
                registerMicronutrients(prices, fertilizer.getName(), "CONCENTRADO", fertilizer.getB(), fertilizer.getCu(),
                        fertilizer.getFe(), fertilizer.getMn(), fertilizer.getMo(), fertilizer.getZn(),
                        fertilizer.getPrecoSaco5Kg(), fertilizer.getPrecoSaco25Kg(), fertilizer.getPrecoSaco50Kg(), fertilizer.getPrecoSaco1000Kg());
            }
        }
        return prices;
    }

    private void registerMicronutrients(Map<Nutrient, NutrientPrice> prices,
                                        String fertilizerName,
                                        String fertilizerType,
                                        Double b,
                                        Double cu,
                                        Double fe,
                                        Double mn,
                                        Double mo,
                                        Double zn,
                                        BigDecimal price5,
                                        BigDecimal price25,
                                        BigDecimal price50,
                                        BigDecimal price1000) {
        register(prices, Nutrient.B, b, fertilizerName, fertilizerType, price5, price25, price50, price1000);
        register(prices, Nutrient.Cu, cu, fertilizerName, fertilizerType, price5, price25, price50, price1000);
        register(prices, Nutrient.Fe, fe, fertilizerName, fertilizerType, price5, price25, price50, price1000);
        register(prices, Nutrient.Mn, mn, fertilizerName, fertilizerType, price5, price25, price50, price1000);
        register(prices, Nutrient.Mo, mo, fertilizerName, fertilizerType, price5, price25, price50, price1000);
        register(prices, Nutrient.Zn, zn, fertilizerName, fertilizerType, price5, price25, price50, price1000);
    }

    private void register(Map<Nutrient, NutrientPrice> prices,
                          Nutrient nutrient,
                          Double nutrientPercent,
                          String fertilizerName,
                          String fertilizerType,
                          BigDecimal price5,
                          BigDecimal price25,
                          BigDecimal price50,
                          BigDecimal price1000) {
        if (!positive(nutrientPercent)) {
            return;
        }
        for (CommercialUnit unit : commercialUnits(price5, price25, price50, price1000)) {
            BigDecimal denominator = unit.weightKg().multiply(BigDecimal.valueOf(nutrientPercent)).divide(ONE_HUNDRED, 10, RoundingMode.HALF_UP);
            if (denominator.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            BigDecimal pricePerKgNutrient = unit.price().divide(denominator, 6, RoundingMode.HALF_UP);
            NutrientPrice candidate = new NutrientPrice(nutrient, pricePerKgNutrient, fertilizerName, fertilizerType, unit.weightKg(), unit.price());
            NutrientPrice current = prices.get(nutrient);
            if (current == null || candidate.pricePerKg().compareTo(current.pricePerKg()) < 0) {
                prices.put(nutrient, candidate);
            }
        }
    }

    private OpportunityCostDecision decideFormulated(FormulatedMineralFertilizerModel fertilizer,
                                                     Map<Nutrient, NutrientPrice> nutrientPrices) {
        List<NutrientContribution> contributions = new ArrayList<>();
        addContribution(contributions, Nutrient.N, fertilizer.getN(), nutrientPrices);
        addContribution(contributions, Nutrient.P2O5, fertilizer.getP2O5(), nutrientPrices);
        addContribution(contributions, Nutrient.K2O, fertilizer.getK2O(), nutrientPrices);

        String category = positive(fertilizer.getK2O()) ? "FORMULADO" : "COMPOSTO";
        String opportunityLabel = "FORMULADO".equals(category) ? "POAF" : "POAC";
        String commercialLabel = "FORMULADO".equals(category) ? "PCAF" : "PCAC";
        String decisionTarget = "FORMULADO".equals(category) ? "formulado" : "composto";
        String name = formatFormulatedName(fertilizer);
        return decide(name, category, commercialLabel, opportunityLabel, decisionTarget, contributions,
                fertilizer.getPrecoSaco5Kg(), fertilizer.getPrecoSaco25Kg(), fertilizer.getPrecoSaco50Kg(), fertilizer.getPrecoSaco1000Kg());
    }

    private OpportunityCostDecision decideFte(MineralFertilizerModel fertilizer,
                                              Map<Nutrient, NutrientPrice> nutrientPrices) {
        List<NutrientContribution> contributions = new ArrayList<>();
        addContribution(contributions, Nutrient.B, fertilizer.getB(), nutrientPrices);
        addContribution(contributions, Nutrient.Cu, fertilizer.getCu(), nutrientPrices);
        addContribution(contributions, Nutrient.Fe, fertilizer.getFe(), nutrientPrices);
        addContribution(contributions, Nutrient.Mn, fertilizer.getMn(), nutrientPrices);
        addContribution(contributions, Nutrient.Mo, fertilizer.getMo(), nutrientPrices);
        addContribution(contributions, Nutrient.Zn, fertilizer.getZn(), nutrientPrices);
        return decide(fertilizer.getName(), "FTE", "PCAFTE", "POAFTE", "FTE", contributions,
                fertilizer.getPrecoSaco5Kg(), fertilizer.getPrecoSaco25Kg(), fertilizer.getPrecoSaco50Kg(), fertilizer.getPrecoSaco1000Kg());
    }

    private OpportunityCostDecision decide(String fertilizerName,
                                           String category,
                                           String commercialLabel,
                                           String opportunityLabel,
                                           String decisionTarget,
                                           List<NutrientContribution> contributions,
                                           BigDecimal price5,
                                           BigDecimal price25,
                                           BigDecimal price50,
                                           BigDecimal price1000) {
        Optional<CommercialUnit> actualUnit = bestCommercialUnit(price5, price25, price50, price1000);
        if (actualUnit.isEmpty()) {
            return indeterminate(fertilizerName, category, "indeterminada por ausência de preço",
                    "Ausência de preço comercial válido para " + safe(fertilizerName) + ".");
        }
        if (contributions.isEmpty()) {
            return indeterminate(fertilizerName, category, "indeterminada por ausência de preço",
                    "Ausência de menor R$/kg para um ou mais nutrientes exigidos em " + safe(fertilizerName) + ".");
        }
        BigDecimal opportunityPrice = BigDecimal.ZERO;
        for (NutrientContribution contribution : contributions) {
            if (contribution.nutrientPrice() == null) {
                return indeterminate(fertilizerName, category, "indeterminada por ausência de preço",
                        "Ausência de preço de referência para " + contribution.nutrient().name()
                                + " em fontes simples/concentradas válidas.");
            }
            BigDecimal mass = actualUnit.get().weightKg()
                    .multiply(BigDecimal.valueOf(contribution.percent()))
                    .divide(ONE_HUNDRED, 10, RoundingMode.HALF_UP);
            opportunityPrice = opportunityPrice.add(mass.multiply(contribution.nutrientPrice().pricePerKg()));
        }
        opportunityPrice = opportunityPrice.multiply(MIX_FACTOR).setScale(2, RoundingMode.HALF_UP);
        if (opportunityPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return indeterminate(fertilizerName, category, "indeterminada por ausência de preço",
                    "Preço de oportunidade calculado igual a zero para " + safe(fertilizerName) + ".");
        }
        BigDecimal ratio = actualUnit.get().price().divide(opportunityPrice, 6, RoundingMode.HALF_UP);
        String decision = preferSimpleSources(ratio)
                ? "comprar e usar adubos simples"
                : "comprar e usar " + decisionTarget;
        return new OpportunityCostDecision(
                safe(fertilizerName),
                category,
                commercialLabel,
                actualUnit.get().price().setScale(2, RoundingMode.HALF_UP),
                opportunityLabel,
                opportunityPrice,
                actualUnit.get().weightKg().setScale(0, RoundingMode.HALF_UP),
                ratio.setScale(4, RoundingMode.HALF_UP),
                decision,
                false,
                null,
                contributionSummary(contributions));
    }

    static boolean preferSimpleSources(BigDecimal commercialToOpportunityRatio) {
        return commercialToOpportunityRatio != null
                && commercialToOpportunityRatio.compareTo(BigDecimal.ONE) > 0;
    }

    private OpportunityCostDecision indeterminate(String fertilizerName, String category, String decision, String justification) {
        return new OpportunityCostDecision(safe(fertilizerName), category, null, null, null, null, null, null,
                decision, true, justification, "");
    }

    private void addContribution(List<NutrientContribution> contributions,
                                 Nutrient nutrient,
                                 Double percent,
                                 Map<Nutrient, NutrientPrice> nutrientPrices) {
        if (!positive(percent)) {
            return;
        }
        contributions.add(new NutrientContribution(nutrient, percent, nutrientPrices.get(nutrient)));
    }

    private Optional<CommercialUnit> bestCommercialUnit(BigDecimal price5,
                                                        BigDecimal price25,
                                                        BigDecimal price50,
                                                        BigDecimal price1000) {
        return commercialUnits(price5, price25, price50, price1000).stream()
                .min(Comparator.comparing(unit -> unit.price().divide(unit.weightKg(), 6, RoundingMode.HALF_UP)));
    }

    private List<CommercialUnit> commercialUnits(BigDecimal price5,
                                                 BigDecimal price25,
                                                 BigDecimal price50,
                                                 BigDecimal price1000) {
        List<CommercialUnit> units = new ArrayList<>();
        addCommercialUnit(units, price5, 5);
        addCommercialUnit(units, price25, 25);
        addCommercialUnit(units, price50, 50);
        addCommercialUnit(units, price1000, 1000);
        return units;
    }

    private void addCommercialUnit(List<CommercialUnit> units, BigDecimal price, int weightKg) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0 || weightKg <= 0) {
            return;
        }
        units.add(new CommercialUnit(BigDecimal.valueOf(weightKg), price));
    }

    private List<FormulatedMineralFertilizerModel> selectedFormulatedFertilizers(List<FormulatedMineralFertilizerModel> fertilizers,
                                                                                 List<Long> selectedIds) {
        if (fertilizers == null || fertilizers.isEmpty() || selectedIds == null || selectedIds.isEmpty()) {
            return List.of();
        }
        Map<Long, FormulatedMineralFertilizerModel> byId = new LinkedHashMap<>();
        for (FormulatedMineralFertilizerModel fertilizer : fertilizers) {
            if (fertilizer != null && fertilizer.getId() != null) {
                byId.putIfAbsent(fertilizer.getId(), fertilizer);
            }
        }
        return selectedIds.stream()
                .distinct()
                .map(byId::get)
                .filter(fertilizer -> fertilizer != null)
                .toList();
    }

    private boolean hasMicronutrientsOnly(MineralFertilizerModel fertilizer) {
        if (fertilizer == null) {
            return false;
        }
        boolean hasMicro = positive(fertilizer.getB()) || positive(fertilizer.getCu()) || positive(fertilizer.getFe())
                || positive(fertilizer.getMn()) || positive(fertilizer.getMo()) || positive(fertilizer.getZn());
        boolean hasMacro = positive(fertilizer.getN()) || positive(fertilizer.getP2O5()) || positive(fertilizer.getK2O());
        String name = normalize(fertilizer.getName());
        return hasMicro && (!hasMacro || name.contains("fte"));
    }

    private String contributionSummary(List<NutrientContribution> contributions) {
        List<String> parts = new ArrayList<>();
        for (NutrientContribution contribution : contributions) {
            String source = contribution.nutrientPrice() != null
                    ? contribution.nutrientPrice().fertilizerName() + " (R$ " + formatMoney(contribution.nutrientPrice().pricePerKg()) + "/kg)"
                    : "sem referência";
            parts.add(contribution.nutrient().name() + " " + formatPercent(contribution.percent()) + " via " + source);
        }
        return String.join("; ", parts);
    }

    private String formatFormulatedName(FormulatedMineralFertilizerModel fertilizer) {
        if (fertilizer == null) {
            return "NPK formulado";
        }
        return String.format(Locale.US, "NPK %.2f-%.2f-%.2f", fertilizer.getN(), fertilizer.getP2O5(), fertilizer.getK2O());
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Não informado" : value;
    }

    private String formatMoney(BigDecimal value) {
        return value == null ? "0,00" : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatPercent(Double value) {
        return String.format(Locale.US, "%.2f%%", value == null ? 0d : value);
    }

    private boolean positive(Double value) {
        return value != null && Double.isFinite(value) && value > 0d;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
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

    private List<FormulatedMineralFertilizerModel> selectFormulatedFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
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

    private List<MineralFertilizerModel> selectMineralFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
        FertilizerSourceOption resolvedOption = sourceOption != null ? sourceOption : FertilizerSourceOption.BOTH;
        return switch (resolvedOption) {
            case PRIVATE -> mineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user);
            case PUBLIC -> mineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case DEFAULT -> mineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO);
            case BOTH, ALL -> dedup(
                    dedup(mineralFertilizerRepository.findAllByUserAndPublicoFalseOrderByNameAsc(user),
                            mineralFertilizerRepository.findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo.USUARIO_SUPREMO),
                            MineralFertilizerModel::getId),
                    mineralFertilizerRepository.findAllByUser_CargoOrderByNameAsc(Cargo.USUARIO_SUPREMO),
                    MineralFertilizerModel::getId);
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

    public enum Nutrient {
        N, P2O5, K2O, B, Cu, Fe, Mn, Mo, Zn
    }

    public record OpportunityCostResult(
            Map<Nutrient, NutrientPrice> nutrientPrices,
            List<OpportunityCostDecision> decisions,
            List<String> warnings) {
    }

    public record NutrientPrice(
            Nutrient nutrient,
            BigDecimal pricePerKg,
            String fertilizerName,
            String fertilizerType,
            BigDecimal commercialWeightKg,
            BigDecimal commercialPrice) {
    }

    public record OpportunityCostDecision(
            String fertilizerName,
            String category,
            String commercialPriceLabel,
            BigDecimal commercialPrice,
            String opportunityPriceLabel,
            BigDecimal opportunityPrice,
            BigDecimal commercialWeightKg,
            BigDecimal ratio,
            String decision,
            boolean indeterminate,
            String justification,
            String contributionSummary) {
    }

    private record CommercialUnit(BigDecimal weightKg, BigDecimal price) {
    }

    private record NutrientContribution(Nutrient nutrient, Double percent, NutrientPrice nutrientPrice) {
    }
}
