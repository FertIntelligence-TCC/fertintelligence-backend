package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableBaseRatio;

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
public class ExchangeableBaseRatioResponseDto {

    @Schema(description = "ID do registro")
    private Long id;
    @Schema(description = "ID da tabela pai")
    @JsonProperty("id_tabela")
    private Long tableId;
    @JsonProperty("nome_exibicao")
    private String displayName;
    @JsonProperty("unidade")
    private String unit;

    @JsonProperty("relacao_ca_mg_baixo")
    private Double caMgLow;
    @JsonProperty("relacao_ca_mg_medio_menor_relacao")
    private Double caMgMediumLowerRatio;
    @JsonProperty("relacao_ca_mg_medio_maior_relacao")
    private Double caMgMediumHigherRatio;
    @JsonProperty("relacao_ca_mg_adequado_menor_relacao")
    private Double caMgAdequateLowerRatio;
    @JsonProperty("relacao_ca_mg_adequado_maior_relacao")
    private Double caMgAdequateHigherRatio;
    @JsonProperty("relacao_ca_mg_alto")
    private Double caMgHigh;

    @JsonProperty("relacao_ca_k_baixo")
    private Double caKLow;
    @JsonProperty("relacao_ca_k_medio_menor_relacao")
    private Double caKMediumLowerRatio;
    @JsonProperty("relacao_ca_k_medio_maior_relacao")
    private Double caKMediumHigherRatio;
    @JsonProperty("relacao_ca_k_adequado_menor_relacao")
    private Double caKAdequateLowerRatio;
    @JsonProperty("relacao_ca_k_adequado_maior_relacao")
    private Double caKAdequateHigherRatio;
    @JsonProperty("relacao_ca_k_alto")
    private Double caKHigh;

    @JsonProperty("relacao_mg_k_baixo")
    private Double mgKLow;
    @JsonProperty("relacao_mg_k_medio_menor_relacao")
    private Double mgKMediumLowerRatio;
    @JsonProperty("relacao_mg_k_medio_maior_relacao")
    private Double mgKMediumHigherRatio;
    @JsonProperty("relacao_mg_k_adequado_menor_relacao")
    private Double mgKAdequateLowerRatio;
    @JsonProperty("relacao_mg_k_adequado_maior_relacao")
    private Double mgKAdequateHigherRatio;
    @JsonProperty("relacao_mg_k_alto")
    private Double mgKHigh;

    @JsonProperty("relacao_ca_mg_sobre_k_baixo")
    private Double caMgKLow;
    @JsonProperty("relacao_ca_mg_sobre_k_medio_menor_relacao")
    private Double caMgKMediumLowerRatio;
    @JsonProperty("relacao_ca_mg_sobre_k_medio_maior_relacao")
    private Double caMgKMediumHigherRatio;
    @JsonProperty("relacao_ca_mg_sobre_k_adequado_menor_relacao")
    private Double caMgKAdequateLowerRatio;
    @JsonProperty("relacao_ca_mg_sobre_k_adequado_maior_relacao")
    private Double caMgKAdequateHigherRatio;
    @JsonProperty("relacao_ca_mg_sobre_k_alto")
    private Double caMgKHigh;

    @JsonProperty("observacoes")
    private String observations;
    @JsonProperty("fontes")
    private String sources;
}
