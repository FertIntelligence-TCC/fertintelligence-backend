package com.migueltcc.fertintelligence.dto.property;

import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocalizacaoDto {

    @Schema(example = "-15.7801")
    @JsonProperty("latitude")
    private Double latitude;

    @Schema(example = "SUL")
    @JsonProperty("latitudeDirection")
    private LatitudeDirection latitudeDirection;

    @Schema(example = "-47.9292")
    @JsonProperty("longitude")
    private Double longitude;

    @Schema(example = "OESTE")
    @JsonProperty("longitudeDirection")
    private LongitudeDirection longitudeDirection;

    @Schema(example = "1172.0")
    @JsonProperty("altitude")
    private Double altitude;
}