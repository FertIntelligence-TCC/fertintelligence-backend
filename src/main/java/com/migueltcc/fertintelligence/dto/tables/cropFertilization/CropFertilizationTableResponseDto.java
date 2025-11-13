package com.migueltcc.fertintelligence.dto.tables.cropFertilization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropFertilizationTableResponseDto {

    @Schema(example = "10")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "5")
    @JsonProperty("id_criador")
    private Long creator_id;

    @Schema(example = "João Silva")
    @JsonProperty("nome_criador")
    private String creator_name;

    @Schema(example = "SUL")
    @JsonProperty("regioes_cultura")
    private Regiao region;

    @Schema(example = "SOJA")
    @JsonProperty("nome_comum_cultura")
    private NomeComum crop_common_name;

    @Schema(example = "Glycine_max")
    @JsonProperty("nome_cientifico_cultura")
    private NomeCientifico crop_scientific_nome;

    @Schema(example = "TMG 7062 IPRO, BRS 583")
    @JsonProperty("cultivares")
    private String cultivares;

    @Schema(example = "BETWEEN_LINES_IN_METERS") // CORRIGIDO
    @JsonProperty("espacamentos_sugeridos")
    private SpacingType suggested_spacing;

    @Schema(example = "0.45")
    @JsonProperty("valor_inicial")
    private Double initial_value;

    @Schema(example = "0.50")
    @JsonProperty("valor_final")
    private Double final_value;

    @Schema(example = "0.45")
    @JsonProperty("espacamento_usado")
    private Double used_spacing;

    @Schema(example = "3500.0")
    @JsonProperty("produtividade_regional")
    private Double regional_productivity;

    @Schema(example = "4200.0")
    @JsonProperty("produtividade_esperada")
    private Double expected_productivity;

    @Schema(example = "SATURACAO_POR_BASES_TROCAVEIS") // CORRIGIDO
    @JsonProperty("criterio_de_calagem")
    private CriterioCalagem criteria;

    @Schema(example = "BOVINO") // OK
    @JsonProperty("tipo_de_esterco")
    private TipoEsterco manure;

    @Schema(example = "5.0")
    @JsonProperty("quantidade_de_esterco")
    private Double manure_qtd;

    @Schema(example = "1.5")
    @JsonProperty("sugestao_gessagem")
    private Double gessing;

    @Schema(example = "200.0")
    @JsonProperty("sugestao_micronutrientes")
    private Double micronutrients;

    @Schema(example = "120.0")
    @JsonProperty("sugestao_npk")
    private Double npk;

    @Schema(example = "Aplicar NPK em cobertura.")
    @JsonProperty("observacoes")
    private String observations;
}