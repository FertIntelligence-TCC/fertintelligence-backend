package com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChelatedFertilizerPostRequestDto {

    @JsonProperty("novo_nome_adubo")
    private String name;

    @JsonProperty("nova_densidade_g_ml")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double densidadeGml;

    @JsonProperty("nova_concentracao_volume_g_l")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double concentracaoVolumeGl;

    @JsonProperty("nova_concentracao_massa_g_kg")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double concentracaoMassaGkg;

    @JsonProperty("novo_n")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double n;

    @JsonProperty("novo_p2o5")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double p2o5;

    @JsonProperty("novo_k2o")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double k2o;

    @JsonProperty("novo_ca")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double ca;

    @JsonProperty("novo_mg")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double mg;

    @JsonProperty("novo_s")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double s;

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

    @JsonProperty("novo_indice_salino")
    private Double indiceSalino;

    @JsonProperty("novo_indice_acidez")
    private Double indiceAcidez;

    @JsonProperty("novo_publico")
    private Boolean novoPublico;

}
