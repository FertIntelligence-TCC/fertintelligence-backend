package com.migueltcc.fertintelligence.dto.extractAnalysis.physical;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis.PhysicalAnalysisUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhysicalAnalysisExtractCreateRequestDto {

    @JsonProperty("teor_areia")
    @JsonAlias("teorAreia")
    Double teorAreia;

    @JsonProperty("unidade_teor_areia")
    @JsonAlias("unidadeTeorAreia")
    PhysicalAnalysisUnit unidadeTeorAreia;

    @JsonProperty("teor_silte")
    @JsonAlias("teorSilte")
    Double teorSilte;

    @JsonProperty("unidade_teor_silte")
    @JsonAlias("unidadeTeorSilte")
    PhysicalAnalysisUnit unidadeTeorSilte;

    @JsonProperty("teor_argila")
    @JsonAlias("teorArgila")
    Double teorArgila;

    @JsonProperty("unidade_teor_argila")
    @JsonAlias("unidadeTeorArgila")
    PhysicalAnalysisUnit unidadeTeorArgila;

    @JsonProperty("densidade_aparente")
    @JsonAlias("densidadeAparente")
    Double densidadeAparente;

    @JsonProperty("unidade_densidade_aparente")
    @JsonAlias("unidadeDensidadeAparente")
    PhysicalAnalysisUnit unidadeDensidadeAparente;

    @JsonProperty("densidade_real")
    @JsonAlias("densidadeReal")
    Double densidadeReal;

    @JsonProperty("unidade_densidade_real")
    @JsonAlias("unidadeDensidadeReal")
    PhysicalAnalysisUnit unidadeDensidadeReal;

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

    @JsonProperty("perc_agregados_0_5_a_1_0mm")
    Double percAgregados0_5a1_0mm;

    @JsonProperty("perc_agregados_0_25_a_0_5mm")
    Double percAgregados0_25a0_5mm;

    @JsonProperty("perc_agregados_menor_0_25mm")
    Double percAgregadosMenor0_25mm;
}
