package com.migueltcc.fertintelligence.dto.recommendation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationFertigramaItemDto {

    @JsonProperty("rotulo")
    private String label;

    @JsonProperty("rotulo_curto")
    private String shortLabel;

    @JsonProperty("valor_analisado")
    private Double analyzedValue;

    @JsonProperty("unidade")
    private String unit;

    @JsonProperty("adequado_min")
    private Double adequateMin;

    @JsonProperty("adequado_max")
    private Double adequateMax;

    @JsonProperty("valor_normalizado")
    private Double normalizedValue;

    @JsonProperty("adequado_min_normalizado")
    private Double normalizedAdequateMin;

    @JsonProperty("adequado_max_normalizado")
    private Double normalizedAdequateMax;

    @JsonProperty("interpretacao")
    private String interpretation;

    @JsonProperty("faixa")
    private String rangeLabel;

    @JsonProperty("observacao")
    private String observation;
}
