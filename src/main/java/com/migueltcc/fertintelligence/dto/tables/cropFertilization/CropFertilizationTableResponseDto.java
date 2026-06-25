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

    @Schema(example = "BETWEEN_LINES_IN_METERS")
    @JsonProperty("espacamentos_sugeridos")
    private SpacingType suggested_spacing;

    @Schema(example = "0.45")
    @JsonProperty("valor_inicial")
    private Double initial_value;

    @Schema(example = "0.50")
    @JsonProperty("valor_final")
    private Double final_value;

    @Schema(example = "BETWEEN_LINES_IN_METERS")
    @JsonProperty("espacamento_usado")
    private SpacingType used_spacing;

    @Schema(example = "0.45")
    @JsonProperty("valor_espacamento_usado")
    private Double used_spacing_value;

    @Schema(example = "0.55")
    @JsonProperty("valor_maximo_espacamento_usado")
    private Double used_spacing_maximum_value;

    @Schema(example = "3500.0")
    @JsonProperty("produtividade_regional")
    private Double regional_productivity;

    @Schema(example = "4200.0")
    @JsonProperty("produtividade_esperada")
    private Double expected_productivity;

    @Schema(example = "SATURACAO_POR_BASES_TROCAVEIS") // CORRIGIDO
    @JsonProperty("criterio_de_calagem")
    private CriterioCalagem criteria;

    @JsonProperty("criterio_de_calagem_indicado")
    private String indicatedLimingCriterion;

    @JsonProperty("canViewLinkedAnalyses")
    private Boolean canViewLinkedAnalyses;

    @JsonProperty("id_propriedade")
    private Long propertyId;

    @JsonProperty("nome_propriedade")
    private String propertyName;

    @JsonProperty("id_talhao")
    private Long plotId;

    @JsonProperty("identificacao_talhao")
    private String plotIdentification;

    @JsonProperty("id_extrato_analise_fisica")
    private Long physicalAnalysisId;

    @JsonProperty("identificacao_analise_fisica")
    private String physicalAnalysisIdentification;

    @JsonProperty("id_extrato_analise_fertilidade")
    private Long fertilityAnalysisId;

    @JsonProperty("identificacao_analise_fertilidade")
    private String fertilityAnalysisIdentification;

    @Schema(example = "Aplicar NPK em cobertura.")
    @JsonProperty("observacoes")
    private String observations;

    @Schema(example = "Manual de adubação e calagem regional; boletins técnicos locais")
    @JsonProperty("fontes")
    private String sources;

    @Schema(example = "false")
    @JsonProperty("tabela_publica")
    private boolean public_table;
}
