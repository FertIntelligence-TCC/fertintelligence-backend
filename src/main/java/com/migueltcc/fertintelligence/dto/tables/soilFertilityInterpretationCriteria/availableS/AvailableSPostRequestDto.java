package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS;

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
public class AvailableSPostRequestDto {

    @JsonProperty("novo_observacoes")
    private String observations;

    @JsonProperty("novo_fontes")
    private String sources;

    // --- SOLO ARENOSO ---
    @JsonProperty("novo_menor_teor_enxofre_solo_arenoso")
    private Double s_content_sandy_too_low;

    @JsonProperty("novo_teor_inicial_baixo_enxofre_solo_arenoso")
    private Double s_content_sandy_low_i;

    @JsonProperty("novo_teor_final_baixo_enxofre_solo_arenoso")
    private Double s_content_sandy_low_f;

    @JsonProperty("novo_teor_inicial_medio_enxofre_solo_arenoso")
    private Double s_content_sandy_medium_i;

    @JsonProperty("novo_teor_final_medio_enxofre_solo_arenoso")
    private Double s_content_sandy_medium_f;

    @JsonProperty("novo_teor_inicial_alto_enxofre_solo_arenoso")
    private Double s_content_sandy_hight_i;

    @JsonProperty("novo_teor_final_alto_enxofre_solo_arenoso")
    private Double s_content_sandy_hight_f;

    @JsonProperty("novo_maior_teor_enxofre_solo_arenoso")
    private Double s_content_sandy_too_hight;

    // --- SOLO ARENOSO/ARGILOSO ---
    @JsonProperty("novo_menor_teor_enxofre_solo_arenoso_argiloso")
    private Double s_content_sandy_clayey_too_low;

    @JsonProperty("novo_teor_inicial_baixo_enxofre_solo_arenoso_argiloso")
    private Double s_content_sandy_clayey_low_i;

    @JsonProperty("novo_teor_final_baixo_enxofre_solo_arenoso_argiloso")
    private Double s_content_sandy_clayey_low_f;

    @JsonProperty("novo_teor_inicial_medio_enxofre_solo_arenoso_argiloso")
    private Double s_content_sandy_clayey_medium_i;

    @JsonProperty("novo_teor_final_medio_enxofre_solo_arenoso_argiloso")
    private Double s_content_sandy_clayey_medium_f;

    @JsonProperty("novo_teor_inicial_alto_enxofre_solo_arenoso_argiloso")
    private Double s_content_sandy_clayey_hight_i;

    @JsonProperty("novo_teor_final_alto_enxofre_solo_arenoso_argiloso")
    private Double s_content_sandy_clayey_hight_f;

    @JsonProperty("novo_maior_teor_enxofre_solo_arenoso_argiloso")
    private Double s_content_sandy_clayey_too_hight;

    // --- SOLO ARGILOSO ---
    @JsonProperty("novo_menor_teor_enxofre_solo_argiloso")
    private Double s_content_clayey_too_low;

    @JsonProperty("novo_teor_inicial_baixo_enxofre_solo_argiloso")
    private Double s_content_clayey_low_i;

    @JsonProperty("novo_teor_final_baixo_enxofre_solo_argiloso")
    private Double s_content_clayey_low_f;

    @JsonProperty("novo_teor_inicial_medio_enxofre_solo_argiloso")
    private Double s_content_clayey_medium_i;

    @JsonProperty("novo_teor_final_medio_enxofre_solo_argiloso")
    private Double s_content_clayey_medium_f;

    @JsonProperty("novo_teor_inicial_alto_enxofre_solo_argiloso")
    private Double s_content_clayey_hight_i;

    @JsonProperty("novo_teor_final_alto_enxofre_solo_argiloso")
    private Double s_content_clayey_hight_f;

    @JsonProperty("novo_maior_teor_enxofre_solo_argiloso")
    private Double s_content_clayey_too_hight;

    // --- SOLO MUITO ARGILOSO ---
    @JsonProperty("novo_menor_teor_enxofre_solo_muito_argiloso")
    private Double s_content_very_clayey_too_low;

    @JsonProperty("novo_teor_inicial_baixo_enxofre_solo_muito_argiloso")
    private Double s_content_very_clayey_low_i;

    @JsonProperty("novo_teor_final_baixo_enxofre_solo_muito_argiloso")
    private Double s_content_very_clayey_low_f;

    @JsonProperty("novo_teor_inicial_medio_enxofre_solo_muito_argiloso")
    private Double s_content_very_clayey_medium_i;

    @JsonProperty("novo_teor_final_medio_enxofre_solo_muito_argiloso")
    private Double s_content_very_clayey_medium_f;

    @JsonProperty("novo_teor_inicial_alto_enxofre_solo_muito_argiloso")
    private Double s_content_very_clayey_hight_i;

    @JsonProperty("novo_teor_final_alto_enxofre_solo_muito_argiloso")
    private Double s_content_very_clayey_hight_f;

    @JsonProperty("novo_maior_teor_enxofre_solo_muito_argiloso")
    private Double s_content_very_clayey_too_hight;
}
