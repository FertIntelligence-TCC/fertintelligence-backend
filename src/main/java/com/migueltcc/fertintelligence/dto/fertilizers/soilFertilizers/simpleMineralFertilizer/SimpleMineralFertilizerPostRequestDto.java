package com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
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
public class SimpleMineralFertilizerPostRequestDto {

    @JsonProperty("novo_nome_adubo")
    private String name;

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

    @JsonProperty("data_tomada_preco")
    @JsonAlias("dataTomadaPreco")
    private LocalDate dataTomadaPreco;

    @JsonProperty("preco_saco_5kg")
    @JsonAlias("precoSaco5Kg")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco5Kg;

    @JsonProperty("preco_saco_25kg")
    @JsonAlias("precoSaco25Kg")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco25Kg;

    @JsonProperty("preco_saco_50kg")
    @JsonAlias("precoSaco50Kg")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco50Kg;

    @JsonProperty("preco_saco_1000kg")
    @JsonAlias("precoSaco1000Kg")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco1000Kg;


    @Size(max = 5, message = "Um adubo pode ter no máximo 5 fotos")
    @JsonProperty("novos_ids_fotos")
    private List<String> idsFotos;

    @JsonProperty("novo_observacao")
    private String observation;

    @JsonProperty("novo_fonte")
    private String source;

    @JsonProperty("novo_publico")
    private Boolean novoPublico;
}
