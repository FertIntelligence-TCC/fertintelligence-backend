package com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.FormulateDto;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.NPKrelationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormulatedMineralFertilizerPostRequestDto {

    @JsonProperty("nova_formula")
    @JsonAlias("novo_formulate")
    @Valid
    private FormulateDto formulate;

    @JsonProperty("nova_relacao")
    @JsonAlias("novo_relation")
    @Valid
    private NPKrelationDto relation;

    @JsonProperty("novo_numero_formula_indicada")
    private Integer indicatedFormulaNumber;

    // Garantias
    @JsonProperty("novo_n")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double n;

    @JsonProperty("novo_p2o5")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double p2o5;

    @JsonProperty("novo_k2o")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double k2o;

    // Secundários
    @JsonProperty("novo_ca")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double ca;

    @JsonProperty("novo_mg")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double mg;

    @JsonProperty("novo_s")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double s;

    // Micronutrientes
    @JsonProperty("novo_b")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double b;

    @JsonProperty("novo_cu")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double cu;

    @JsonProperty("novo_fe")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double fe;

    @JsonProperty("novo_mn")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double mn;

    @JsonProperty("novo_mo")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double mo;

    @JsonProperty("novo_zn")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double zn;

    @JsonProperty("novo_publico")
    private Boolean novoPublico;
}
