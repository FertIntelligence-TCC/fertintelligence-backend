package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendedLimestoneTypeCreateRequestDto {

    @JsonProperty("relacao_ca_mg_baixa")
    private Double caMgLowRatio;
    @JsonProperty("relacao_ca_mg_alta")
    private Double caMgHighRatio;

    @JsonProperty("observacoes")
    private String observations;
    @JsonProperty("fontes")
    private String sources;
}
