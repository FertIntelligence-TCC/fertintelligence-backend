package com.migueltcc.fertintelligence.dto.extract.layer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.Camada;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayerExtractPostRequestDto {

    @Schema(example = "0")
    @JsonProperty("nova_profundidade_inicial")
    Integer initialDepth;

    @Schema(example = "20")
    @JsonProperty("nova_profundidade_final")
    Integer finalDepth;

    @Schema(example = "A")
    @JsonProperty("nova_camada")
    Camada layer;

    @Schema(example = "1")
    @JsonProperty("nova_subcamada")
    Integer subLayer;
}