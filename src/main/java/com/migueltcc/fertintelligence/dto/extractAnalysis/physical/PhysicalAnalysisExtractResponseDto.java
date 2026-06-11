package com.migueltcc.fertintelligence.dto.extractAnalysis.physical;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PhysicalAnalysisExtractResponseDto {

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

    @Schema(example = "450.0")
    @JsonProperty("teor_areia")
    Double teorAreia;

    @Schema(example = "200.0")
    @JsonProperty("teor_silte")
    Double teorSilte;

    @Schema(example = "350.0")
    @JsonProperty("teor_argila")
    Double teorArgila;

    @Schema(example = "1.2")
    @JsonProperty("densidade_aparente")
    Double densidadeAparente;

    @Schema(example = "2.6")
    @JsonProperty("densidade_real")
    Double densidadeReal;

    @Schema(example = "45.0")
    @JsonProperty("porosidade_total")
    Double porosidadeTotal;

    @Schema(example = "30.0")
    @JsonProperty("microporosidade")
    Double microporosidade;

    @Schema(example = "28.0")
    @JsonProperty("umidade_capacidade_campo")
    Double umidadeCapacidadeCampo;

    @Schema(example = "15.0")
    @JsonProperty("umidade_ponto_murcha_permanente")
    Double umidadePontoMurchaPermanente;

    @Schema(example = "13.0")
    @JsonProperty("agua_disponivel")
    Double aguaDisponivel;

    @Schema(example = "2.1")
    @JsonProperty("resistencia_penetracao")
    Double resistenciaPenetracao;

    @Schema(example = "5.0")
    @JsonProperty("perc_agregados_6_0mm")
    Double percAgregados6_0mm;

    @Schema(example = "10.0")
    @JsonProperty("perc_agregados_4_1_a_6_0mm")
    Double percAgregados4_1a6_0mm;

    @Schema(example = "15.0")
    @JsonProperty("perc_agregados_2_1_a_4_0mm")
    Double percAgregados2_1a4_0mm;

    @Schema(example = "20.0")
    @JsonProperty("perc_agregados_1_0_a_2_0mm")
    Double percAgregados1_0a2_0mm;

    @Schema(example = "50.0")
    @JsonProperty("perc_agregados_menor_1_0mm")
    Double percAgregadosMenor1_0mm;

    @Schema(example = "2.34")
    @JsonProperty("dmp")
    Double dmp;
}