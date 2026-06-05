package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor;

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
public class AvailablePAnionExchangeResinExtractorResponseDto {

    @Schema(description = "ID do registro")
    private Long id;

    @Schema(description = "ID da tabela pai")
    @JsonProperty("id_tabela")
    private Long tableId;

    @JsonProperty("observacoes")
    private String observations;

    @JsonProperty("fontes")
    private String sources;

    // --- ALGODAO ---
    @JsonProperty("menor_teor_fosforo_solo_algodao")
    private Double p_content_cotton_too_low;

    @JsonProperty("teor_inicial_baixo_fosforo_solo_algodao")
    private Double p_content_cotton_low_i;

    @JsonProperty("teor_final_baixo_fosforo_solo_algodao")
    private Double p_content_cotton_low_f;

    @JsonProperty("teor_inicial_medio_fosforo_solo_algodao")
    private Double p_content_cotton_medium_i;

    @JsonProperty("teor_final_medio_fosforo_solo_algodao")
    private Double p_content_cotton_medium_f;

    @JsonProperty("teor_inicial_alto_fosforo_solo_algodao")
    private Double p_content_cotton_hight_i;

    @JsonProperty("teor_final_alto_fosforo_solo_algodao")
    private Double p_content_cotton_hight_f;

    @JsonProperty("maior_teor_fosforo_solo_algodao")
    private Double p_content_cotton_too_hight;

    // --- AMENDOIM ---
    @JsonProperty("menor_teor_fosforo_solo_amendoim")
    private Double p_content_peanut_too_low;

    @JsonProperty("teor_inicial_baixo_fosforo_solo_amendoim")
    private Double p_content_peanut_low_i;

    @JsonProperty("teor_final_baixo_fosforo_solo_amendoim")
    private Double p_content_peanut_low_f;

    @JsonProperty("teor_inicial_medio_fosforo_solo_amendoim")
    private Double p_content_peanut_medium_i;

    @JsonProperty("teor_final_medio_fosforo_solo_amendoim")
    private Double p_content_peanut_medium_f;

    @JsonProperty("teor_inicial_alto_fosforo_solo_amendoim")
    private Double p_content_peanut_hight_i;

    @JsonProperty("teor_final_alto_fosforo_solo_amendoim")
    private Double p_content_peanut_hight_f;

    @JsonProperty("maior_teor_fosforo_solo_amendoim")
    private Double p_content_peanut_too_hight;

    // --- CANA_DE_ACUCAR ---
    @JsonProperty("menor_teor_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_too_low;

    @JsonProperty("teor_inicial_baixo_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_low_i;

    @JsonProperty("teor_final_baixo_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_low_f;

    @JsonProperty("teor_inicial_medio_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_medium_i;

    @JsonProperty("teor_final_medio_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_medium_f;

    @JsonProperty("teor_inicial_alto_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_hight_i;

    @JsonProperty("teor_final_alto_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_hight_f;

    @JsonProperty("maior_teor_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_too_hight;

    // --- FEIJAO_CAUPI ---
    @JsonProperty("menor_teor_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_too_low;

    @JsonProperty("teor_inicial_baixo_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_low_i;

    @JsonProperty("teor_final_baixo_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_low_f;

    @JsonProperty("teor_inicial_medio_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_medium_i;

    @JsonProperty("teor_final_medio_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_medium_f;

    @JsonProperty("teor_inicial_alto_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_hight_i;

    @JsonProperty("teor_final_alto_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_hight_f;

    @JsonProperty("maior_teor_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_too_hight;

    // --- FEIJAO_COMUM ---
    @JsonProperty("menor_teor_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_too_low;

    @JsonProperty("teor_inicial_baixo_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_low_i;

    @JsonProperty("teor_final_baixo_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_low_f;

    @JsonProperty("teor_inicial_medio_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_medium_i;

    @JsonProperty("teor_final_medio_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_medium_f;

    @JsonProperty("teor_inicial_alto_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_hight_i;

    @JsonProperty("teor_final_alto_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_hight_f;

    @JsonProperty("maior_teor_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_too_hight;

    // --- GERGELIM ---
    @JsonProperty("menor_teor_fosforo_solo_gergelim")
    private Double p_content_sesame_too_low;

    @JsonProperty("teor_inicial_baixo_fosforo_solo_gergelim")
    private Double p_content_sesame_low_i;

    @JsonProperty("teor_final_baixo_fosforo_solo_gergelim")
    private Double p_content_sesame_low_f;

    @JsonProperty("teor_inicial_medio_fosforo_solo_gergelim")
    private Double p_content_sesame_medium_i;

    @JsonProperty("teor_final_medio_fosforo_solo_gergelim")
    private Double p_content_sesame_medium_f;

    @JsonProperty("teor_inicial_alto_fosforo_solo_gergelim")
    private Double p_content_sesame_hight_i;

    @JsonProperty("teor_final_alto_fosforo_solo_gergelim")
    private Double p_content_sesame_hight_f;

    @JsonProperty("maior_teor_fosforo_solo_gergelim")
    private Double p_content_sesame_too_hight;

    // --- MAMONA ---
    @JsonProperty("menor_teor_fosforo_solo_mamona")
    private Double p_content_castor_bean_too_low;

    @JsonProperty("teor_inicial_baixo_fosforo_solo_mamona")
    private Double p_content_castor_bean_low_i;

    @JsonProperty("teor_final_baixo_fosforo_solo_mamona")
    private Double p_content_castor_bean_low_f;

    @JsonProperty("teor_inicial_medio_fosforo_solo_mamona")
    private Double p_content_castor_bean_medium_i;

    @JsonProperty("teor_final_medio_fosforo_solo_mamona")
    private Double p_content_castor_bean_medium_f;

    @JsonProperty("teor_inicial_alto_fosforo_solo_mamona")
    private Double p_content_castor_bean_hight_i;

    @JsonProperty("teor_final_alto_fosforo_solo_mamona")
    private Double p_content_castor_bean_hight_f;

    @JsonProperty("maior_teor_fosforo_solo_mamona")
    private Double p_content_castor_bean_too_hight;

    // --- MILHO ---
    @JsonProperty("menor_teor_fosforo_solo_milho")
    private Double p_content_corn_too_low;

    @JsonProperty("teor_inicial_baixo_fosforo_solo_milho")
    private Double p_content_corn_low_i;

    @JsonProperty("teor_final_baixo_fosforo_solo_milho")
    private Double p_content_corn_low_f;

    @JsonProperty("teor_inicial_medio_fosforo_solo_milho")
    private Double p_content_corn_medium_i;

    @JsonProperty("teor_final_medio_fosforo_solo_milho")
    private Double p_content_corn_medium_f;

    @JsonProperty("teor_inicial_alto_fosforo_solo_milho")
    private Double p_content_corn_hight_i;

    @JsonProperty("teor_final_alto_fosforo_solo_milho")
    private Double p_content_corn_hight_f;

    @JsonProperty("maior_teor_fosforo_solo_milho")
    private Double p_content_corn_too_hight;

    // --- SISAL ---
    @JsonProperty("menor_teor_fosforo_solo_sisal")
    private Double p_content_sisal_too_low;

    @JsonProperty("teor_inicial_baixo_fosforo_solo_sisal")
    private Double p_content_sisal_low_i;

    @JsonProperty("teor_final_baixo_fosforo_solo_sisal")
    private Double p_content_sisal_low_f;

    @JsonProperty("teor_inicial_medio_fosforo_solo_sisal")
    private Double p_content_sisal_medium_i;

    @JsonProperty("teor_final_medio_fosforo_solo_sisal")
    private Double p_content_sisal_medium_f;

    @JsonProperty("teor_inicial_alto_fosforo_solo_sisal")
    private Double p_content_sisal_hight_i;

    @JsonProperty("teor_final_alto_fosforo_solo_sisal")
    private Double p_content_sisal_hight_f;

    @JsonProperty("maior_teor_fosforo_solo_sisal")
    private Double p_content_sisal_too_hight;

    // --- SOJA ---
    @JsonProperty("menor_teor_fosforo_solo_soja")
    private Double p_content_soybean_too_low;

    @JsonProperty("teor_inicial_baixo_fosforo_solo_soja")
    private Double p_content_soybean_low_i;

    @JsonProperty("teor_final_baixo_fosforo_solo_soja")
    private Double p_content_soybean_low_f;

    @JsonProperty("teor_inicial_medio_fosforo_solo_soja")
    private Double p_content_soybean_medium_i;

    @JsonProperty("teor_final_medio_fosforo_solo_soja")
    private Double p_content_soybean_medium_f;

    @JsonProperty("teor_inicial_alto_fosforo_solo_soja")
    private Double p_content_soybean_hight_i;

    @JsonProperty("teor_final_alto_fosforo_solo_soja")
    private Double p_content_soybean_hight_f;

    @JsonProperty("maior_teor_fosforo_solo_soja")
    private Double p_content_soybean_too_hight;
}
