package com.migueltcc.fertintelligence.dto.tables.cropFertilization;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonAlias({"tipo_espacamento_sugerido", "tipo_de_espacamento_sugerido", "suggested_spacing", "suggestedSpacing"})
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
    @JsonAlias({"expected_productivity", "expectedProductivity"})
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
    @JsonAlias({"physicalAnalysisId", "id_analise_fisica", "id_analise_fisica_solo", "id_extrato_fisico", "id_extrato_analise_fisica_solo"})
    private Long physicalAnalysisId;

    @JsonProperty("id_extrato_analise_fertilidade")
    @JsonAlias({"fertilityAnalysisId", "id_analise_fertilidade_solo", "id_analise_fertilidade", "id_extrato_fertilidade", "id_extrato_analise_fertilidade_solo"})
    private Long fertilityAnalysisId;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    @Schema(example = "Manual de adubação e calagem regional; boletins técnicos locais")
    private String sources;

    @JsonProperty("tabela_publica")
    @JsonAlias({"public_table", "publicTable"})
    private Boolean public_table;
}
