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

    @JsonProperty("id_dose_b")
    private Long bDoseId;
    @JsonProperty("dose_minima_b")
    private Double bMinimumDose;
    @JsonProperty("dose_maxima_b")
    private Double bMaximumDose;

    @JsonProperty("id_dose_cu")
    private Long cuDoseId;
    @JsonProperty("dose_minima_cu")
    private Double cuMinimumDose;
    @JsonProperty("dose_maxima_cu")
    private Double cuMaximumDose;

    @JsonProperty("id_dose_fe")
    private Long feDoseId;
    @JsonProperty("dose_minima_fe")
    private Double feMinimumDose;
    @JsonProperty("dose_maxima_fe")
    private Double feMaximumDose;

    @JsonProperty("id_dose_ni")
    private Long niDoseId;
    @JsonProperty("dose_minima_ni")
    private Double niMinimumDose;
    @JsonProperty("dose_maxima_ni")
    private Double niMaximumDose;

    @JsonProperty("id_dose_mn")
    private Long mnDoseId;
    @JsonProperty("dose_minima_mn")
    private Double mnMinimumDose;
    @JsonProperty("dose_maxima_mn")
    private Double mnMaximumDose;

    @JsonProperty("id_dose_mo")
    private Long moDoseId;
    @JsonProperty("dose_minima_mo")
    private Double moMinimumDose;
    @JsonProperty("dose_maxima_mo")
    private Double moMaximumDose;

    @JsonProperty("id_dose_zn")
    private Long znDoseId;
    @JsonProperty("dose_minima_zn")
    private Double znMinimumDose;
    @JsonProperty("dose_maxima_zn")
    private Double znMaximumDose;

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