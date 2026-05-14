package com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.FormulateDto;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.NPKrelationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormulatedMineralFertilizerResponseDto {

    @Schema(example = "1")
    @JsonProperty("id")
    private Long id;

    @JsonProperty("formula")
    private FormulateDto formulate;

    @JsonProperty("relacao")
    private NPKrelationDto relation;

    @Schema(example = "1")
    @JsonProperty("numero_formula_indicada")
    private Integer indicatedFormulaNumber;

    @Schema(example = "4.0")
    @JsonProperty("n")
    private Double n;

    @Schema(example = "14.0")
    @JsonProperty("p2o5")
    private Double p2o5;

    @Schema(example = "8.0")
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
