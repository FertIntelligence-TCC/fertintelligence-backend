package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis.PhysicalAnalysisUnit;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
class LimingRequirementCalculator {

    private static final String UNIT = "t/ha";
    private static final String PRNT_WARNING = "Se o calcário comprado tiver PRNT diferente de 100%, corrija o valor de NC multiplicando-o pela expressão 100/PRNT";

    RecommendationCalculationService.LimingRequirementResult calculate(RecommendationCreateRequestDto dto,
                                                                       Optional<FertilityAnalysisExtractModel> fertilityExtract,
                                                                       PhysicalAnalysisExtractModel physicalAnalysis,
                                                                       CropFertilizationTableModel cropFertilizationTable,
                                                                       List<String> warnings) {
        CriterioCalagem selectedCriteria = dto.getLimingCriteria() != null
                ? dto.getLimingCriteria()
                : cropFertilizationTable.getCriteria();
        Map<String, Double> inputValues = new LinkedHashMap<>();
        List<String> limingWarnings = new ArrayList<>();

        if (selectedCriteria == null) {
            limingWarnings.add("Critério de calagem não informado na recomendação nem na tabela de adubação selecionada.");
            warnings.addAll(limingWarnings);
            return notCalculated(null, "Não calculada: critério de calagem ausente.", inputValues, limingWarnings);
        }

        if (fertilityExtract.isEmpty()) {
            limingWarnings.add("Nenhum extrato de fertilidade foi encontrado para calcular necessidade de calagem.");
            warnings.addAll(limingWarnings);
            return notCalculated(selectedCriteria.name(), "Não calculada: análise de fertilidade ausente.", inputValues, limingWarnings);
        }

        RecommendationCalculationService.LimingRequirementResult result = physicalAnalysis == null
                ? calculateByBaseSaturation(selectedCriteria, fertilityExtract.get(), inputValues, limingWarnings)
                : calculateByPhysicalAndFertility(selectedCriteria, fertilityExtract.get(), physicalAnalysis, inputValues, limingWarnings);

        warnings.addAll(limingWarnings);
        return result;
    }

    private RecommendationCalculationService.LimingRequirementResult calculateByBaseSaturation(CriterioCalagem selectedCriteria,
                                                                                               FertilityAnalysisExtractModel fertility,
                                                                                               Map<String, Double> inputValues,
                                                                                               List<String> limingWarnings) {
        String formula = "NC(PRNT 100%, t/ha) = [T * (70 - V) / 100] / 10";
        Double currentBaseSaturation = fertility.getSaturacaoBasesV();
        Double ctcPh7 = fertility.getCtcPh7();
        Double ctcPh7Mmol = exchangeableValueAsMmol(ctcPh7, fertility.getUnidadeCtcPh7());

        inputValues.put("V atual (%)", currentBaseSaturation);
        inputValues.put("V alvo (%)", 70d);
        inputValues.put("CTC pH 7,0 - T (" + fertilityUnit(fertility.getUnidadeCtcPh7()) + ")", ctcPh7);
        inputValues.put("CTC pH 7,0 - T normalizada (mmolc/dm3)", ctcPh7Mmol);

        if (currentBaseSaturation == null) {
            limingWarnings.add("V atual ausente no extrato de fertilidade (saturacaoBasesV).");
        }
        if (ctcPh7 == null) {
            limingWarnings.add("CTC pH 7,0 ausente no extrato de fertilidade (ctcPh7).");
        }
        limingWarnings.add(PRNT_WARNING);

        Double theoreticalRequirement = null;
        if (currentBaseSaturation != null && ctcPh7Mmol != null) {
            theoreticalRequirement = normalizeRequirement(ctcPh7Mmol * (70d - currentBaseSaturation) / 100d);
        }

        return RecommendationCalculationService.LimingRequirementResult.builder()
                .selectedCriteria(selectedCriteria.name())
                .formula(formula)
                .criterionJustification("Saturação por bases a 70%")
                .inputValues(inputValues)
                .theoreticalRequirement(theoreticalRequirement)
                .prnt(100d)
                .correctedRequirement(theoreticalRequirement)
                .calculatedRequirement(theoreticalRequirement)
                .limestoneSource("PRNT 100%")
                .unit(UNIT)
                .warnings(limingWarnings)
                .build();
    }

    private RecommendationCalculationService.LimingRequirementResult calculateByPhysicalAndFertility(CriterioCalagem selectedCriteria,
                                                                                                     FertilityAnalysisExtractModel fertility,
                                                                                                     PhysicalAnalysisExtractModel physicalAnalysis,
                                                                                                     Map<String, Double> inputValues,
                                                                                                     List<String> limingWarnings) {
        String formula = "I = F * Al; II = F * [2 - (Ca + Mg)]; NC(PRNT 100%, t/ha) = maior(I, II) / 10";
        Double exchangeableAluminum = fertility.getAluminio();
        Double calcium = fertility.getCalcio();
        Double magnesium = fertility.getMagnesio();
        Double exchangeableAluminumMmol = exchangeableValueAsMmol(exchangeableAluminum, fertility.getUnidadeAluminio());
        Double calciumMmol = exchangeableValueAsMmol(calcium, fertility.getUnidadeCalcio());
        Double magnesiumMmol = exchangeableValueAsMmol(magnesium, fertility.getUnidadeMagnesio());
        Double clayContent = physicalAnalysis.getTeorArgila();
        Double factor = clayContent != null ? limingFactorByClayContent(clayContent) : null;

        inputValues.put("Al trocável (" + fertilityUnit(fertility.getUnidadeAluminio()) + ")", exchangeableAluminum);
        inputValues.put("Cálcio (" + fertilityUnit(fertility.getUnidadeCalcio()) + ")", calcium);
        inputValues.put("Magnésio (" + fertilityUnit(fertility.getUnidadeMagnesio()) + ")", magnesium);
        inputValues.put("Al trocável normalizado (mmolc/dm3)", exchangeableAluminumMmol);
        inputValues.put("Cálcio normalizado (mmolc/dm3)", calciumMmol);
        inputValues.put("Magnésio normalizado (mmolc/dm3)", magnesiumMmol);
        inputValues.put("Argila (" + physicalUnit(physicalAnalysis.getUnidadeTeorArgila()) + ")", clayContent);
        inputValues.put("Fator de calagem por argila", factor);

        if (exchangeableAluminum == null) {
            limingWarnings.add("Al trocável ausente no extrato de fertilidade (aluminio).");
        }
        if (calcium == null) {
            limingWarnings.add("Cálcio ausente no extrato de fertilidade (calcio).");
        }
        if (magnesium == null) {
            limingWarnings.add("Magnésio ausente no extrato de fertilidade (magnesio).");
        }
        if (clayContent == null) {
            limingWarnings.add("Teor de argila ausente no extrato de análise física (teorArgila), necessário para selecionar o fator de calagem.");
        }
        limingWarnings.add(PRNT_WARNING);

        Double neutralization = exchangeableAluminumMmol != null && factor != null
                ? factor * exchangeableAluminumMmol
                : null;
        Double calciumMagnesiumIncrease = calciumMmol != null && magnesiumMmol != null && factor != null
                ? factor * (20d - (calciumMmol + magnesiumMmol))
                : null;

        inputValues.put("I - Neutralização do Al trocável", neutralization);
        inputValues.put("II - Elevação dos teores de Ca + Mg", calciumMagnesiumIncrease);

        SelectedLimingCriterion winner = selectWinner(neutralization, calciumMagnesiumIncrease);
        Double theoreticalRequirement = winner.value() != null ? normalizeRequirement(winner.value()) : null;

        return RecommendationCalculationService.LimingRequirementResult.builder()
                .selectedCriteria(selectedCriteria.name())
                .formula(formula)
                .criterionJustification(winner.text())
                .inputValues(inputValues)
                .theoreticalRequirement(theoreticalRequirement)
                .prnt(100d)
                .correctedRequirement(theoreticalRequirement)
                .calculatedRequirement(theoreticalRequirement)
                .limestoneSource("PRNT 100%")
                .unit(UNIT)
                .warnings(limingWarnings)
                .build();
    }

    private SelectedLimingCriterion selectWinner(Double neutralization, Double calciumMagnesiumIncrease) {
        if (neutralization == null && calciumMagnesiumIncrease == null) {
            return new SelectedLimingCriterion(null, "Não calculado: dados mínimos insuficientes.");
        }
        if (neutralization == null) {
            return new SelectedLimingCriterion(calciumMagnesiumIncrease, "Elevação dos teores de Ca + Mg");
        }
        if (calciumMagnesiumIncrease == null) {
            return new SelectedLimingCriterion(neutralization, "Neutralização do Al trocável");
        }
        return neutralization >= calciumMagnesiumIncrease
                ? new SelectedLimingCriterion(neutralization, "Neutralização do Al trocável")
                : new SelectedLimingCriterion(calciumMagnesiumIncrease, "Elevação dos teores de Ca + Mg");
    }

    private RecommendationCalculationService.LimingRequirementResult notCalculated(String selectedCriteria,
                                                                                  String formula,
                                                                                  Map<String, Double> inputValues,
                                                                                  List<String> limingWarnings) {
        return RecommendationCalculationService.LimingRequirementResult.builder()
                .selectedCriteria(selectedCriteria)
                .formula(formula)
                .criterionJustification("Não calculado")
                .inputValues(inputValues)
                .unit(UNIT)
                .warnings(limingWarnings)
                .build();
    }

    private Double normalizeRequirement(double rawRequirement) {
        return Math.max(0d, round2(rawRequirement / 10d));
    }

    private Double limingFactorByClayContent(Double clayContent) {
        if (clayContent < 150d) return 1.5;
        if (clayContent <= 350d) return 2.0;
        return 2.5;
    }

    private String fertilityUnit(FertilityAnalysisUnit unit) {
        return unit != null ? unit.getSymbol() : FertilityAnalysisUnit.MMOLC_PER_DM3.getSymbol();
    }

    private String physicalUnit(PhysicalAnalysisUnit unit) {
        return unit != null ? unit.getSymbol() : PhysicalAnalysisUnit.G_PER_DM3.getSymbol();
    }

    private Double exchangeableValueAsMmol(Double value, FertilityAnalysisUnit unit) {
        if (value == null) return null;
        return unit == FertilityAnalysisUnit.CMOLC_PER_DM3 ? value * 10d : value;
    }

    private double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private record SelectedLimingCriterion(Double value, String text) {
    }
}
