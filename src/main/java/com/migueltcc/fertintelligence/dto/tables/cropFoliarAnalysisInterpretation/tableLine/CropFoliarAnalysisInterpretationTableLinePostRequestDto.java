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
public class CropFoliarAnalysisInterpretationTableLinePostRequestDto {

    @JsonProperty("novo_nome_cultura")
    @Schema(example = "MILHO")
    private NomeComum crop;

    @JsonProperty("novo_teores_n")
    private MenorMaiorTeores n_content;

    @JsonProperty("novo_teores_p")
    private MenorMaiorTeores p_content;

    @JsonProperty("novo_teores_k")
    private MenorMaiorTeores k_content;

    @JsonProperty("novo_teores_ca")
    private MenorMaiorTeores ca_content;

    @JsonProperty("novo_teores_mg")
    private MenorMaiorTeores mg_content;

    @JsonProperty("novo_teores_s")
    private MenorMaiorTeores s_content;

    @JsonProperty("novo_teores_b")
    private MenorMaiorTeores b_content;

    @JsonProperty("novo_teores_cu")
    private MenorMaiorTeores cu_content;

    @JsonProperty("novo_teores_fe")
    private MenorMaiorTeores fe_content;

    @JsonProperty("novo_teores_mn")
    private MenorMaiorTeores mn_content;

    @JsonProperty("novo_teores_mo")
    private MenorMaiorTeores mo_content;

    @JsonProperty("novo_teores_zn")
    private MenorMaiorTeores zn_content;
}