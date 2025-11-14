package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor;

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
public class AvailablePMehlich1ExtractorPostRequestDto {

    @Schema(description = "ID da tabela pai", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("novo_id_tabela")
    private Long tableId;

    // --- SOLO ARENOSO ---
    @JsonProperty("novo_menor_teor_fosforo_solo_arenoso")
    private Double p_content_sandy_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_arenoso")
    private Double p_content_sandy_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_arenoso")
    private Double p_content_sandy_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_arenoso")
    private Double p_content_sandy_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_arenoso")
    private Double p_content_sandy_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_arenoso")
    private Double p_content_sandy_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_arenoso")
    private Double p_content_sandy_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_arenoso")
    private Double p_content_sandy_too_hight;

    // --- SOLO ARENOSO/ARGILOSO (TEXTURA MÉDIA) ---
    @JsonProperty("novo_menor_teor_fosforo_solo_arenoso_argiloso")
    private Double p_content_sandy_clayey_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_arenoso_argiloso")
    private Double p_content_sandy_clayey_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_arenoso_argiloso")
    private Double p_content_sandy_clayey_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_arenoso_argiloso")
    private Double p_content_sandy_clayey_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_arenoso_argiloso")
    private Double p_content_sandy_clayey_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_arenoso_argiloso")
    private Double p_content_sandy_clayey_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_arenoso_argiloso")
    private Double p_content_sandy_clayey_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_arenoso_argiloso")
    private Double p_content_sandy_clayey_too_hight;

    // --- SOLO ARGILOSO ---
    @JsonProperty("novo_menor_teor_fosforo_solo_argiloso")
    private Double p_content_clayey_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_argiloso")
    private Double p_content_clayey_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_argiloso")
    private Double p_content_clayey_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_argiloso")
    private Double p_content_clayey_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_argiloso")
    private Double p_content_clayey_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_argiloso")
    private Double p_content_clayey_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_argiloso")
    private Double p_content_clayey_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_argiloso")
    private Double p_content_clayey_too_hight;

    // --- SOLO MUITO ARGILOSO ---
    @JsonProperty("novo_menor_teor_fosforo_solo_muito_argiloso")
    private Double p_content_very_clayey_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_muito_argiloso")
    private Double p_content_very_clayey_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_muito_argiloso")
    private Double p_content_very_clayey_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_muito_argiloso")
    private Double p_content_very_clayey_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_muito_argiloso")
    private Double p_content_very_clayey_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_muito_argiloso")
    private Double p_content_very_clayey_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_muito_argiloso")
    private Double p_content_very_clayey_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_muito_argiloso")
    private Double p_content_very_clayey_too_hight;
}