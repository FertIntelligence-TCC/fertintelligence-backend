package com.migueltcc.fertintelligence.dto.tables.cropFertilization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CropFertilizationTableCreateRequestDto {

    @JsonProperty("nome_comum_cultura")
    @NotNull
    private NomeComum crop_common_name;

    @JsonProperty("nome_cientifico_cultura")
    @NotNull
    private NomeCientifico crop_scientific_nome;

    @JsonProperty("cultivares")
    @NotNull
    private String cultivares;

    @JsonProperty("espacamentos_sugeridos")
    @NotNull
    private SpacingType suggested_spacing;

    @JsonProperty("valor_inicial")
    @NotNull
    private Double initial_value;

    @JsonProperty("valor_final")
    @NotNull
    private Double final_value;

    @JsonProperty("espacamento_usado")
    @NotNull
    private Double used_spacing;

    @JsonProperty("produtividade_regional")
    @NotNull
    private Double regional_productivity;

    @JsonProperty("produtividade_esperada")
    @NotNull
    private Double expected_productivity;

    @JsonProperty("criterio_de_calagem")
    @NotNull
    private CriterioCalagem criteria;

    @JsonProperty("tipo_de_esterco")
    @NotNull
    private TipoEsterco manure;

    @JsonProperty("quantidade_de_esterco")
    @NotNull
    private Double manure_qtd;

    @JsonProperty("sugestao_gessagem")
    @NotNull
    private Double gessing;

    @JsonProperty("sugestao_micronutrientes")
    @NotNull
    private Double micronutrients;

    @JsonProperty("sugestao_npk")
    @NotNull
    private Double npk;

    @JsonProperty("observacoes")
    @NotNull
    private String observations;
}