package com.migueltcc.fertintelligence.dto.extractAnalysis.fertility;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FertilityAnalysisExtractCreateRequestDto {

    @JsonProperty("ph_agua")
    Double phAgua;

    @JsonProperty("ph_cacl2")
    Double phCacl2;

    @JsonProperty("calcio")
    Double calcio;

    @JsonProperty("magnesio")
    Double magnesio;

    @JsonProperty("potassio")
    Double potassio;

    @JsonProperty("sodio")
    Double sodio;

    @JsonProperty("aluminio")
    Double aluminio;

    @JsonProperty("aluminio_mais_hidrogenio")
    Double aluminioMaisHidrogenio;

    @JsonProperty("soma_bases")
    Double somaBases;

    @JsonProperty("ctc_efetiva")
    Double ctcEfetiva;

    @JsonProperty("ctc_ph7")
    Double ctcPh7;

    @JsonProperty("saturacao_bases_v")
    Double saturacaoBasesV;

    @JsonProperty("saturacao_aluminio_m")
    Double saturacaoAluminioM;

    @JsonProperty("pst")
    Double pst;

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