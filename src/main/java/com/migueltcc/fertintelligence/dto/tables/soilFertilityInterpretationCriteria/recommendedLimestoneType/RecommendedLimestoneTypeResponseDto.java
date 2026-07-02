package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedLimestoneTypeResponseDto {

    @Schema(description = "ID do registro")
    private Long id;
    @Schema(description = "ID da tabela pai")
    @JsonProperty("id_tabela")
    private Long tableId;
    @JsonProperty("nome_exibicao")
    private String displayName;

    @JsonProperty("relacao_ca_mg_baixa")
    private Double caMgLowRatio;
    @JsonProperty("relacao_ca_mg_media_menor_valor")
    private Double caMgMediumLowerValue;
    @JsonProperty("relacao_ca_mg_media_maior_valor")
    private Double caMgMediumHigherValue;
    @JsonProperty("relacao_ca_mg_alta")
    private Double caMgHighRatio;

    @JsonProperty("legenda_relacao_ca_mg_baixa")
    private String caMgLowLegend;
    @JsonProperty("legenda_relacao_ca_mg_media_menor_valor")
    private String caMgMediumLowerLegend;
    @JsonProperty("legenda_relacao_ca_mg_media_maior_valor")
    private String caMgMediumHigherLegend;
    @JsonProperty("legenda_relacao_ca_mg_alta")
    private String caMgHighLegend;

    @JsonProperty("observacoes")
    private String observations;
    @JsonProperty("fontes")
    private String sources;
}
