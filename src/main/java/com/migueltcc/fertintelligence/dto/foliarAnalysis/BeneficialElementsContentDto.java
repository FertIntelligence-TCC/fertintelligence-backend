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
public class BeneficialElementsContentDto {

    @Schema(example = "5.0")
    @JsonProperty("na_content")
    private Double na_content;

    @Schema(example = "20.0")
    @JsonProperty("si_content")
    private Double si_content;

    @Schema(example = "0.02")
    @JsonProperty("v_content")
    private Double v_content;

    @Schema(example = "0.01")
    @JsonProperty("co_content")
    private Double co_content;

    @Schema(example = "0.05")
    @JsonProperty("se_content")
    private Double se_content;
}