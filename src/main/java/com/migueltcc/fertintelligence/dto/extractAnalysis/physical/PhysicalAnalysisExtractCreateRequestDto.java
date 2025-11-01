package com.migueltcc.fertintelligence.dto.extractAnalysis.physical;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalAnalysisExtractCreateRequestDto {

    @JsonProperty("teor_areia")
    Double teorAreia;

    @JsonProperty("teor_silte")
    Double teorSilte;

    @JsonProperty("teor_argila")
    Double teorArgila;

    @JsonProperty("densidade_aparente")
    Double densidadeAparente;

    @JsonProperty("densidade_real")
    Double densidadeReal;

    @JsonProperty("porosidade_total")
    Double porosidadeTotal;

    @JsonProperty("microporosidade")
    Double microporosidade;

    @JsonProperty("umidade_capacidade_campo")
    Double umidadeCapacidadeCampo;

    @JsonProperty("umidade_ponto_murcha_permanente")
    Double umidadePontoMurchaPermanente;

    @JsonProperty("agua_disponivel")
    Double aguaDisponivel;

    @JsonProperty("resistencia_penetracao")
    Double resistenciaPenetracao;

    @JsonProperty("perc_agregados_6_0mm")
    Double percAgregados6_0mm;

    @JsonProperty("perc_agregados_4_1_a_6_0mm")
    Double percAgregados4_1a6_0mm;

    @JsonProperty("perc_agregados_2_1_a_4_0mm")
    Double percAgregados2_1a4_0mm;

    @JsonProperty("perc_agregados_1_0_a_2_0mm")
    Double percAgregados1_0a2_0mm;

    @JsonProperty("perc_agregados_menor_1_0mm")
    Double percAgregadosMenor1_0mm;
}