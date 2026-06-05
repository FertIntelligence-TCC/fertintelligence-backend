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

    @Schema(example = "Aplicar em cobertura com umidade adequada no solo.")
    @JsonProperty("observacoes")
    private String observations;

    @Schema(example = "Manual de adubação regional; boletim técnico local")
    @JsonProperty("fontes")
    private String sources;
}
