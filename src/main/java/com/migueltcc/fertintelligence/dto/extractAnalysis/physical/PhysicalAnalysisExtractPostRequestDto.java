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
public class PhysicalAnalysisExtractPostRequestDto {

    @JsonProperty("novo_teor_areia")
    @JsonAlias({"teor_areia", "teorAreia"})
    Double teorAreia;

    @JsonProperty("nova_unidade_teor_areia")
    @JsonAlias({"unidade_teor_areia", "unidadeTeorAreia"})
    PhysicalAnalysisUnit unidadeTeorAreia;

    @JsonProperty("novo_teor_silte")
    @JsonAlias({"teor_silte", "teorSilte"})
    Double teorSilte;

    @JsonProperty("nova_unidade_teor_silte")
    @JsonAlias({"unidade_teor_silte", "unidadeTeorSilte"})
    PhysicalAnalysisUnit unidadeTeorSilte;

    @JsonProperty("novo_teor_argila")
    @JsonAlias({"teor_argila", "teorArgila"})
    Double teorArgila;

    @JsonProperty("nova_unidade_teor_argila")
    @JsonAlias({"unidade_teor_argila", "unidadeTeorArgila"})
    PhysicalAnalysisUnit unidadeTeorArgila;

    @JsonProperty("nova_densidade_aparente")
    @JsonAlias({"densidade_aparente", "densidadeAparente"})
    Double densidadeAparente;

    @JsonProperty("nova_unidade_densidade_aparente")
    @JsonAlias({"unidade_densidade_aparente", "unidadeDensidadeAparente"})
    PhysicalAnalysisUnit unidadeDensidadeAparente;

    @JsonProperty("nova_densidade_real")
    @JsonAlias({"densidade_real", "densidadeReal"})
    Double densidadeReal;

    @JsonProperty("nova_unidade_densidade_real")
    @JsonAlias({"unidade_densidade_real", "unidadeDensidadeReal"})
    PhysicalAnalysisUnit unidadeDensidadeReal;

    @JsonProperty("nova_porosidade_total")
    Double porosidadeTotal;

    @JsonProperty("nova_microporosidade")
    Double microporosidade;

    @JsonProperty("nova_umidade_capacidade_campo")
    Double umidadeCapacidadeCampo;

    @JsonProperty("nova_umidade_ponto_murcha_permanente")
    Double umidadePontoMurchaPermanente;

    @JsonProperty("nova_agua_disponivel")
    Double aguaDisponivel;

    @JsonProperty("nova_resistencia_penetracao")
    Double resistenciaPenetracao;

    @JsonProperty("novo_perc_agregados_6_0mm")
    Double percAgregados6_0mm;

    @JsonProperty("novo_perc_agregados_4_1_a_6_0mm")
    Double percAgregados4_1a6_0mm;

    @JsonProperty("novo_perc_agregados_2_1_a_4_0mm")
    Double percAgregados2_1a4_0mm;

    @JsonProperty("novo_perc_agregados_1_0_a_2_0mm")
    Double percAgregados1_0a2_0mm;

    @JsonProperty("novo_perc_agregados_0_5_a_1_0mm")
    Double percAgregados0_5a1_0mm;

    @JsonProperty("novo_perc_agregados_0_25_a_0_5mm")
    Double percAgregados0_25a0_5mm;

    @JsonProperty("novo_perc_agregados_menor_0_25mm")
    Double percAgregadosMenor0_25mm;
}
