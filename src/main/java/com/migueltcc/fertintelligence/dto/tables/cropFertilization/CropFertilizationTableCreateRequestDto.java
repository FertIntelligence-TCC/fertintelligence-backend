package com.migueltcc.fertintelligence.dto.tables.cropFertilization;

import com.fasterxml.jackson.annotation.JsonAlias;
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

    @JsonProperty("regioes_cultura")
    @Schema(example = "SUL")
    @NotNull
    private Regiao region;

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
    private SpacingType used_spacing;

    @JsonProperty("valor_espacamento_usado")
    @JsonAlias("valor_minimo_espacamento_usado")
    @NotNull
    private Double used_spacing_value;

    @JsonProperty("valor_maximo_espacamento_usado")
    private Double used_spacing_maximum_value;

    @JsonProperty("produtividade_regional")
    @NotNull
    private Double regional_productivity;

    @JsonProperty("produtividade_esperada")
    @NotNull
    private Double expected_productivity;

    @JsonProperty("criterio_de_calagem")
    private CriterioCalagem criteria;

    @JsonProperty("id_propriedade")
    @JsonAlias("propertyId")
    private Long propertyId;

    @JsonProperty("id_talhao")
    @JsonAlias("plotId")
    private Long plotId;

    @JsonProperty("id_extrato_analise_fisica")
    @JsonAlias({"physicalAnalysisId", "id_analise_fisica"})
    private Long physicalAnalysisId;

    @JsonProperty("id_extrato_analise_fertilidade")
    @JsonAlias({"fertilityAnalysisId", "id_analise_fertilidade"})
    private Long fertilityAnalysisId;

    @JsonProperty("tipo_de_esterco")
    @NotNull
    private TipoEsterco manure;

    @JsonProperty("quantidade_de_esterco")
    @NotNull
    private Double manure_qtd;

    @JsonProperty("sugestao_gessagem")
    @NotNull
    private Double gessing;

    @JsonProperty("dose_minima_b")
    private Double bMinimumDose;

    @JsonProperty("dose_maxima_b")
    private Double bMaximumDose;

    @JsonProperty("dose_minima_cu")
    private Double cuMinimumDose;

    @JsonProperty("dose_maxima_cu")
    private Double cuMaximumDose;

    @JsonProperty("dose_minima_fe")
    private Double feMinimumDose;

    @JsonProperty("dose_maxima_fe")
    private Double feMaximumDose;

    @JsonProperty("dose_minima_ni")
    private Double niMinimumDose;

    @JsonProperty("dose_maxima_ni")
    private Double niMaximumDose;

    @JsonProperty("dose_minima_mn")
    private Double mnMinimumDose;

    @JsonProperty("dose_maxima_mn")
    private Double mnMaximumDose;

    @JsonProperty("dose_minima_mo")
    private Double moMinimumDose;

    @JsonProperty("dose_maxima_mo")
    private Double moMaximumDose;

    @JsonProperty("dose_minima_zn")
    private Double znMinimumDose;

    @JsonProperty("dose_maxima_zn")
    private Double znMaximumDose;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    @Schema(example = "Manual de adubação e calagem regional; boletins técnicos locais")
    private String sources;

    @JsonProperty("tabela_publica")
    private Boolean public_table;
}