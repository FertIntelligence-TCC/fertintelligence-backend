package com.migueltcc.fertintelligence.dto.foliarAnalysis;

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
public class MacronutrientsContentDto {

    @Schema(example = "3.5")
    @JsonProperty("n_content")
    private Double n_content;

    @Schema(example = "0.2")
    @JsonProperty("p_content")
    private Double p_content;

    @Schema(example = "1.8")
    @JsonProperty("k_content")
    private Double k_content;

    @Schema(example = "1.0")
    @JsonProperty("ca_content")
    private Double ca_content;

    @Schema(example = "0.3")
    @JsonProperty("mg_content")
    private Double mg_content;

    @Schema(example = "0.15")
    @JsonProperty("s_content")
    private Double s_content;
}