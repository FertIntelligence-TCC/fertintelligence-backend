package com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer;

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
public class SimpleMineralFertilizerResponseDto {

    @Schema(example = "Fosfato Diamônio")
    @JsonProperty("nome_adubo")
    private String name;

    @Schema(example = "1")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "45.0", description = "Porcentagem de Nitrogênio")
    @JsonProperty("n")
    private Double n;

    @Schema(example = "0.0", description = "Porcentagem de Fósforo")
    @JsonProperty("p2o5")
    private Double p2o5;

    @Schema(example = "0.0", description = "Porcentagem de Potássio")
    @JsonProperty("k2o")
    private Double k2o;

    @Schema(example = "0.0")
    @JsonProperty("ca")
    private Double ca;

    @Schema(example = "0.0")
    @JsonProperty("mg")
    private Double mg;

    @Schema(example = "0.0")
    @JsonProperty("s")
    private Double s;

    @Schema(example = "0.0")
    @JsonProperty("b")
    private Double b;

    @Schema(example = "0.0")
    @JsonProperty("cu")
    private Double cu;

    @Schema(example = "0.0")
    @JsonProperty("fe")
    private Double fe;

    @Schema(example = "0.0")
    @JsonProperty("mn")
    private Double mn;

    @Schema(example = "0.0")
    @JsonProperty("mo")
    private Double mo;

    @Schema(example = "0.0")
    @JsonProperty("zn")
    private Double zn;

    @Schema(example = "75.0", description = "Índice Salino")
    @JsonProperty("indice_salino")
    private Double indiceSalino;

    @Schema(example = "60.0", description = "Índice de Acidez")
    @JsonProperty("indice_acidez")
    private Double indiceAcidez;

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
