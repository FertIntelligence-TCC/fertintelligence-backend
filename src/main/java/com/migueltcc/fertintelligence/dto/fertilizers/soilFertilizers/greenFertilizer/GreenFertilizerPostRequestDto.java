package com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GreenFertilizerPostRequestDto {

    @JsonProperty("novo_nome_adubo")
    private String name;

    @JsonProperty("novo_c")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double c;

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

    @JsonProperty("novo_produtividade_esperada_kg_ha")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double produtividadeEsperadaKgHa;

    @JsonProperty("novo_taxa_mineralizacao_primeiro_ano_percentual")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double taxaMineralizacaoPrimeiroAnoPercentual;

    @JsonProperty("novo_taxa_mineralizacao_segundo_ano_percentual")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double taxaMineralizacaoSegundoAnoPercentual;

    @JsonProperty("novo_taxa_mineralizacao_terceiro_ano_percentual")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double taxaMineralizacaoTerceiroAnoPercentual;

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
