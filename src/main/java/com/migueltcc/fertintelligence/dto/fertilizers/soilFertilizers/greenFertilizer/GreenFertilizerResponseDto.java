package com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class GreenFertilizerResponseDto {

    @Schema(example = "1")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "Organo Plus 10-10-10")
    @JsonProperty("nome_adubo")
    private String name;

    @Schema(description = "Umidade na incorporação em %")
    @JsonProperty("umidade_incorporacao_percentual")
    private Double umidadeIncorporacaoPercentual;

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

    @Schema(example = "42")
    @JsonProperty("user_id")
    private Long userId;

    @Schema(example = "João Agrônomo")
    @JsonProperty("user_nome")
    private String userNome;


    @Schema(example = "true")
    @JsonProperty("publico")
    private Boolean publico;

    @JsonProperty("data_tomada_preco")
    private LocalDate dataTomadaPreco;

    @JsonProperty("preco_saco_5kg")
    private BigDecimal precoSaco5Kg;

    @JsonProperty("preco_saco_25kg")
    private BigDecimal precoSaco25Kg;

    @JsonProperty("preco_saco_50kg")
    private BigDecimal precoSaco50Kg;

    @JsonProperty("preco_saco_1000kg")
    private BigDecimal precoSaco1000Kg;


    @JsonProperty("ids_fotos")
    private List<String> idsFotos;

    @JsonProperty("observacao")
    private String observation;

    @JsonProperty("fonte")
    private String source;

    @Schema(example = "12000.0", description = "Produtividade esperada em kg/ha")
    @JsonProperty("produtividade_esperada_kg_ha")
    private Double produtividadeEsperadaKgHa;

    @Schema(example = "50.0", description = "Taxa de mineralização no primeiro ano em %")
    @JsonProperty("taxa_mineralizacao_primeiro_ano_percentual")
    private Double taxaMineralizacaoPrimeiroAnoPercentual;

    @Schema(example = "30.0", description = "Taxa de mineralização no segundo ano em %")
    @JsonProperty("taxa_mineralizacao_segundo_ano_percentual")
    private Double taxaMineralizacaoSegundoAnoPercentual;

    @Schema(example = "20.0", description = "Taxa de mineralização no terceiro ano em %")
    @JsonProperty("taxa_mineralizacao_terceiro_ano_percentual")
    private Double taxaMineralizacaoTerceiroAnoPercentual;

    @Schema(description = "Taxa de mineralização no quarto ano em %")
    @JsonProperty("taxa_mineralizacao_quarto_ano_percentual")
    private Double taxaMineralizacaoQuartoAnoPercentual;

    @JsonProperty("nome_criador")
    private String nomeCriador;
}
