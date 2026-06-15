package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailablePAnionExchangeResinExtractorPostRequestDto {

    @JsonProperty("novo_muito_baixo")
    private Double pContentTooLow;

    @JsonProperty("novo_baixo_menor")
    private Double pContentLowI;

    @JsonProperty("novo_baixo_maior")
    private Double pContentLowF;

    @JsonProperty("novo_medio_menor")
    private Double pContentMediumI;

    @JsonProperty("novo_medio_maior")
    private Double pContentMediumF;

    @JsonProperty("novo_alto_menor")
    private Double pContentHighI;

    @JsonProperty("novo_alto_maior")
    private Double pContentHighF;

    @JsonProperty("novo_muito_alto")
    private Double pContentTooHigh;
}
