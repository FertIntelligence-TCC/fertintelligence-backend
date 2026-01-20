package com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CropFoliarAnalysisInterpretationTableLineCreateRequestDto {

    @JsonProperty("nome_cultura")
    @NotNull
    @Schema(example = "SOJA")
    private NomeComum crop;

    // Nutrientes podem ser nulos no Model (nullable=true), então não levam @NotNull obrigatório aqui,
    // a menos que a regra de negócio exija todos preenchidos na criação.

    @JsonProperty("teores_n")
    private MenorMaiorTeores n_content;

    @JsonProperty("teores_p")
    private MenorMaiorTeores p_content;

    @JsonProperty("teores_k")
    private MenorMaiorTeores k_content;

    @JsonProperty("teores_ca")
    private MenorMaiorTeores ca_content;

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