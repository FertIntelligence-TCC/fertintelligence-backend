package com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropFoliarAnalysisInterpretationTableLineResponseDto {

    @Schema(example = "10")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "1")
    @JsonProperty("id_tabela_interpretacao")
    private Long table_id;

    @Schema(example = "SOJA")
    @JsonProperty("nome_cultura")
    private NomeComum crop;

    @JsonProperty("teores_n")
    private MenorMaiorTeores n_content;

    @JsonProperty("teores_p")
    private MenorMaiorTeores p_content;

    @JsonProperty("teores_k")
    private MenorMaiorTeores k_content;

    @JsonProperty("teores_mg")
    private MenorMaiorTeores mg_content;

    @JsonProperty("teores_s")
    private MenorMaiorTeores s_content;

    @JsonProperty("teores_b")
    private MenorMaiorTeores b_content;

    @JsonProperty("teores_cu")
    private MenorMaiorTeores cu_content;

    @JsonProperty("teores_fe")
    private MenorMaiorTeores fe_content;

    @JsonProperty("teores_mn")
    private MenorMaiorTeores mn_content;

    @JsonProperty("teores_mo")
    private MenorMaiorTeores mo_content;

    @JsonProperty("teores_zn")
    private MenorMaiorTeores zn_content;
}