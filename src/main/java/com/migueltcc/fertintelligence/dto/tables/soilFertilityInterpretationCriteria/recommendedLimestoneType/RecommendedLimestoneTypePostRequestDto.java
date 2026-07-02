package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class RecommendedLimestoneTypePostRequestDto {

    @JsonProperty("relacao_ca_mg_baixa")
    @JsonAlias("novo_relacao_ca_mg_baixa")
    private Double caMgLowRatio;
    @JsonProperty("relacao_ca_mg_media_menor_valor")
    @JsonAlias("novo_relacao_ca_mg_media_menor_valor")
    private Double caMgMediumLowerValue;
    @JsonProperty("relacao_ca_mg_media_maior_valor")
    @JsonAlias("novo_relacao_ca_mg_media_maior_valor")
    private Double caMgMediumHigherValue;
    @JsonProperty("relacao_ca_mg_alta")
    @JsonAlias("novo_relacao_ca_mg_alta")
    private Double caMgHighRatio;

    @JsonProperty("novo_observacoes")
    @JsonAlias({"observacoes", "observacao"})
    private String observations;
    @JsonProperty("novo_fontes")
    @JsonAlias({"fontes", "fonte", "fonte_literatura"})
    private String sources;
}
