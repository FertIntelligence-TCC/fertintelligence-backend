package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS;

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
public class AvailableSCreateRequestDto {

    @JsonProperty("menor_teor_enxofre_argila_menor_400")
    private Double sContentLess400TooLow;
    @JsonProperty("teor_inicial_baixo_enxofre_argila_menor_400")
    private Double sContentLess400LowI;
    @JsonProperty("teor_final_baixo_enxofre_argila_menor_400")
    private Double sContentLess400LowF;
    @JsonProperty("teor_inicial_medio_enxofre_argila_menor_400")
    private Double sContentLess400MediumI;
    @JsonProperty("teor_final_medio_enxofre_argila_menor_400")
    private Double sContentLess400MediumF;
    @JsonProperty("teor_inicial_alto_enxofre_argila_menor_400")
    private Double sContentLess400HighI;
    @JsonProperty("teor_final_alto_enxofre_argila_menor_400")
    private Double sContentLess400HighF;
    @JsonProperty("maior_teor_enxofre_argila_menor_400")
    private Double sContentLess400TooHigh;

    @JsonProperty("menor_teor_enxofre_argila_maior_400")
    private Double sContentGreater400TooLow;
    @JsonProperty("teor_inicial_baixo_enxofre_argila_maior_400")
    private Double sContentGreater400LowI;
    @JsonProperty("teor_final_baixo_enxofre_argila_maior_400")
    private Double sContentGreater400LowF;
    @JsonProperty("teor_inicial_medio_enxofre_argila_maior_400")
    private Double sContentGreater400MediumI;
    @JsonProperty("teor_final_medio_enxofre_argila_maior_400")
    private Double sContentGreater400MediumF;
    @JsonProperty("teor_inicial_alto_enxofre_argila_maior_400")
    private Double sContentGreater400HighI;
    @JsonProperty("teor_final_alto_enxofre_argila_maior_400")
    private Double sContentGreater400HighF;
    @JsonProperty("maior_teor_enxofre_argila_maior_400")
    private Double sContentGreater400TooHigh;

    @JsonProperty("fonte_literatura")
    private String literatureSource;
    @JsonProperty("observacoes")
    private String observations;

}
