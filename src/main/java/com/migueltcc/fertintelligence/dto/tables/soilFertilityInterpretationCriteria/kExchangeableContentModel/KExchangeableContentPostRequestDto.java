package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel;

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
public class KExchangeableContentPostRequestDto {

    @JsonProperty("menor_teor_k")
    @JsonAlias("novo_menor_teor_k")
    private Double kContentTooLow;

    @JsonProperty("teor_inicial_baixo_k")
    @JsonAlias("novo_teor_inicial_baixo_k")
    private Double kContentLowI;

    @JsonProperty("teor_final_baixo_k")
    @JsonAlias("novo_teor_final_baixo_k")
    private Double kContentLowF;

    @JsonProperty("teor_inicial_medio_k")
    @JsonAlias("novo_teor_inicial_medio_k")
    private Double kContentMediumI;

    @JsonProperty("teor_final_medio_k")
    @JsonAlias("novo_teor_final_medio_k")
    private Double kContentMediumF;

    @JsonProperty("teor_inicial_alto_k")
    @JsonAlias("novo_teor_inicial_alto_k")
    private Double kContentHighI;

    @JsonProperty("teor_final_alto_k")
    @JsonAlias("novo_teor_final_alto_k")
    private Double kContentHighF;

    @JsonProperty("maior_teor_k")
    @JsonAlias("novo_maior_teor_k")
    private Double kContentTooHigh;
}
