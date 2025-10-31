package com.migueltcc.fertintelligence.dto.extract.layer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.Camada;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.TipoExtrato;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LayerExtractResponseDto {

    @Schema(example = "404")
    @JsonProperty("id")
    Long id;

    @Schema(example = "0")
    @JsonProperty("profundidade_inicial")
    Integer initialDepth;

    @Schema(example = "20")
    @JsonProperty("profundidade_final")
    Integer finalDepth;

    @Schema(example = "A")
    @JsonProperty("camada")
    Camada layer;

    @Schema(example = "1")
    @JsonProperty("subcamada")
    Integer subLayer;

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