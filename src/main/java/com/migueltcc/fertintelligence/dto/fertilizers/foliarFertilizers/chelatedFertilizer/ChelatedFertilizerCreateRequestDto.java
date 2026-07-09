package com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChelatedFertilizerCreateRequestDto {

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

    @JsonProperty("data_tomada_preco")
    @JsonAlias("dataTomadaPreco")
    private LocalDate dataTomadaPreco;

    @JsonProperty("preco_saco_5kg")
    @JsonAlias({"precoSaco5Kg", "preco_saco_5_kg"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco5Kg;

    @JsonProperty("preco_saco_25kg")
    @JsonAlias({"precoSaco25Kg", "preco_saco_25_kg"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco25Kg;

    @JsonProperty("preco_saco_50kg")
    @JsonAlias({"precoSaco50Kg", "preco_saco_50_kg"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco50Kg;

    @JsonProperty("preco_saco_1000kg")
    @JsonAlias({"precoSaco1000Kg", "preco_saco_1000_kg"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco1000Kg;


    @Size(max = 5, message = "Um adubo pode ter no máximo 5 fotos")
    @JsonProperty("ids_fotos")
    private List<String> idsFotos;

    @JsonProperty("observacao")
    private String observation;

    @JsonProperty("fonte")
    private String source;

    @JsonProperty("publico")
    private Boolean publico;

}
