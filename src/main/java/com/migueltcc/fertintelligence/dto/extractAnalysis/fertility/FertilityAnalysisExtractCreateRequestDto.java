package com.migueltcc.fertintelligence.dto.extractAnalysis.fertility;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FertilityAnalysisExtractCreateRequestDto {

    @JsonProperty("ph_agua")
    Double phAgua;

    @JsonProperty("ph_cacl2")
    Double phCacl2;

    @JsonProperty("calcio")
    Double calcio;

    @JsonProperty("unidade_calcio")
    @JsonAlias("unidadeCalcio")
    FertilityAnalysisUnit unidadeCalcio;

    @JsonProperty("magnesio")
    Double magnesio;

    @JsonProperty("unidade_magnesio")
    @JsonAlias("unidadeMagnesio")
    FertilityAnalysisUnit unidadeMagnesio;

    @JsonProperty("potassio")
    Double potassio;

    @JsonProperty("unidade_potassio")
    @JsonAlias("unidadePotassio")
    FertilityAnalysisUnit unidadePotassio;

    @JsonProperty("sodio")
    Double sodio;

    @JsonProperty("unidade_sodio")
    @JsonAlias("unidadeSodio")
    FertilityAnalysisUnit unidadeSodio;

    @JsonProperty("aluminio")
    Double aluminio;

    @JsonProperty("unidade_aluminio")
    @JsonAlias("unidadeAluminio")
    FertilityAnalysisUnit unidadeAluminio;

    @JsonProperty("aluminio_mais_hidrogenio")
    Double aluminioMaisHidrogenio;

    @JsonProperty("unidade_aluminio_mais_hidrogenio")
    @JsonAlias("unidadeAluminioMaisHidrogenio")
    FertilityAnalysisUnit unidadeAluminioMaisHidrogenio;

    @JsonProperty("soma_bases")
    Double somaBases;

    @JsonProperty("unidade_soma_bases")
    @JsonAlias("unidadeSomaBases")
    FertilityAnalysisUnit unidadeSomaBases;

    @JsonProperty("ctc_efetiva")
    Double ctcEfetiva;

    @JsonProperty("unidade_ctc_efetiva")
    @JsonAlias("unidadeCtcEfetiva")
    FertilityAnalysisUnit unidadeCtcEfetiva;

    @JsonProperty("ctc_ph7")
    Double ctcPh7;

    @JsonProperty("unidade_ctc_ph7")
    @JsonAlias("unidadeCtcPh7")
    FertilityAnalysisUnit unidadeCtcPh7;

    @JsonProperty("saturacao_bases_v")
    Double saturacaoBasesV;

    @JsonProperty("saturacao_aluminio_m")
    Double saturacaoAluminioM;

    @JsonProperty("fosforo_mehlich1")
    Double fosforoMehlich1;

    @JsonProperty("fosforo_resina")
    Double fosforoResina;

    @JsonProperty("enxofre")
    Double enxofre;

    @JsonProperty("materia_organica")
    Double materiaOrganica;

    @JsonProperty("boro")
    Double boro;

    @JsonProperty("cobre")
    Double cobre;

    @JsonProperty("ferro")
    Double ferro;

    @JsonProperty("manganes")
    Double manganes;

    @JsonProperty("molibdenio")
    Double molibdenio;

    @JsonProperty("zinco")
    Double zinco;
}
