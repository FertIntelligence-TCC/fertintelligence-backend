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
public class SalinityInterpretationPostRequestDto {

    @JsonProperty("novo_observacoes")
    private String observations;

    @JsonProperty("novo_fontes")
    private String sources;

    // --- SOLO NORMAL ---
    @JsonProperty("novo_maior_ce_solo_normal")
    private Double normal_soil_highest_ce;

    @JsonProperty("novo_maior_pst_solo_normal")
    private Double normal_soil_highest_pst;

    @JsonProperty("novo_maior_ph_solo_normal")
    private Double normal_soil_highest_ph;

    @JsonProperty("novo_maior_ras_solo_normal")
    private Double normal_soil_highest_ras;

    // --- SOLO SALINO ---
    @JsonProperty("novo_menor_ce_solo_salino")
    private Double saline_soil_lowest_ce;

    @JsonProperty("novo_maior_pst_solo_salino")
    private Double saline_soil_highest_pst;

    @JsonProperty("novo_maior_ph_solo_salino")
    private Double saline_soil_highest_ph;

    @JsonProperty("novo_maior_ras_solo_salino")
    private Double saline_soil_highest_ras;

    // --- SOLO SALINO-SÓDICO ---
    @JsonProperty("novo_maior_ce_solo_salino_sodico")
    private Double sodic_saline_soil_highest_ce;

    @JsonProperty("novo_menor_pst_solo_salino_sodico")
    private Double sodic_saline_soil_lowest_pst;

    @JsonProperty("novo_menor_ph_solo_salino_sodico")
    private Double sodic_saline_soil_lowest_ph;

    @JsonProperty("novo_menor_ras_solo_salino_sodico")
    private Double sodic_saline_soil_lowest_ras;

    // --- SOLO SÓDICO ---
    @JsonProperty("novo_maior_ce_solo_sodico")
    private Double sodic_soil_highest_ce;

    @JsonProperty("novo_menor_pst_solo_sodico")
    private Double sodic_soil_lowest_pst;

    @JsonProperty("novo_menor_ph_solo_sodico")
    private Double sodic_soil_lowest_ph;

    @JsonProperty("novo_menor_ras_solo_sodico")
    private Double sodic_soil_lowest_ras;
}
