package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalinityInterpretationCreateRequestDto {

    // --- SOLO NORMAL ---
    @JsonProperty("maior_ce_solo_normal")
    private Double normal_soil_highest_ce;

    @JsonProperty("maior_pst_solo_normal")
    private Double normal_soil_highest_pst;

    @JsonProperty("maior_ph_solo_normal")
    private Double normal_soil_highest_ph;

    @JsonProperty("maior_ras_solo_normal")
    private Double normal_soil_highest_ras;

    // --- SOLO SALINO ---
    @JsonProperty("menor_ce_solo_salino")
    private Double saline_soil_lowest_ce;

    @JsonProperty("maior_pst_solo_salino")
    private Double saline_soil_highest_pst;

    @JsonProperty("maior_ph_solo_salino")
    private Double saline_soil_highest_ph;

    @JsonProperty("maior_ras_solo_salino")
    private Double saline_soil_highest_ras;

    // --- SOLO SALINO-SÓDICO ---
    @JsonProperty("maior_ce_solo_salino_sodico")
    private Double sodic_saline_soil_highest_ce;

    @JsonProperty("menor_pst_solo_salino_sodico")
    private Double sodic_saline_soil_lowest_pst;

    @JsonProperty("menor_ph_solo_salino_sodico")
    private Double sodic_saline_soil_lowest_ph;

    @JsonProperty("menor_ras_solo_salino_sodico")
    private Double sodic_saline_soil_lowest_ras;

    // --- SOLO SÓDICO ---
    @JsonProperty("maior_ce_solo_sodico")
    private Double sodic_soil_highest_ce;

    @JsonProperty("menor_pst_solo_sodico")
    private Double sodic_soil_lowest_pst;

    @JsonProperty("menor_ph_solo_sodico")
    private Double sodic_soil_lowest_ph;

    @JsonProperty("menor_ras_solo_sodico")
    private Double sodic_soil_lowest_ras;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    private String sources;
}
