package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailableSPostRequestDto {

    @JsonProperty("novo_menor_teor_enxofre_argila_menor_400")
    @JsonAlias("menor_teor_enxofre_argila_menor_400")
    private Double sContentLess400TooLow;
    @JsonProperty("novo_teor_inicial_baixo_enxofre_argila_menor_400")
    @JsonAlias("teor_inicial_baixo_enxofre_argila_menor_400")
    private Double sContentLess400LowI;
    @JsonProperty("novo_teor_final_baixo_enxofre_argila_menor_400")
    @JsonAlias("teor_final_baixo_enxofre_argila_menor_400")
    private Double sContentLess400LowF;
    @JsonProperty("novo_teor_inicial_medio_enxofre_argila_menor_400")
    @JsonAlias("teor_inicial_medio_enxofre_argila_menor_400")
    private Double sContentLess400MediumI;
    @JsonProperty("novo_teor_final_medio_enxofre_argila_menor_400")
    @JsonAlias("teor_final_medio_enxofre_argila_menor_400")
    private Double sContentLess400MediumF;
    @JsonProperty("novo_teor_inicial_alto_enxofre_argila_menor_400")
    @JsonAlias("teor_inicial_alto_enxofre_argila_menor_400")
    private Double sContentLess400HighI;
    @JsonProperty("novo_teor_final_alto_enxofre_argila_menor_400")
    @JsonAlias("teor_final_alto_enxofre_argila_menor_400")
    private Double sContentLess400HighF;
    @JsonProperty("novo_maior_teor_enxofre_argila_menor_400")
    @JsonAlias("maior_teor_enxofre_argila_menor_400")
    private Double sContentLess400TooHigh;

    @JsonProperty("novo_menor_teor_enxofre_argila_maior_400")
    @JsonAlias("menor_teor_enxofre_argila_maior_400")
    private Double sContentGreater400TooLow;
    @JsonProperty("novo_teor_inicial_baixo_enxofre_argila_maior_400")
    @JsonAlias("teor_inicial_baixo_enxofre_argila_maior_400")
    private Double sContentGreater400LowI;
    @JsonProperty("novo_teor_final_baixo_enxofre_argila_maior_400")
    @JsonAlias("teor_final_baixo_enxofre_argila_maior_400")
    private Double sContentGreater400LowF;
    @JsonProperty("novo_teor_inicial_medio_enxofre_argila_maior_400")
    @JsonAlias("teor_inicial_medio_enxofre_argila_maior_400")
    private Double sContentGreater400MediumI;
    @JsonProperty("novo_teor_final_medio_enxofre_argila_maior_400")
    @JsonAlias("teor_final_medio_enxofre_argila_maior_400")
    private Double sContentGreater400MediumF;
    @JsonProperty("novo_teor_inicial_alto_enxofre_argila_maior_400")
    @JsonAlias("teor_inicial_alto_enxofre_argila_maior_400")
    private Double sContentGreater400HighI;
    @JsonProperty("novo_teor_final_alto_enxofre_argila_maior_400")
    @JsonAlias("teor_final_alto_enxofre_argila_maior_400")
    private Double sContentGreater400HighF;
    @JsonProperty("novo_maior_teor_enxofre_argila_maior_400")
    @JsonAlias("maior_teor_enxofre_argila_maior_400")
    private Double sContentGreater400TooHigh;

    @JsonProperty("novo_fonte_literatura")
    @JsonAlias({"fonte_literatura", "fonte"})
    private String literatureSource;
    @JsonProperty("novo_observacoes")
    @JsonAlias({"observacoes", "observacao"})
    private String observations;

}
