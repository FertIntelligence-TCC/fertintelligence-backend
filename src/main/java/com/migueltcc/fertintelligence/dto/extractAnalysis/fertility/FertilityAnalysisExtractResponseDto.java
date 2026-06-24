package com.migueltcc.fertintelligence.dto.extractAnalysis.fertility;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FertilityAnalysisExtractResponseDto {

    @Schema(example = "404")
    @JsonProperty("id")
    Long id;

    @Schema(example = "101")
    @JsonProperty("id_extrato_intervalo")
    Long rangeExtractId;

    @Schema(example = "202")
    @JsonProperty("id_extrato_camada")
    Long layerExtractId;

    @Schema(example = "0")
    @JsonProperty("profundidade_inicial")
    Integer initialDepth;

    @Schema(example = "20")
    @JsonProperty("profundidade_final")
    Integer finalDepth;

    @Schema(example = "A")
    @JsonProperty("camada")
    Camada layer;

    @Schema(example = "1")
    @JsonProperty("subcamada")
    Integer subLayer;

    @Schema(example = "5.6")
    @JsonProperty("ph_agua")
    Double phAgua;

    @Schema(example = "5.2")
    @JsonProperty("ph_cacl2")
    Double phCacl2;

    @Schema(example = "45.2")
    @JsonProperty("calcio")
    Double calcio;

    @Schema(example = "mmolc/dm³")
    @JsonProperty("unidade_calcio")
    FertilityAnalysisUnit unidadeCalcio;

    @Schema(example = "20.1")
    @JsonProperty("magnesio")
    Double magnesio;

    @Schema(example = "mmolc/dm³")
    @JsonProperty("unidade_magnesio")
    FertilityAnalysisUnit unidadeMagnesio;

    @Schema(example = "3.5")
    @JsonProperty("potassio")
    Double potassio;

    @Schema(example = "mmolc/dm³")
    @JsonProperty("unidade_potassio")
    FertilityAnalysisUnit unidadePotassio;

    @Schema(example = "2.4")
    @JsonProperty("sodio")
    Double sodio;

    @Schema(example = "mmolc/dm³")
    @JsonProperty("unidade_sodio")
    FertilityAnalysisUnit unidadeSodio;

    @Schema(example = "0.5")
    @JsonProperty("aluminio")
    Double aluminio;

    @Schema(example = "mmolc/dm³")
    @JsonProperty("unidade_aluminio")
    FertilityAnalysisUnit unidadeAluminio;

    @Schema(example = "4.2")
    @JsonProperty("aluminio_mais_hidrogenio")
    Double aluminioMaisHidrogenio;

    @Schema(example = "mmolc/dm³")
    @JsonProperty("unidade_aluminio_mais_hidrogenio")
    FertilityAnalysisUnit unidadeAluminioMaisHidrogenio;

    @Schema(example = "69.5")
    @JsonProperty("soma_bases")
    Double somaBases;

    @Schema(example = "mmolc/dm³")
    @JsonProperty("unidade_soma_bases")
    FertilityAnalysisUnit unidadeSomaBases;

    @Schema(example = "72.4")
    @JsonProperty("ctc_efetiva")
    Double ctcEfetiva;

    @Schema(example = "mmolc/dm³")
    @JsonProperty("unidade_ctc_efetiva")
    FertilityAnalysisUnit unidadeCtcEfetiva;

    @Schema(example = "89.1")
    @JsonProperty("ctc_ph7")
    Double ctcPh7;

    @Schema(example = "mmolc/dm³")
    @JsonProperty("unidade_ctc_ph7")
    FertilityAnalysisUnit unidadeCtcPh7;

    @Schema(example = "70.0")
    @JsonProperty("saturacao_bases_v")
    Double saturacaoBasesV;

    @Schema(example = "10.0")
    @JsonProperty("saturacao_aluminio_m")
    Double saturacaoAluminioM;

    @Schema(example = "2.7")
    @JsonProperty("pst")
    Double pst;

    @Schema(example = "15.2")
    @JsonProperty("fosforo_mehlich1")
    Double fosforoMehlich1;

    @Schema(example = "18.4")
    @JsonProperty("fosforo_resina")
    Double fosforoResina;

    @Schema(example = "22.0")
    @JsonProperty("enxofre")
    Double enxofre;

    @Schema(example = "3.2")
    @JsonProperty("materia_organica")
    Double materiaOrganica;

    @Schema(example = "0.35")
    @JsonProperty("boro")
    Double boro;

    @Schema(example = "1.8")
    @JsonProperty("cobre")
    Double cobre;

    @Schema(example = "25.0")
    @JsonProperty("ferro")
    Double ferro;

    @Schema(example = "12.3")
    @JsonProperty("manganes")
    Double manganes;

    @Schema(example = "0.1")
    @JsonProperty("molibdenio")
    Double molibdenio;

    @Schema(example = "3.4")
    @JsonProperty("zinco")
    Double zinco;
}
