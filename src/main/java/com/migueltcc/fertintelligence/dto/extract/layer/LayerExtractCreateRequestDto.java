package com.migueltcc.fertintelligence.dto.extract.layer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayerExtractCreateRequestDto {

    @JsonProperty("profundidade_inicial")
    @NotNull
    Integer initialDepth;

    @JsonProperty("profundidade_final")
    @NotNull
    Integer finalDepth;

    @JsonProperty("camada")
    @NotNull
    Camada layer;

    @JsonProperty("subcamada")
    @NotNull
    Integer subLayer;
}