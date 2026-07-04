package com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class GreenFertilizerCreateRequestDto {

    @JsonProperty("nome_adubo")
    @NotBlank(message = "O nome do adubo é obrigatório")
    private String name;

    // Componente Orgânico
    @JsonProperty("c")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double c;

    // Macronutrientes Primários
    @JsonProperty("n")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double n;

    @JsonProperty("p2o5")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double p2o5;

    @JsonProperty("k2o")
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

    @JsonProperty("produtividade_esperada_kg_ha")
    @JsonAlias({"produtividade_esperada", "produtividadeEsperadaKgHa"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double produtividadeEsperadaKgHa;

    @JsonProperty("taxa_mineralizacao_primeiro_ano_percentual")
    @JsonAlias({"taxa_mineralizacao_primeiro_ano", "taxaMineralizacaoPrimeiroAnoPercentual"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double taxaMineralizacaoPrimeiroAnoPercentual;

    @JsonProperty("taxa_mineralizacao_segundo_ano_percentual")
    @JsonAlias({"taxa_mineralizacao_segundo_ano", "taxaMineralizacaoSegundoAnoPercentual"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double taxaMineralizacaoSegundoAnoPercentual;

    @JsonProperty("taxa_mineralizacao_terceiro_ano_percentual")
    @JsonAlias({"taxa_mineralizacao_terceiro_ano", "taxaMineralizacaoTerceiroAnoPercentual"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double taxaMineralizacaoTerceiroAnoPercentual;

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
    @JsonProperty("ids_fotos")
    private List<String> idsFotos;

    @JsonProperty("observacao")
    private String observation;

    @JsonProperty("fonte")
    private String source;

    @JsonProperty("publico")
    private Boolean publico;

}
