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
public class CoverageResponseDto {

    @Schema(example = "201")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "101")
    @JsonProperty("id_intervalo_teor")
    private Long contentRangeId;

    @Schema(example = "1")
    @JsonProperty("ordem_cobertura")
    private Integer order;

    @Schema(example = "30.0")
    @JsonProperty("aplicacao_recomendada_cobertura")
    private Double application;

    @Schema(example = "kg/ha")
    @JsonProperty("unidade_aplicacao_recomendada_cobertura")
    private String applicationUnit;
}
