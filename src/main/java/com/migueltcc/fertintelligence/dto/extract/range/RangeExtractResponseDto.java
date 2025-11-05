package com.migueltcc.fertintelligence.dto.extract.range;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RangeExtractResponseDto {

    @Schema(example = "404")
    @JsonProperty("id")
    Long id;

    @Schema(example = "0")
    @JsonProperty("profundidade_inicial")
    Integer initialDepth;

    @Schema(example = "20")
    @JsonProperty("profundidade_final")
    Integer finalDepth;

    @Schema(example = "101")
    @JsonProperty("id_analise")
    Long analysisId;

    @Schema(example = "2018")
    @JsonProperty("ano_analise")
    Integer analysisYear;

    @Schema(example = "Embrapa Algodão")
    @JsonProperty("laboratorio_responsavel")
    String responsibleLaboratory;

}