package com.migueltcc.fertintelligence.service.implementation.RecommendationEngine;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LimingRequirementCalculatorTest {

    private final LimingRequirementCalculator calculator = new LimingRequirementCalculator(
            new CalciumMagnesiumBalanceCalculator());

    @Test
    void usesPrntOneHundredRequirementAndNormalizesCalciumAndMagnesiumToMmol() {
        var request = RecommendationCreateRequestDto.builder()
                .limingCriteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS)
                .build();
        var fertility = FertilityAnalysisExtractModel.builder()
                .ctcPh7(100d)
                .unidadeCtcPh7(FertilityAnalysisUnit.MMOLC_PER_DM3)
                .saturacaoBasesV(30d)
                .calcio(2d)
                .unidadeCalcio(FertilityAnalysisUnit.CMOLC_PER_DM3)
                .magnesio(1d)
                .unidadeMagnesio(FertilityAnalysisUnit.CMOLC_PER_DM3)
                .build();

        var result = calculator.calculate(
                request,
                Optional.of(fertility),
                null,
                CropFertilizationTableModel.builder().criteria(CriterioCalagem.SATURACAO_POR_BASES_TROCAVEIS).build(),
                new ArrayList<>());

        assertThat(result.getTheoreticalRequirement()).isEqualTo(4d);
        assertThat(result.getPrnt()).isEqualTo(100d);
        assertThat(result.getUnit()).isEqualTo("t/ha");
        assertThat(result.getCalciumMagnesiumBalance().currentCalcium()).isEqualTo(20d);
        assertThat(result.getCalciumMagnesiumBalance().currentMagnesium()).isEqualTo(10d);
        assertThat(result.getCalciumMagnesiumBalance().currentRatio()).isEqualTo(2d);
        assertThat(result.getCalciumMagnesiumBalance().available()).isTrue();
    }
}
