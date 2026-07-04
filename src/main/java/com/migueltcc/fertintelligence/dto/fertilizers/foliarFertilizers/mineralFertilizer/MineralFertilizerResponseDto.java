package com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NaturezaFisica;
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
public class MineralFertilizerResponseDto {

    @Schema(example = "1")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "Organo Plus 10-10-10")
    @JsonProperty("nome_adubo")
    private String name;

    @Schema(example = "LÍQUIDO")
    @JsonProperty("natureza_fisica")
    private NaturezaFisica naturezaFisica;

    @Schema(example = "1.2")
    @JsonProperty("densidade_g_ml")
    private Double densidadeGml;

    @Schema(example = "240.0")
    @JsonProperty("concentracao_volume_g_l")
    private Double concentracaoVolumeGl;

    @Schema(example = "200.0")
    @JsonProperty("concentracao_massa_g_kg")
    private Double concentracaoMassaGkg;

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

    @Schema(example = "45.0")
    @JsonProperty("indice_salino")
    private Double indiceSalino;

    @Schema(example = "5.0")
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

    @JsonProperty("nome_criador")
    private String nomeCriador;
}
