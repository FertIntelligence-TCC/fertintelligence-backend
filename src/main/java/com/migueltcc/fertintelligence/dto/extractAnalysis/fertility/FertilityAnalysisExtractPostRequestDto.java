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
public class FertilityAnalysisExtractPostRequestDto {

    @JsonProperty("novo_ph_agua")
    @JsonAlias("ph_agua")
    Double phAgua;

    @JsonProperty("novo_ph_cacl2")
    @JsonAlias("ph_cacl2")
    Double phCacl2;

    @JsonProperty("novo_calcio")
    @JsonAlias("calcio")
    Double calcio;

    @JsonProperty("nova_unidade_calcio")
    @JsonAlias({"unidade_calcio", "unidadeCalcio"})
    FertilityAnalysisUnit unidadeCalcio;

    @JsonProperty("novo_magnesio")
    @JsonAlias("magnesio")
    Double magnesio;

    @JsonProperty("nova_unidade_magnesio")
    @JsonAlias({"unidade_magnesio", "unidadeMagnesio"})
    FertilityAnalysisUnit unidadeMagnesio;

    @JsonProperty("novo_potassio")
    @JsonAlias("potassio")
    Double potassio;

    @JsonProperty("nova_unidade_potassio")
    @JsonAlias({"unidade_potassio", "unidadePotassio"})
    FertilityAnalysisUnit unidadePotassio;

    @JsonProperty("novo_sodio")
    @JsonAlias("sodio")
    Double sodio;

    @JsonProperty("nova_unidade_sodio")
    @JsonAlias({"unidade_sodio", "unidadeSodio"})
    FertilityAnalysisUnit unidadeSodio;

    @JsonProperty("novo_aluminio")
    @JsonAlias("aluminio")
    Double aluminio;

    @JsonProperty("nova_unidade_aluminio")
    @JsonAlias({"unidade_aluminio", "unidadeAluminio"})
    FertilityAnalysisUnit unidadeAluminio;

    @JsonProperty("novo_aluminio_mais_hidrogenio")
    @JsonAlias("aluminio_mais_hidrogenio")
    Double aluminioMaisHidrogenio;

    @JsonProperty("nova_unidade_aluminio_mais_hidrogenio")
    @JsonAlias({"unidade_aluminio_mais_hidrogenio", "unidadeAluminioMaisHidrogenio"})
    FertilityAnalysisUnit unidadeAluminioMaisHidrogenio;

    @JsonProperty("nova_soma_bases")
    @JsonAlias("soma_bases")
    Double somaBases;

    @JsonProperty("nova_unidade_soma_bases")
    @JsonAlias({"unidade_soma_bases", "unidadeSomaBases"})
    FertilityAnalysisUnit unidadeSomaBases;

    @JsonProperty("nova_ctc_efetiva")
    @JsonAlias("ctc_efetiva")
    Double ctcEfetiva;

    @JsonProperty("nova_unidade_ctc_efetiva")
    @JsonAlias({"unidade_ctc_efetiva", "unidadeCtcEfetiva"})
    FertilityAnalysisUnit unidadeCtcEfetiva;

    @JsonProperty("nova_ctc_ph7")
    @JsonAlias("ctc_ph7")
    Double ctcPh7;

    @JsonProperty("nova_unidade_ctc_ph7")
    @JsonAlias({"unidade_ctc_ph7", "unidadeCtcPh7"})
    FertilityAnalysisUnit unidadeCtcPh7;

    @JsonProperty("nova_saturacao_bases_v")
    @JsonAlias("saturacao_bases_v")
    Double saturacaoBasesV;

    @JsonProperty("nova_saturacao_aluminio_m")
    @JsonAlias("saturacao_aluminio_m")
    Double saturacaoAluminioM;

    @JsonProperty("novo_fosforo_mehlich1")
    Double fosforoMehlich1;

    @JsonProperty("novo_fosforo_resina")
    Double fosforoResina;

    @JsonProperty("novo_enxofre")
    Double enxofre;

    @JsonProperty("nova_materia_organica")
    Double materiaOrganica;

    @JsonProperty("novo_boro")
    Double boro;

    @JsonProperty("novo_cobre")
    Double cobre;

    @JsonProperty("novo_ferro")
    Double ferro;

    @JsonProperty("novo_manganes")
    Double manganes;

    @JsonProperty("novo_molibdenio")
    Double molibdenio;

    @JsonProperty("novo_zinco")
    Double zinco;
}
