package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.ctcSaturation;

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
public class CtcSaturationPostRequestDto {

    @JsonProperty("percentual_k_baixo")
    @JsonAlias("novo_percentual_k_baixo")
    private Double kLow;
    @JsonProperty("percentual_k_medio_menor_teor")
    @JsonAlias("novo_percentual_k_medio_menor_teor")
    private Double kMediumLowerContent;
    @JsonProperty("percentual_k_medio_maior_teor")
    @JsonAlias("novo_percentual_k_medio_maior_teor")
    private Double kMediumHigherContent;
    @JsonProperty("percentual_k_adequado_menor_teor")
    @JsonAlias("novo_percentual_k_adequado_menor_teor")
    private Double kAdequateLowerContent;
    @JsonProperty("percentual_k_adequado_maior_teor")
    @JsonAlias("novo_percentual_k_adequado_maior_teor")
    private Double kAdequateHigherContent;
    @JsonProperty("percentual_k_alto")
    @JsonAlias("novo_percentual_k_alto")
    private Double kHigh;

    @JsonProperty("percentual_ca_baixo")
    @JsonAlias("novo_percentual_ca_baixo")
    private Double caLow;
    @JsonProperty("percentual_ca_medio_menor_teor")
    @JsonAlias("novo_percentual_ca_medio_menor_teor")
    private Double caMediumLowerContent;
    @JsonProperty("percentual_ca_medio_maior_teor")
    @JsonAlias("novo_percentual_ca_medio_maior_teor")
    private Double caMediumHigherContent;
    @JsonProperty("percentual_ca_adequado_menor_teor")
    @JsonAlias("novo_percentual_ca_adequado_menor_teor")
    private Double caAdequateLowerContent;
    @JsonProperty("percentual_ca_adequado_maior_teor")
    @JsonAlias("novo_percentual_ca_adequado_maior_teor")
    private Double caAdequateHigherContent;
    @JsonProperty("percentual_ca_alto")
    @JsonAlias("novo_percentual_ca_alto")
    private Double caHigh;

    @JsonProperty("percentual_mg_baixo")
    @JsonAlias("novo_percentual_mg_baixo")
    private Double mgLow;
    @JsonProperty("percentual_mg_medio_menor_teor")
    @JsonAlias("novo_percentual_mg_medio_menor_teor")
    private Double mgMediumLowerContent;
    @JsonProperty("percentual_mg_medio_maior_teor")
    @JsonAlias("novo_percentual_mg_medio_maior_teor")
    private Double mgMediumHigherContent;
    @JsonProperty("percentual_mg_adequado_menor_teor")
    @JsonAlias("novo_percentual_mg_adequado_menor_teor")
    private Double mgAdequateLowerContent;
    @JsonProperty("percentual_mg_adequado_maior_teor")
    @JsonAlias("novo_percentual_mg_adequado_maior_teor")
    private Double mgAdequateHigherContent;
    @JsonProperty("percentual_mg_alto")
    @JsonAlias("novo_percentual_mg_alto")
    private Double mgHigh;

    @JsonProperty("novo_observacoes")
    @JsonAlias({"observacoes", "observacao"})
    private String observations;
    @JsonProperty("novo_fontes")
    @JsonAlias({"fontes", "fonte", "fonte_literatura"})
    private String sources;
}
