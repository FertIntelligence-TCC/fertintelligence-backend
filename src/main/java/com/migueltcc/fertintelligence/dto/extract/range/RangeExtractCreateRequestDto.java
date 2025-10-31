package com.migueltcc.fertintelligence.dto.extract.range;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RangeExtractCreateRequestDto {

    @JsonProperty("profundidade_inicial")
    @NotNull
    Integer initialDepth;

    @JsonProperty("profundidade_final")
    @NotNull
    Integer finalDepth;
}