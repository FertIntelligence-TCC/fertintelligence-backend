package com.migueltcc.fertintelligence.dto.tables.contentRange;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentRangeResponseDto {

    @Schema(example = "101")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "10")
    @JsonProperty("id_tabela")
    private Long tableId;

    @Schema(example = "FOSFORO")
    @JsonProperty("nutriente")
    private Nutriente nutrient;

    @Schema(example = "Fósforo (P) disponível")
    @JsonProperty("descricao_teor")
    private String contentDescription;

    @Schema(example = "1")
    @JsonProperty("ordem_teor")
    private Integer order;

    @Schema(example = "10.0")
    @JsonProperty("menor_teor")
    private Double smallest;

    @Schema(example = "5.0")
    @JsonProperty("maior_teor")
    private Double largest;

    @Schema(example = "mg/dm³")
    @JsonProperty("unidade_teor")
    private String contentUnit;

    @Schema(example = "80.0")
    @JsonProperty("aplicacao_recomendada_plantio")
    private Double application;

    @Schema(example = "kg/ha")
    @JsonProperty("unidade_aplicacao_recomendada_plantio")
    private String applicationUnit;
}
