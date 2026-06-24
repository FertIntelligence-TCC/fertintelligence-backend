package com.migueltcc.fertintelligence.dto.tables.cropFertilization;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class CropFertilizationTablePostRequestDto {

    @JsonProperty("novo_nome_comum_cultura")
    @Schema(example = "MILHO") // OK
    private NomeComum crop_common_name;

    @JsonProperty("novo_nome_cientifico_cultura")
    @Schema(example = "Zea_mays") // CORRIGIDO
    private NomeCientifico crop_scientific_nome;

    @JsonProperty("novo_cultivares")
    @Schema(example = "AG 8025 PRO3, BRS 1055")
    private String cultivares;

    @JsonProperty("novo_espacamentos_sugeridos")
    @JsonAlias({"espacamentos_sugeridos", "novo_tipo_espacamento_sugerido", "tipo_espacamento_sugerido", "novo_tipo_de_espacamento_sugerido", "tipo_de_espacamento_sugerido", "suggested_spacing", "suggestedSpacing"})
    @Schema(example = "PLANTS_PER_LINEAR_METER")
    private SpacingType suggested_spacing;

    @JsonProperty("novo_valor_inicial")
    @Schema(example = "0.50")
    private Double initial_value;

    @JsonProperty("novo_valor_final")
    @Schema(example = "0.60")
    private Double final_value;

    @JsonProperty("novo_espacamento_usado")
    @Schema(example = "BETWEEN_LINES_IN_METERS")
    private SpacingType used_spacing;

    @JsonProperty("novo_valor_espacamento_usado")
    @JsonAlias("novo_valor_minimo_espacamento_usado")
    @Schema(example = "0.55")
    private Double used_spacing_value;

    @JsonProperty("novo_valor_maximo_espacamento_usado")
    @Schema(example = "0.65")
    private Double used_spacing_maximum_value;

    @JsonProperty("novo_produtividade_regional")
    @Schema(example = "8000.0")
    private Double regional_productivity;

    @JsonProperty("novo_produtividade_esperada")
    @Schema(example = "9500.0")
    private Double expected_productivity;

    @JsonProperty("novo_criterio_de_calagem")
    @Schema(example = "NEUTRALIZACAO_POR_ALUMINIO_TROCAVEL") // CORRIGIDO
    private CriterioCalagem criteria;

    @JsonProperty("novo_id_propriedade")
    @JsonAlias({"id_propriedade", "propertyId"})
    private Long propertyId;

    @JsonProperty("novo_id_talhao")
    @JsonAlias({"id_talhao", "plotId"})
    private Long plotId;

    @JsonProperty("novo_id_extrato_analise_fisica")
    @JsonAlias({"id_extrato_analise_fisica", "physicalAnalysisId", "novo_physicalAnalysisId", "id_analise_fisica", "novo_id_analise_fisica", "id_analise_fisica_solo", "novo_id_analise_fisica_solo", "id_extrato_fisico", "novo_id_extrato_fisico", "id_extrato_analise_fisica_solo", "novo_id_extrato_analise_fisica_solo"})
    private Long physicalAnalysisId;

    @JsonProperty("novo_id_extrato_analise_fertilidade")
    @JsonAlias({"id_extrato_analise_fertilidade", "fertilityAnalysisId", "novo_fertilityAnalysisId", "id_analise_fertilidade_solo", "novo_id_analise_fertilidade_solo", "id_analise_fertilidade", "novo_id_analise_fertilidade", "id_extrato_fertilidade", "novo_id_extrato_fertilidade", "id_extrato_analise_fertilidade_solo", "novo_id_extrato_analise_fertilidade_solo"})
    private Long fertilityAnalysisId;

    @JsonProperty("novo_tipo_de_esterco")
    @Schema(example = "GALINHA") // CORRIGIDO
    private TipoEsterco manure;

    @JsonProperty("novo_quantidade_de_esterco")
    @Schema(example = "3.0")
    private Double manure_qtd;

    @JsonProperty("novo_observacoes")
    @Schema(example = "Observação atualizada.")
    private String observations;

    @JsonProperty("novo_fontes")
    @Schema(example = "Manual atualizado; boletim técnico complementar")
    private String sources;

    @JsonProperty("novo_regioes_cultura")
    @Schema(example = "NORDESTE")
    private Regiao region;

    @JsonProperty("tabela_publica")
    private Boolean public_table;
}
