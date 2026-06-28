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
import java.util.Optional;
import java.util.function.Function;

@Service
class GypsumCalculationService {

    private static final String UNIT = "t/ha";
    private static final String CRITERION =
            "Faixas diversas da tabela de interpretação para cálcio, alumínio e saturação por alumínio; dose quantitativa de gessagem não modelada no backend atual.";

    private final DiverseContentRangeRepository diverseContentRangeRepository;
    private final SimpleMineralFertilizerRepository simpleMineralFertilizerRepository;

    GypsumCalculationService(DiverseContentRangeRepository diverseContentRangeRepository,
                             SimpleMineralFertilizerRepository simpleMineralFertilizerRepository) {
        this.diverseContentRangeRepository = diverseContentRangeRepository;
        this.simpleMineralFertilizerRepository = simpleMineralFertilizerRepository;
    }

    RecommendationCalculationService.GypsumRequirementResult calculate(Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                                       PhysicalAnalysisExtractModel physicalAnalysis,
                                                                       CropFertilizationTableModel cropFertilizationTable,
                                                                       SoilFertilityInterpretationCriteriaTableModel soilInterpretationTable,
                                                                       UserModel user,
                                                                       FertilizerSourceOption sourceOption,
                                                                       List<String> warnings) {
        Map<String, Double> inputValues = new LinkedHashMap<>();
        List<String> gypsumWarnings = new ArrayList<>();
        FertilityAnalysisExtractModel fertility = fertilityExtract.orElse(null);

        if (fertility == null) {
            gypsumWarnings.add("Nenhum extrato de fertilidade foi encontrado para avaliar necessidade de gessagem.");
            warnings.addAll(gypsumWarnings);
            return RecommendationCalculationService.GypsumRequirementResult.builder()
                    .needed(null)
                    .criterion(CRITERION)
                    .inputValues(inputValues)
                    .unit(UNIT)
                    .justification("Gessagem não avaliada por ausência de extrato de fertilidade.")
                    .warnings(gypsumWarnings)
                    .build();
        }

        inputValues.put("Cálcio (" + fertilityUnit(fertility.getUnidadeCalcio()) + ")", fertility.getCalcio());
        inputValues.put("Alumínio (" + fertilityUnit(fertility.getUnidadeAluminio()) + ")", fertility.getAluminio());
        inputValues.put("Saturação por alumínio (%)", fertility.getSaturacaoAluminioM());
        inputValues.put("CTC efetiva (" + fertilityUnit(fertility.getUnidadeCtcEfetiva()) + ")", fertility.getCtcEfetiva());
        inputValues.put("CTC pH 7,0 (" + fertilityUnit(fertility.getUnidadeCtcPh7()) + ")", fertility.getCtcPh7());
        inputValues.put("Argila (" + physicalUnit(physicalAnalysis != null ? physicalAnalysis.getUnidadeTeorArgila() : null) + ")", physicalAnalysis != null ? physicalAnalysis.getTeorArgila() : null);
        inputValues.put("Enxofre (mg/dm³)", fertility.getEnxofre());
        inputValues.put("Profundidade inicial do extrato de fertilidade (cm)", extractInitialDepth(fertility));
        inputValues.put("Profundidade final do extrato de fertilidade (cm)", extractFinalDepth(fertility));

        if (extractInitialDepth(fertility) == null || extractFinalDepth(fertility) == null) {
            gypsumWarnings.add("Profundidade do extrato de fertilidade não disponível; o backend não inferiu camada para gessagem.");
        }

        Optional<DiverseContentRangeModel> diverseRange = diverseContentRangeRepository.findByTable(soilInterpretationTable);
        if (diverseRange.isEmpty()) {
            gypsumWarnings.add("Não há faixas diversas cadastradas para avaliar cálcio, alumínio e saturação por alumínio na gessagem.");
            warnings.addAll(gypsumWarnings);
            return RecommendationCalculationService.GypsumRequirementResult.builder()
                    .needed(null)
                    .criterion(CRITERION)
                    .inputValues(inputValues)
                    .unit(UNIT)
                    .justification("Gessagem não calculada por ausência de critério técnico cadastrado para os indicadores disponíveis.")
                    .warnings(gypsumWarnings)
                    .build();
        }

        RecommendationCalculationService.SoilChemicalDiagnosisItem calcium = classifyDiverseRange("Cálcio", fertility.getCalcio(), fertilityUnit(fertility.getUnidadeCalcio()), diverseRange,
                r -> new RangeCriterion(r.getCalcium_too_low(), r.getCalcium_low_i(), r.getCalcium_low_f(), r.getCalcium_medium_i(), r.getCalcium_medium_f(), r.getCalcium_hight_i(), r.getCalcium_hight_f(), r.getCalcium_too_hight()),
                "Cálcio usado como indicador para necessidade de gessagem.");
        RecommendationCalculationService.SoilChemicalDiagnosisItem aluminum = classifyDiverseRange("Alumínio", fertility.getAluminio(), fertilityUnit(fertility.getUnidadeAluminio()), diverseRange,
                r -> new RangeCriterion(r.getAluminum_too_low(), r.getAluminum_low_i(), r.getAluminum_low_f(), r.getAluminum_medium_i(), r.getAluminum_medium_f(), r.getAluminum_hight_i(), r.getAluminum_hight_f(), r.getAluminum_too_hight()),
                "Alumínio usado como indicador para necessidade de gessagem.");
        RecommendationCalculationService.SoilChemicalDiagnosisItem aluminumSaturation = classifyDiverseRange("Saturação por alumínio", fertility.getSaturacaoAluminioM(), "%", diverseRange,
                r -> new RangeCriterion(r.getAluminum_saturation_too_low(), r.getAluminum_saturation_low_i(), r.getAluminum_saturation_low_f(), r.getAluminum_saturation_medium_i(), r.getAluminum_saturation_medium_f(), r.getAluminum_saturation_hight_i(), r.getAluminum_saturation_hight_f(), r.getAluminum_saturation_too_hight()),
                "Saturação por alumínio usada como indicador para necessidade de gessagem.");

        boolean calciumIndicatesNeed = isInterpretation(calcium, "Muito baixo", "Baixo");
        boolean aluminumIndicatesNeed = isInterpretation(aluminum, "Alto", "Muito alto");
        boolean aluminumSaturationIndicatesNeed = isInterpretation(aluminumSaturation, "Alto", "Muito alto");
        boolean hasClassifiedIndicator = calcium.getInterpretation() != null
                || aluminum.getInterpretation() != null
                || aluminumSaturation.getInterpretation() != null;

        if (!hasClassifiedIndicator) {
            gypsumWarnings.add("Dados ou critérios insuficientes para classificar cálcio, alumínio ou saturação por alumínio.");
            warnings.addAll(gypsumWarnings);
            return RecommendationCalculationService.GypsumRequirementResult.builder()
                    .needed(null)
                    .criterion(CRITERION)
                    .inputValues(inputValues)
                    .unit(UNIT)
                    .justification("Gessagem não calculada porque nenhum indicador pôde ser classificado com os dados e critérios cadastrados.")
                    .warnings(gypsumWarnings)
                    .build();
        }

        boolean needed = calciumIndicatesNeed || aluminumIndicatesNeed || aluminumSaturationIndicatesNeed;
        Double dose = needed ? null : 0d;
        if (needed) {
            gypsumWarnings.add("Gessagem foi indicada pelos critérios disponíveis, mas a dose quantitativa não está modelada no backend atual.");
        }

        String justification = needed
                ? "Gessagem indicada por pelo menos um indicador crítico: Ca=" + safeInterpretation(calcium)
                + ", Al=" + safeInterpretation(aluminum)
                + ", m%=" + safeInterpretation(aluminumSaturation) + "."
                : "Gessagem não indicada pelos indicadores classificados: Ca=" + safeInterpretation(calcium)
                + ", Al=" + safeInterpretation(aluminum)
                + ", m%=" + safeInterpretation(aluminumSaturation) + ".";

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
                .justification(justification)
                .warnings(gypsumWarnings)
                .build();
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
        PhysicalAnalysisUnit normalized = unit != null ? unit.canonicalForPhysicalExtract() : PhysicalAnalysisUnit.G_PER_DM3;
        return normalizeUnit(normalized.getSymbol(), "g/dm³");
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
