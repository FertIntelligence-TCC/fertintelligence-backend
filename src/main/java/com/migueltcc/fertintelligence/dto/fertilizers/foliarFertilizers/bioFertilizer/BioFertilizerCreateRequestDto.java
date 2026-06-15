package com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer;

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
public class BioFertilizerCreateRequestDto {

    @JsonProperty("nome_adubo")
    @NotBlank(message = "O nome do adubo é obrigatório")
    private String name;

    @JsonProperty("densidade_g_ml")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double densidadeGml;

    @JsonProperty("concentracao_volume_g_l")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double concentracaoVolumeGl;

    @JsonProperty("concentracao_massa_g_kg")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double concentracaoMassaGkg;

    @JsonProperty("proteinas_g_l")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double proteinasGl;

    @JsonProperty("aminoacidos_g_l")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double aminoacidosGl;

    @JsonProperty("amidos_g_l")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double amidosGl;

    @JsonProperty("acucares_g_l")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double acucaresGl;

    @JsonProperty("compostos_diversos_g_l")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double compostosDiversosGl;

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
    private Double indiceSalino;

    @JsonProperty("indice_acidez")
    private Double indiceAcidez;


    @JsonProperty("publico")
    private Boolean publico;
}
