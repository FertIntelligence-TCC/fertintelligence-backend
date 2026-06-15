package com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer;

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
public class OrganicFertilizerResponseDto {

    @Schema(example = "1")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "Composto Orgânico")
    @JsonProperty("nome_adubo")
    private String name;

    @Schema(example = "8.0", description = "Porcentagem de Carbono Orgânico")
    @JsonProperty("c")
    private Double c;

    @Schema(example = "10.0")
    @JsonProperty("n")
    private Double n;

    @Schema(example = "10.0")
    @JsonProperty("p2o5")
    private Double p2o5;

    @Schema(example = "10.0")
    @JsonProperty("k2o")
    private Double k2o;

    @JsonProperty("ca")
    private Double ca;

    @JsonProperty("mg")
    private Double mg;

    @JsonProperty("s")
    private Double s;

    @JsonProperty("b")
    private Double b;

    @JsonProperty("cu")
    private Double cu;

    @JsonProperty("fe")
    private Double fe;

    @JsonProperty("mn")
    private Double mn;

    @JsonProperty("mo")
    private Double mo;

    @JsonProperty("zn")
    private Double zn;

    @Schema(example = "12.0", description = "Teor de umidade em porcentagem")
    @JsonProperty("teor_umidade")
    private Double teorUmidade;

    @Schema(example = "18.0", description = "Teor de cinzas em porcentagem")
    @JsonProperty("teor_cinzas")
    private Double teorCinzas;

    @Schema(example = "42")
    @JsonProperty("user_id")
    private Long userId;

    @Schema(example = "João Agrônomo")
    @JsonProperty("user_nome")
    private String userNome;

    @Schema(example = "true")
    @JsonProperty("publico")
    private Boolean publico;

    @Schema(example = "João Agrônomo")
    @JsonProperty("nome_criador")
    private String nomeCriador;
}
