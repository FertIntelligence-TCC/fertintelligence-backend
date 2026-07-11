package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis.PhysicalAnalysisUnit;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import com.migueltcc.fertintelligence.repository.DiverseContentRangeRepository;
import com.migueltcc.fertintelligence.repository.SimpleMineralFertilizerRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Service
class GypsumCalculationService {

    private static final String UNIT = "kg/ha";
    private static final String CRITERION =
            "Camadas subsuperficiais avaliadas por Ca2+ < 5 mmolc/dm³, Al3+ >= 3 mmolc/dm³ ou Valor m >= 20%; NG = 5 * maior teor de argila em g/kg nas camadas 21-40 e 41-60 cm.";
    static final String NOT_NEEDED_MESSAGE =
            "O solo não precisa de gessagem, pois tem Ca²⁺ ≥ 5 mmolc/dm³, teor de Al³⁺ < 3 mmolc/dm³ e Valor m < 20%.";
    static final String MISSING_SUBSURFACE_MESSAGE =
            "Não é possível recomendar gessagem sem análises das camadas subsuperficiais; a camada 0-20 cm é usada para manejo da adubação da cultura e calagem.";
    static final String LOW_DOSE_RECOMMENDATION =
            "A dose < 400 kg/ha (ou < 60 kg/ha de S) de gesso pode ser aplicada na adubação de plantio, juntamente com o formulado escolhido, na linha de plantio ou a lanço; ou aplicar o equivalente em S recomendado (S kg/ha = kg/ha de gesso agrícola * 15/100) na adubação de plantio, usando como fonte sulfato de amônio (22% de S) e superfosfato simples (11% de S).";
    static final String MEDIUM_DOSE_RECOMMENDATION =
            "A dose de gesso agrícola > 400 e < 2.000 kg/ha deve ser aplicada toda a lanço em área total, imediatamente antes do plantio. Não precisa incorporar, mas, se necessário, fazer gradagem da área.";
    static final String HIGH_DOSE_RECOMMENDATION =
            "A dose de gesso agrícola ≥ 2.000 kg/ha deve ser aplicada 30 a 90 dias antes do plantio, juntamente com o calcário, e incorporada com aração e gradagem. Se for aplicado somente o gesso, incorporar com grade leve para evitar perda pelo arraste do vento.";

    private final DiverseContentRangeRepository diverseContentRangeRepository;
    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;

    GypsumCalculationService(DiverseContentRangeRepository diverseContentRangeRepository,
                             SimpleMineralFertilizerRepository simpleMineralFertilizerRepository) {
        this.diverseContentRangeRepository = diverseContentRangeRepository;
        this.simpleMineralFertilizerRepository = simpleMineralFertilizerRepository;
    }

    RecommendationCalculationService.GypsumRequirementResult calculate(List<FertilityAnalysisExtractModel> fertilityExtracts,
                                                                       List<PhysicalAnalysisExtractModel> physicalAnalysisExtracts,
                                                                       CropFertilizationTableModel cropFertilizationTable,
                                                                       SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                                                       UserModel user,
                                                                       FertilizerSourceOption sourceOption,
                                                                       List<String> warnings) {
        Map<String, Double> inputValues = new LinkedHashMap<>();
        List<String> gypsumWarnings = new ArrayList<>();
        List<FertilityAnalysisExtractModel> fertilityAnalyses = fertilityExtracts != null ? fertilityExtracts : List.of();
        List<PhysicalAnalysisExtractModel> physicalAnalyses = physicalAnalysisExtracts != null ? physicalAnalysisExtracts : List.of();

        if (fertilityAnalyses.isEmpty() || physicalAnalyses.isEmpty()) {
            return notEvaluatedByMissingDepth(fertilityAnalyses, physicalAnalyses,
                    inputValues, gypsumWarnings, warnings);
        }

        Double clay = physicalAnalyses.stream()
                .map(PhysicalAnalysisExtractModel::getTeorArgila)
                .filter(Objects::nonNull)
                .max(Double::compareTo)
                .orElse(null);

        inputValues.put("Maior teor de argila subsuperficial (g/kg)", clay);
        for (FertilityAnalysisExtractModel fertility : fertilityAnalyses) {
            String suffix = " " + formatDepth(fertility);
            inputValues.put("Cálcio" + suffix + " (" + fertilityUnit(fertility.getUnidadeCalcio()) + ")", fertility.getCalcio());
            inputValues.put("Alumínio" + suffix + " (" + fertilityUnit(fertility.getUnidadeAluminio()) + ")", fertility.getAluminio());
            inputValues.put("Saturação por alumínio" + suffix + " (%)", fertility.getSaturacaoAluminioM());
        }

        if (clay == null) {
            gypsumWarnings.add("Gessagem não calculada porque as camadas subsuperficiais não possuem teor de argila informado.");
            warnings.addAll(gypsumWarnings);
            return RecommendationCalculationService.GypsumRequirementResult.builder()
                    .needed(null)
                    .criterion(CRITERION)
                    .inputValues(inputValues)
                    .unit(UNIT)
                    .justification("Não foi possível calcular NG sem teor de argila nas camadas 21-40 e/ou 41-60 cm.")
                    .warnings(gypsumWarnings)
                    .build();
        }

        boolean hasAnyIndicator = fertilityAnalyses.stream().anyMatch(fertility ->
                fertility.getCalcio() != null || fertility.getAluminio() != null || fertility.getSaturacaoAluminioM() != null);
        if (!hasAnyIndicator) {
            gypsumWarnings.add("Dados insuficientes para avaliar Ca2+, Al3+ ou m% nas camadas subsuperficiais.");
            warnings.addAll(gypsumWarnings);
            return RecommendationCalculationService.GypsumRequirementResult.builder()
                    .needed(null)
                    .criterion(CRITERION)
                    .inputValues(inputValues)
                    .unit(UNIT)
                    .justification("Gessagem não calculada porque nenhum indicador crítico estava disponível nas camadas subsuperficiais.")
                    .warnings(gypsumWarnings)
                    .build();
        }

        boolean calciumIndicatesNeed = fertilityAnalyses.stream().anyMatch(f -> f.getCalcio() != null && f.getCalcio() < 5d);
        boolean aluminumIndicatesNeed = fertilityAnalyses.stream().anyMatch(f -> f.getAluminio() != null && f.getAluminio() >= 3d);
        boolean aluminumSaturationIndicatesNeed = fertilityAnalyses.stream().anyMatch(f -> f.getSaturacaoAluminioM() != null && f.getSaturacaoAluminioM() >= 20d);
        boolean needed = calciumIndicatesNeed || aluminumIndicatesNeed || aluminumSaturationIndicatesNeed;
        Double dose = needed ? 5d * clay : 0d;
        Double sulfurEquivalent = dose * 15d / 100d;

        String justification = needed
                ? "Gessagem indicada por pelo menos uma condição crítica subsuperficial: "
                + criticalConditionSummary(calciumIndicatesNeed, aluminumIndicatesNeed, aluminumSaturationIndicatesNeed)
                + ". Dose calculada por NG = 5 * " + formatNumber(clay) + " = " + formatNumber(dose) + " kg/ha."
                : NOT_NEEDED_MESSAGE;

        GypsumSourceSelection sourceSelection = selectGypsumSource(user, sourceOption, dose, gypsumWarnings);

        warnings.addAll(gypsumWarnings);
        return RecommendationCalculationService.GypsumRequirementResult.builder()
                .needed(needed)
                .criterion(CRITERION)
                .inputValues(inputValues)
                .calculatedRequirement(dose)
                .unit(UNIT)
                .sourceName(sourceSelection.sourceName())
                .sourceType(sourceSelection.sourceType())
                .commercialDose(sourceSelection.commercialDose())
                .commercialDoseUnit(sourceSelection.commercialDoseUnit())
                .sourceJustification(sourceSelection.justification())
                .sourceLimitations(sourceSelection.limitations())
                .sulfurEquivalent(sulfurEquivalent)
                .applicationRecommendation(applicationRecommendation(dose, sulfurEquivalent))
                .lowDoseAlternativeApplicable(needed && isLowDose(dose, sulfurEquivalent))
                .justification(justification)
                .warnings(gypsumWarnings)
                .build();
    }

    private RecommendationCalculationService.GypsumRequirementResult notEvaluatedByMissingDepth(List<FertilityAnalysisExtractModel> fertilityAnalyses,
                                                                                                List<PhysicalAnalysisExtractModel> physicalAnalyses,
                                                                                                Map<String, Double> inputValues,
                                                                                                List<String> gypsumWarnings,
                                                                                                List<String> warnings) {
        String warning = MISSING_SUBSURFACE_MESSAGE;
        gypsumWarnings.add(warning);
        if (fertilityAnalyses == null || fertilityAnalyses.isEmpty()) {
            gypsumWarnings.add("Análise de fertilidade sem extrato/camada subsuperficial 21-40 ou 41-60 cm.");
        }
        if (physicalAnalyses == null || physicalAnalyses.isEmpty()) {
            gypsumWarnings.add("Análise física sem extrato/camada subsuperficial 21-40 ou 41-60 cm.");
        }
        warnings.addAll(gypsumWarnings);
        return RecommendationCalculationService.GypsumRequirementResult.builder()
                .evaluated(false)
                .needed(null)
                .inputValues(inputValues)
                .justification(warning)
                .warnings(gypsumWarnings)
                .build();
    }

    private String applicationRecommendation(Double dose, Double sulfurEquivalent) {
        if (dose == null || dose <= 0d) {
            return "Não aplicar gesso agrícola.";
        }
        if (isLowDose(dose, sulfurEquivalent)) {
            return LOW_DOSE_RECOMMENDATION;
        }
        if (dose < 2000d) {
            return MEDIUM_DOSE_RECOMMENDATION;
        }
        return HIGH_DOSE_RECOMMENDATION;
    }

    private boolean isLowDose(Double dose, Double sulfurEquivalent) {
        return dose != null && sulfurEquivalent != null && (dose < 400d || sulfurEquivalent < 60d);
    }

    private String criticalConditionSummary(boolean calcium, boolean aluminum, boolean aluminumSaturation) {
        List<String> conditions = new ArrayList<>();
        if (calcium) conditions.add("Ca2+ < 5 mmolc/dm³");
        if (aluminum) conditions.add("Al3+ >= 3 mmolc/dm³");
        if (aluminumSaturation) conditions.add("Valor m >= 20%");
        return String.join(", ", conditions);
    }

    private String formatDepth(FertilityAnalysisExtractModel fertility) {
        return "(" + formatNumber(extractInitialDepth(fertility)) + "-" + formatNumber(extractFinalDepth(fertility)) + " cm)";
    }

    private GypsumSourceSelection selectGypsumSource(UserModel user,
                                                     FertilizerSourceOption sourceOption,
                                                     Double calculatedDose,
                                                     List<String> gypsumWarnings) {
        if (calculatedDose == null) {
            String limitation = "Fonte comercial de gesso agrícola não selecionada porque a dose de gessagem não foi calculada.";
            gypsumWarnings.add(limitation);
            return new GypsumSourceSelection(null, null, null, null,
                    "Seleção de fonte não realizada por ausência de dose calculada.", limitation);
        }
        if (calculatedDose <= 0d) {
            return new GypsumSourceSelection(null, null, 0d, UNIT,
                    "Gessagem não indicada; não há fonte comercial a aplicar.", "Sem limitação adicional para fonte comercial.");
        }

        Optional<SimpleMineralFertilizerModel> gypsumSource = selectSimpleFertilizers(user, sourceOption).stream()
                .filter(this::isGypsumProduct)
                .max(Comparator.comparing((SimpleMineralFertilizerModel f) -> nvl(f.getCa()) + nvl(f.getS()))
                        .thenComparing(f -> f.getId() == null ? 0L : f.getId()));

        if (gypsumSource.isEmpty()) {
            String limitation = "Não há produto de gesso agrícola cadastrado nos adubos minerais simples acessíveis pela origem selecionada; a dose calculada foi mantida sem fonte comercial.";
            gypsumWarnings.add(limitation);
            return new GypsumSourceSelection(null, null, calculatedDose, UNIT,
                    "Dose mantida como gesso agrícola calculado pela tabela, sem produto comercial selecionado.", limitation);
        }

        SimpleMineralFertilizerModel source = gypsumSource.get();
        String justification = String.format(Locale.US,
                "Produto cadastrado como adubo mineral simples com nome compatível com gesso agrícola e composição Ca %.2f%% / S %.2f%%.",
                nvl(source.getCa()), nvl(source.getS()));
        String limitation = "O modelo do produto não possui teor de pureza de gesso agrícola; portanto a dose comercial foi mantida igual à dose de gesso calculada.";
        gypsumWarnings.add(limitation);
        return new GypsumSourceSelection(source.getName(), "SIMPLES", calculatedDose, UNIT, justification, limitation);
    }

    private RecommendationCalculationService.SoilChemicalDiagnosisItem classifyDiverseRange(String attribute,
                                                                                           Double value,
                                                                                           String unit,
                                                                                           Optional<DiverseContentRangeModel> range,
                                                                                           Function<DiverseContentRangeModel, RangeCriterion> criterionExtractor,
                                                                                           String observation) {
        if (value == null) return missingValue(attribute, "Valor ausente no extrato de fertilidade.");
        if (range.isEmpty()) return notClassified(attribute, value, unit, "Critério ausente na tabela selecionada.");
        return classifyRange(attribute, value, unit, criterionExtractor.apply(range.get()), observation);
    }

    private RecommendationCalculationService.SoilChemicalDiagnosisItem classifyRange(String attribute,
                                                                                    Double value,
                                                                                    String unit,
                                                                                    RangeCriterion criterion,
                                                                                    String observation) {
        if (value == null) return missingValue(attribute, "Valor ausente no extrato de fertilidade.");
        if (criterion == null || criterion.lowStart() == null || criterion.mediumStart() == null
                || criterion.highStart() == null || criterion.tooHighStart() == null) {
            return notClassified(attribute, value, unit, "Critério incompleto na tabela selecionada.");
        }
        String interpretation;
        String usedRange;
        if (value < criterion.lowStart()) {
            interpretation = "Muito baixo";
            usedRange = "< " + formatNumber(criterion.lowStart());
        } else if (value < criterion.mediumStart()) {
            interpretation = "Baixo";
            usedRange = formatInterval(criterion.lowStart(), criterion.lowEnd(), criterion.mediumStart());
        } else if (value < criterion.highStart()) {
            interpretation = "Médio";
            usedRange = formatInterval(criterion.mediumStart(), criterion.mediumEnd(), criterion.highStart());
        } else if (value < criterion.tooHighStart()) {
            interpretation = "Alto";
            usedRange = formatInterval(criterion.highStart(), criterion.highEnd(), criterion.tooHighStart());
        } else {
            interpretation = "Muito alto";
            usedRange = ">= " + formatNumber(criterion.tooHighStart());
        }
        return RecommendationCalculationService.SoilChemicalDiagnosisItem.builder()
                .attribute(attribute)
                .analyzedValue(value)
                .unit(unit)
                .interpretation(interpretation)
                .usedCriterion(usedRange)
                .technicalObservation(observation)
                .build();
    }

    private RecommendationCalculationService.SoilChemicalDiagnosisItem missingValue(String attribute, String observation) {
        return RecommendationCalculationService.SoilChemicalDiagnosisItem.builder()
                .attribute(attribute)
                .technicalObservation(observation)
                .build();
    }

    private RecommendationCalculationService.SoilChemicalDiagnosisItem notClassified(String attribute, Double value, String unit, String observation) {
        return RecommendationCalculationService.SoilChemicalDiagnosisItem.builder()
                .attribute(attribute)
                .analyzedValue(value)
                .unit(unit)
                .technicalObservation(observation)
                .build();
    }

    private boolean isInterpretation(RecommendationCalculationService.SoilChemicalDiagnosisItem item, String... expected) {
        if (item == null || item.getInterpretation() == null) return false;
        return Arrays.asList(expected).contains(item.getInterpretation());
    }

    private String safeInterpretation(RecommendationCalculationService.SoilChemicalDiagnosisItem item) {
        return item == null || item.getInterpretation() == null ? "não classificado" : item.getInterpretation();
    }

    private boolean isGypsumProduct(SimpleMineralFertilizerModel fertilizer) {
        if (fertilizer == null || fertilizer.getName() == null) return false;
        String name = normalizeText(fertilizer.getName());
        boolean nameMatches = name.contains("gesso") || name.contains("gypsum") || name.contains("sulfato de calcio");
        return nameMatches && (nvl(fertilizer.getCa()) > 0d || nvl(fertilizer.getS()) > 0d);
    }

    private List<SimpleMineralFertilizerModel> selectSimpleFertilizers(UserModel user, FertilizerSourceOption sourceOption) {
        return switch (sourceOption) {
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

    private <T> List<T> dedup(List<T> a, List<T> b, Function<T, Long> id) {
        Map<Long, T> selected = new LinkedHashMap<>();
        a.forEach(item -> selected.putIfAbsent(id.apply(item), item));
        b.forEach(item -> selected.putIfAbsent(id.apply(item), item));
        return new ArrayList<>(selected.values());
    }

    private String physicalUnit(PhysicalAnalysisUnit unit) {
        PhysicalAnalysisUnit normalized = unit != null ? unit.canonicalForPhysicalExtract() : PhysicalAnalysisUnit.G_PER_KG;
        return normalizeUnit(normalized.getSymbol(), "g/kg");
    }

    private String fertilityUnit(FertilityAnalysisUnit unit) {
        FertilityAnalysisUnit normalized = unit != null ? unit.canonicalForFertilityExtract() : FertilityAnalysisUnit.MMOLC_PER_DM3;
        return normalizeUnit(normalized.getSymbol(), "mmolc/dm³");
    }

    private String normalizeUnit(String unit, String fallback) {
        if (unit == null || unit.isBlank()) return fallback;
        return unit.trim()
                .replace("dm3", "dm³")
                .replace("cmolc/dm³", "mmolc/dm³")
                .replace("cmolc/dm3", "mmolc/dm³");
    }

    private Double extractInitialDepth(FertilityAnalysisExtractModel fertility) {
        if (fertility == null) return null;
        if (fertility.getRangeExtract() != null) return doubleFromInteger(fertility.getRangeExtract().getProfundidade_inicial());
        if (fertility.getLayerExtract() != null) return doubleFromInteger(fertility.getLayerExtract().getProfundidade_inicial());
        return null;
    }

    private Double extractFinalDepth(FertilityAnalysisExtractModel fertility) {
        if (fertility == null) return null;
        if (fertility.getRangeExtract() != null) return doubleFromInteger(fertility.getRangeExtract().getProfundidade_final());
        if (fertility.getLayerExtract() != null) return doubleFromInteger(fertility.getLayerExtract().getProfundidade_final());
        return null;
    }

    private Double extractInitialDepth(PhysicalAnalysisExtractModel physical) {
        if (physical == null) return null;
        if (physical.getRangeExtract() != null) return doubleFromInteger(physical.getRangeExtract().getProfundidade_inicial());
        if (physical.getLayerExtract() != null) return doubleFromInteger(physical.getLayerExtract().getProfundidade_inicial());
        return null;
    }

    private Double extractFinalDepth(PhysicalAnalysisExtractModel physical) {
        if (physical == null) return null;
        if (physical.getRangeExtract() != null) return doubleFromInteger(physical.getRangeExtract().getProfundidade_final());
        if (physical.getLayerExtract() != null) return doubleFromInteger(physical.getLayerExtract().getProfundidade_final());
        return null;
    }

    private Double doubleFromInteger(Integer value) {
        return value == null ? null : value.doubleValue();
    }

    private String normalizeText(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    private String formatInterval(Double start, Double end, Double fallbackEndExclusive) {
        Double effectiveEnd = end != null ? end : fallbackEndExclusive;
        return formatNumber(start) + " a " + formatNumber(effectiveEnd);
    }

    private String formatNumber(Double value) {
        if (value == null) return "não informado";
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private double nvl(Double value) {
        return value == null ? 0d : value;
    }

    private record GypsumSourceSelection(String sourceName, String sourceType, Double commercialDose,
                                         String commercialDoseUnit, String justification, String limitations) {
    }

    private record RangeCriterion(Double tooLowEnd, Double lowStart, Double lowEnd, Double mediumStart,
                                  Double mediumEnd, Double highStart, Double highEnd, Double tooHighStart) {
    }
}
