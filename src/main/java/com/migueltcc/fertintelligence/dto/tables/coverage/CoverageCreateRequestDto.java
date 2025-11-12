package com.migueltcc.fertintelligence.dto.tables.coverage;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CoverageCreateRequestDto {

    @JsonProperty("ordem_cobertura")
    @NotNull
    @Schema(example = "1")
    private Integer order;

    @JsonProperty("aplicacao_recomendada_cobertura")
    @NotNull
    @Schema(example = "30.0")
    private Double application;
}