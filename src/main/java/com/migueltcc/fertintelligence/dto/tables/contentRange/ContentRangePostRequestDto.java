package com.migueltcc.fertintelligence.dto.tables.contentRange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageCreateRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentRangePostRequestDto {

    @JsonProperty("novo_nutriente")
    @Schema(example = "POTASSIO")
    private Nutriente nutrient;

    @JsonProperty("novo_ordem_teor")
    @Schema(example = "2")
    private Integer order;

    @JsonProperty("novo_menor_teor")
    @Schema(example = "15.0")
    private Double smallest;

    @JsonProperty("novo_maior_teor")
    @Schema(example = "10.1")
    private Double largest;

    @JsonProperty("novo_aplicacao_recomendada_plantio")
    @Schema(example = "60.0")
    private Double application;

    @JsonProperty("coberturas")
    private List<CoverageCreateRequestDto> coverages;
}
