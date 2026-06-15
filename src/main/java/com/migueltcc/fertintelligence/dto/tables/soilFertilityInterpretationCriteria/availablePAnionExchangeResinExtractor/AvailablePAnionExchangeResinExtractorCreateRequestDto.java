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
public class AvailablePAnionExchangeResinExtractorCreateRequestDto {

    @JsonProperty("muito_baixo")
    private Double pContentTooLow;

    @JsonProperty("baixo_menor")
    private Double pContentLowI;

    @JsonProperty("baixo_maior")
    private Double pContentLowF;

    @JsonProperty("medio_menor")
    private Double pContentMediumI;

    @JsonProperty("medio_maior")
    private Double pContentMediumF;

    @JsonProperty("alto_menor")
    private Double pContentHighI;

    @JsonProperty("alto_maior")
    private Double pContentHighF;

    @JsonProperty("muito_alto")
    private Double pContentTooHigh;
}
