package com.migueltcc.fertintelligence.dto.tables.contentRange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
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
public class ContentRangeCreateRequestDto {

    @JsonProperty("nutriente")
    @NotNull
    @Schema(example = "FOSFORO")
    private Nutriente nutrient;

    @JsonProperty("ordem_teor")
    @NotNull
    @Schema(example = "1")
    private Integer order;

    @JsonProperty("menor_teor")
    @Schema(example = "10.0")
    private Double smallest;

    @JsonProperty("maior_teor")
    @Schema(example = "5.0")
    private Double largest;

    @JsonProperty("aplicacao_recomendada_plantio")
    @Schema(example = "80.0")
    private Double application;
}