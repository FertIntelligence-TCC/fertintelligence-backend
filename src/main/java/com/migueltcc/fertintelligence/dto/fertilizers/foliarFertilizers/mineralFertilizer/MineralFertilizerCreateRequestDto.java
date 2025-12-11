package com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MineralFertilizerCreateRequestDto {

    @JsonProperty("nome_adubo")
    @NotBlank(message = "O nome do adubo é obrigatório")
    private String name;

    // Macronutrientes Primários
    @JsonProperty("n")
    @NotNull(message = "A porcentagem de Nitrogênio (N) é obrigatória")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double n;

    @JsonProperty("p2o5")
    @NotNull(message = "A porcentagem de Fósforo (P2O5) é obrigatória")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double p2o5;

    @JsonProperty("k2o")
    @NotNull(message = "A porcentagem de Potássio (K2O) é obrigatória")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double k2o;

    // Macronutrientes Secundários
    @JsonProperty("ca")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double ca;

    @JsonProperty("mg")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double mg;

    @JsonProperty("s")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double s;

    // Micronutrientes
    @JsonProperty("b")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double b;

    @JsonProperty("cu")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double cu;

    @JsonProperty("fe")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double fe;

    @JsonProperty("mn")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double mn;

    @JsonProperty("mo")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double mo;

    @JsonProperty("zn")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double zn;

    // Índices
    @JsonProperty("indice_salino")
    @NotNull(message = "O índice salino é obrigatório")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double indiceSalino;

    @JsonProperty("indice_acidez")
    @NotNull(message = "O índice de acidez é obrigatório")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double indiceAcidez;

}
