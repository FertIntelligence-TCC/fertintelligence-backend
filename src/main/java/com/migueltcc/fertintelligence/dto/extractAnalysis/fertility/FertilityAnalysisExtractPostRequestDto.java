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
public class FertilityAnalysisExtractPostRequestDto {

    @JsonProperty("novo_ph_agua")
    Double phAgua;

    @JsonProperty("novo_ph_cacl2")
    Double phCacl2;

    @JsonProperty("novo_calcio")
    Double calcio;

    @JsonProperty("novo_magnesio")
    Double magnesio;

    @JsonProperty("novo_potassio")
    Double potassio;

    @JsonProperty("novo_sodio")
    Double sodio;

    @JsonProperty("novo_aluminio")
    Double aluminio;

    @JsonProperty("novo_aluminio_mais_hidrogenio")
    Double aluminioMaisHidrogenio;

    @JsonProperty("nova_soma_bases")
    Double somaBases;

    @JsonProperty("nova_ctc_efetiva")
    Double ctcEfetiva;

    @JsonProperty("nova_ctc_ph7")
    Double ctcPh7;

    @JsonProperty("nova_saturacao_bases_v")
    Double saturacaoBasesV;

    @JsonProperty("nova_saturacao_aluminio_m")
    Double saturacaoAluminioM;

    @JsonProperty("novo_pst")
    Double pst;

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