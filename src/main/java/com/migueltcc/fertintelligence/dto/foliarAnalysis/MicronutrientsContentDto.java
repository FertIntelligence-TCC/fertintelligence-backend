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
public class MicronutrientsContentDto {

    @Schema(example = "40.0")
    @JsonProperty("b_content")
    private Double b_content;

    @Schema(example = "10.0")
    @JsonProperty("cu_content")
    private Double cu_content;

    @Schema(example = "150.0")
    @JsonProperty("fe_content")
    private Double fe_content;

    @Schema(example = "0.1")
    @JsonProperty("ni_content")
    private Double ni_content;

    @Schema(example = "70.0")
    @JsonProperty("mn_content")
    private Double mn_content;

    @Schema(example = "0.5")
    @JsonProperty("mo_content")
    private Double mo_content;

    @Schema(example = "30.0")
    @JsonProperty("zn_content")
    private Double zn_content;
}