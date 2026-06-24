package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalinityInterpretationResponseDto {

    @Schema(description = "ID do critério de interpretação")
    private Long id;

    @Schema(description = "ID da tabela pai")
    @JsonProperty("table_id")
    private Long tableId;

    @Schema(description = "Unidade dos limites de RAS")
    @JsonProperty("unidade_ras")
    private String rasUnit;

    // --- SOLO NORMAL ---
    @Schema(description = "Maior CE para solo normal (dS/m)")
    @JsonProperty("normal_soil_highest_ce")
    private Double normal_soil_highest_ce;

    @Schema(description = "Maior PST para solo normal (%)")
    @JsonProperty("normal_soil_highest_pst")
    private Double normal_soil_highest_pst;

    @Schema(description = "Maior pH para solo normal")
    @JsonProperty("normal_soil_highest_ph")
    private Double normal_soil_highest_ph;

    @Schema(description = "Maior RAS para solo normal")
    @JsonProperty("normal_soil_highest_ras")
    private Double normal_soil_highest_ras;

    // --- SOLO SALINO ---
    @Schema(description = "Menor CE para solo salino (dS/m)")
    @JsonProperty("saline_soil_lowest_ce")
    private Double saline_soil_lowest_ce;

    @Schema(description = "Maior PST para solo salino (%)")
    @JsonProperty("saline_soil_highest_pst")
    private Double saline_soil_highest_pst;

    @Schema(description = "Maior pH para solo salino")
    @JsonProperty("saline_soil_highest_ph")
    private Double saline_soil_highest_ph;

    @Schema(description = "Maior RAS para solo salino")
    @JsonProperty("saline_soil_highest_ras")
    private Double saline_soil_highest_ras;

    // --- SOLO SALINO-SÓDICO ---
    @Schema(description = "Maior CE para solo salino-sódico (dS/m)")
    @JsonProperty("sodic_saline_soil_highest_ce")
    private Double sodic_saline_soil_highest_ce;

    @Schema(description = "Menor PST para solo salino-sódico (%)")
    @JsonProperty("sodic_saline_soil_lowest_pst")
    private Double sodic_saline_soil_lowest_pst;

    @Schema(description = "Menor pH para solo salino-sódico")
    @JsonProperty("sodic_saline_soil_lowest_ph")
    private Double sodic_saline_soil_lowest_ph;

    @Schema(description = "Menor RAS para solo salino-sódico")
    @JsonProperty("sodic_saline_soil_lowest_ras")
    private Double sodic_saline_soil_lowest_ras;

    // --- SOLO SÓDICO ---
    @Schema(description = "Maior CE para solo sódico (dS/m)")
    @JsonProperty("sodic_soil_highest_ce")
    private Double sodic_soil_highest_ce;

    @Schema(description = "Menor PST para solo sódico (%)")
    @JsonProperty("sodic_soil_lowest_pst")
    private Double sodic_soil_lowest_pst;

    @Schema(description = "Menor pH para solo sódico")
    @JsonProperty("sodic_soil_lowest_ph")
    private Double sodic_soil_lowest_ph;

    @Schema(description = "Menor RAS para solo sódico")
    @JsonProperty("sodic_soil_lowest_ras")
    private Double sodic_soil_lowest_ras;
}
