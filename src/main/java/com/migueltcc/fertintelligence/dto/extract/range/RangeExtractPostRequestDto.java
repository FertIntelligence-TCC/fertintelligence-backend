package com.migueltcc.fertintelligence.dto.extract.range;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RangeExtractPostRequestDto {

    @Schema(example = "0")
    @JsonProperty("nova_profundidade_inicial")
    Integer initialDepth;

    @Schema(example = "20")
    @JsonProperty("nova_profundidade_final")
    Integer finalDepth;
}