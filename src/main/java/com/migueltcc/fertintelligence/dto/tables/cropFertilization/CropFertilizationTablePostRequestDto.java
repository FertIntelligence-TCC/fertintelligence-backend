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

    @JsonProperty("novo_tipo_de_esterco")
    @Schema(example = "GALINHA") // CORRIGIDO
    private TipoEsterco manure;

    @JsonProperty("novo_quantidade_de_esterco")
    @Schema(example = "3.0")
    private Double manure_qtd;

    @JsonProperty("novo_sugestao_gessagem")
    @Schema(example = "1.0")
    private Double gessing;

    @JsonProperty("novo_dose_minima_b")
    private Double bMinimumDose;

    @JsonProperty("novo_dose_maxima_b")
    private Double bMaximumDose;

    @JsonProperty("novo_dose_minima_cu")
    private Double cuMinimumDose;

    @JsonProperty("novo_dose_maxima_cu")
    private Double cuMaximumDose;

    @JsonProperty("novo_dose_minima_fe")
    private Double feMinimumDose;

    @JsonProperty("novo_dose_maxima_fe")
    private Double feMaximumDose;

    @JsonProperty("novo_dose_minima_ni")
    private Double niMinimumDose;

    @JsonProperty("novo_dose_maxima_ni")
    private Double niMaximumDose;

    @JsonProperty("novo_dose_minima_mn")
    private Double mnMinimumDose;

    @JsonProperty("novo_dose_maxima_mn")
    private Double mnMaximumDose;

    @JsonProperty("novo_dose_minima_mo")
    private Double moMinimumDose;

    @JsonProperty("novo_dose_maxima_mo")
    private Double moMaximumDose;

    @JsonProperty("novo_dose_minima_zn")
    private Double znMinimumDose;

    @JsonProperty("novo_dose_maxima_zn")
    private Double znMaximumDose;

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