package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor;

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
public class AvailablePAnionExchangeResinExtractorPostRequestDto {

    @JsonProperty("muito_baixo")
    @JsonAlias("novo_muito_baixo")
    private Double pContentTooLow;

    @JsonProperty("baixo_menor")
    @JsonAlias("novo_baixo_menor")
    private Double pContentLowI;

    @JsonProperty("baixo_maior")
    @JsonAlias("novo_baixo_maior")
    private Double pContentLowF;

    @JsonProperty("medio_menor")
    @JsonAlias("novo_medio_menor")
    private Double pContentMediumI;

    @JsonProperty("medio_maior")
    @JsonAlias("novo_medio_maior")
    private Double pContentMediumF;

    @JsonProperty("alto_menor")
    @JsonAlias("novo_alto_menor")
    private Double pContentHighI;

    @JsonProperty("alto_maior")
    @JsonAlias("novo_alto_maior")
    private Double pContentHighF;

    @JsonProperty("muito_alto")
    @JsonAlias("novo_muito_alto")
    private Double pContentTooHigh;
}
