package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel;

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
public class KExchangeableContentPostRequestDto {

    @Schema(description = "ID da tabela pai", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("novo_id_tabela")
    private Long tableId;

    // --- CTC < 20 mmolc/dm³ ---
    @JsonProperty("novo_menor_teor_k_ctc_menor_20")
    private Double k_content_cec_less_20_too_low;

    @JsonProperty("novo_teor_inicial_baixo_k_ctc_menor_20")
    private Double k_content_cec_less_20_low_i;

    @JsonProperty("novo_teor_final_baixo_k_ctc_menor_20")
    private Double k_content_cec_less_20_low_f;

    @JsonProperty("novo_teor_inicial_medio_k_ctc_menor_20")
    private Double k_content_cec_less_20_medium_i;

    @JsonProperty("novo_teor_final_medio_k_ctc_menor_20")
    private Double k_content_cec_less_20_medium_f;

    @JsonProperty("novo_teor_inicial_alto_k_ctc_menor_20")
    private Double k_content_cec_less_20_hight_i;

    @JsonProperty("novo_teor_final_alto_k_ctc_menor_20")
    private Double k_content_cec_less_20_hight_f;

    @JsonProperty("novo_maior_teor_k_ctc_menor_20")
    private Double k_content_cec_less_20_too_hight;

    // --- CTC 20 a 40 mmolc/dm³ ---
    @JsonProperty("novo_menor_teor_k_ctc_20_40")
    private Double k_content_cec_20_40_too_low;

    @JsonProperty("novo_teor_inicial_baixo_k_ctc_20_40")
    private Double k_content_cec_20_40_low_i;

    @JsonProperty("novo_teor_final_baixo_k_ctc_20_40")
    private Double k_content_cec_20_40_low_f;

    @JsonProperty("novo_teor_inicial_medio_k_ctc_20_40")
    private Double k_content_cec_20_40_medium_i;

    @JsonProperty("novo_teor_final_medio_k_ctc_20_40")
    private Double k_content_cec_20_40_medium_f;

    @JsonProperty("novo_teor_inicial_alto_k_ctc_20_40")
    private Double k_content_cec_20_40_hight_i;

    @JsonProperty("novo_teor_final_alto_k_ctc_20_40")
    private Double k_content_cec_20_40_hight_f;

    @JsonProperty("novo_maior_teor_k_ctc_20_40")
    private Double k_content_cec_20_40_too_hight;

    // --- CTC 41 a 80 mmolc/dm³ ---
    @JsonProperty("novo_menor_teor_k_ctc_41_80")
    private Double k_content_cec_41_80_too_low;

    @JsonProperty("novo_teor_inicial_baixo_k_ctc_41_80")
    private Double k_content_cec_41_80_low_i;

    @JsonProperty("novo_teor_final_baixo_k_ctc_41_80")
    private Double k_content_cec_41_80_low_f;

    @JsonProperty("novo_teor_inicial_medio_k_ctc_41_80")
    private Double k_content_cec_41_80_medium_i;

    @JsonProperty("novo_teor_final_medio_k_ctc_41_80")
    private Double k_content_cec_41_80_medium_f;

    @JsonProperty("novo_teor_inicial_alto_k_ctc_41_80")
    private Double k_content_cec_41_80_hight_i;

    @JsonProperty("novo_teor_final_alto_k_ctc_41_80")
    private Double k_content_cec_41_80_hight_f;

    @JsonProperty("novo_maior_teor_k_ctc_41_80")
    private Double k_content_cec_41_80_too_hight;

    // --- CTC 81 a 120 mmolc/dm³ ---
    @JsonProperty("novo_menor_teor_k_ctc_81_120")
    private Double k_content_cec_81_120_too_low;

    @JsonProperty("novo_teor_inicial_baixo_k_ctc_81_120")
    private Double k_content_cec_81_120_low_i;

    @JsonProperty("novo_teor_final_baixo_k_ctc_81_120")
    private Double k_content_cec_81_120_low_f;

    @JsonProperty("novo_teor_inicial_medio_k_ctc_81_120")
    private Double k_content_cec_81_120_medium_i;

    @JsonProperty("novo_teor_final_medio_k_ctc_81_120")
    private Double k_content_cec_81_120_medium_f;

    @JsonProperty("novo_teor_inicial_alto_k_ctc_81_120")
    private Double k_content_cec_81_120_hight_i;

    @JsonProperty("novo_teor_final_alto_k_ctc_81_120")
    private Double k_content_cec_81_120_hight_f;

    @JsonProperty("novo_maior_teor_k_ctc_81_120")
    private Double k_content_cec_81_120_too_hight;

    // --- CTC > 120 mmolc/dm³ ---
    @JsonProperty("novo_menor_teor_k_ctc_maior_120")
    private Double k_content_cec_greater_120_too_low;

    @JsonProperty("novo_teor_inicial_baixo_k_ctc_maior_120")
    private Double k_content_cec_greater_120_low_i;

    @JsonProperty("novo_teor_final_baixo_k_ctc_maior_120")
    private Double k_content_cec_greater_120_low_f;

    @JsonProperty("novo_teor_inicial_medio_k_ctc_maior_120")
    private Double k_content_cec_greater_120_medium_i;

    @JsonProperty("novo_teor_final_medio_k_ctc_maior_120")
    private Double k_content_cec_greater_120_medium_f;

    @JsonProperty("novo_teor_inicial_alto_k_ctc_maior_120")
    private Double k_content_cec_greater_120_hight_i;

    @JsonProperty("novo_teor_final_alto_k_ctc_maior_120")
    private Double k_content_cec_greater_120_hight_f;

    @JsonProperty("novo_maior_teor_k_ctc_maior_120")
    private Double k_content_cec_greater_120_too_hight;
}