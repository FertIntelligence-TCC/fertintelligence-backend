package com.migueltcc.fertintelligence.dto.tables.coverage;

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
public class CoveragePostRequestDto {

    @JsonProperty("novo_ordem_cobertura")
    @Schema(example = "2")
    private Integer order;

    @JsonProperty("novo_aplicacao_recomendada_cobertura")
    @Schema(example = "25.0")
    private Double application;
}