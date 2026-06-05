package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailablePAnionExchangeResinExtractorPostRequestDto {

    @JsonProperty("novo_observacoes")
    private String observations;

    @JsonProperty("novo_fontes")
    private String sources;

    // --- ALGODAO ---
    @JsonProperty("novo_menor_teor_fosforo_solo_algodao")
    private Double p_content_cotton_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_algodao")
    private Double p_content_cotton_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_algodao")
    private Double p_content_cotton_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_algodao")
    private Double p_content_cotton_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_algodao")
    private Double p_content_cotton_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_algodao")
    private Double p_content_cotton_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_algodao")
    private Double p_content_cotton_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_algodao")
    private Double p_content_cotton_too_hight;

    // --- AMENDOIM ---
    @JsonProperty("novo_menor_teor_fosforo_solo_amendoim")
    private Double p_content_peanut_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_amendoim")
    private Double p_content_peanut_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_amendoim")
    private Double p_content_peanut_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_amendoim")
    private Double p_content_peanut_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_amendoim")
    private Double p_content_peanut_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_amendoim")
    private Double p_content_peanut_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_amendoim")
    private Double p_content_peanut_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_amendoim")
    private Double p_content_peanut_too_hight;

    // --- CANA_DE_ACUCAR ---
    @JsonProperty("novo_menor_teor_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_cana_de_acucar")
    private Double p_content_sugar_cane_too_hight;

    // --- FEIJAO_CAUPI ---
    @JsonProperty("novo_menor_teor_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_feijao_caupi")
    private Double p_content_cowpea_too_hight;

    // --- FEIJAO_COMUM ---
    @JsonProperty("novo_menor_teor_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_feijao_comum")
    private Double p_content_common_bean_too_hight;

    // --- GERGELIM ---
    @JsonProperty("novo_menor_teor_fosforo_solo_gergelim")
    private Double p_content_sesame_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_gergelim")
    private Double p_content_sesame_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_gergelim")
    private Double p_content_sesame_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_gergelim")
    private Double p_content_sesame_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_gergelim")
    private Double p_content_sesame_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_gergelim")
    private Double p_content_sesame_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_gergelim")
    private Double p_content_sesame_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_gergelim")
    private Double p_content_sesame_too_hight;

    // --- MAMONA ---
    @JsonProperty("novo_menor_teor_fosforo_solo_mamona")
    private Double p_content_castor_bean_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_mamona")
    private Double p_content_castor_bean_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_mamona")
    private Double p_content_castor_bean_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_mamona")
    private Double p_content_castor_bean_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_mamona")
    private Double p_content_castor_bean_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_mamona")
    private Double p_content_castor_bean_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_mamona")
    private Double p_content_castor_bean_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_mamona")
    private Double p_content_castor_bean_too_hight;

    // --- MILHO ---
    @JsonProperty("novo_menor_teor_fosforo_solo_milho")
    private Double p_content_corn_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_milho")
    private Double p_content_corn_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_milho")
    private Double p_content_corn_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_milho")
    private Double p_content_corn_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_milho")
    private Double p_content_corn_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_milho")
    private Double p_content_corn_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_milho")
    private Double p_content_corn_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_milho")
    private Double p_content_corn_too_hight;

    // --- SISAL ---
    @JsonProperty("novo_menor_teor_fosforo_solo_sisal")
    private Double p_content_sisal_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_sisal")
    private Double p_content_sisal_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_sisal")
    private Double p_content_sisal_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_sisal")
    private Double p_content_sisal_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_sisal")
    private Double p_content_sisal_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_sisal")
    private Double p_content_sisal_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_sisal")
    private Double p_content_sisal_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_sisal")
    private Double p_content_sisal_too_hight;

    // --- SOJA ---
    @JsonProperty("novo_menor_teor_fosforo_solo_soja")
    private Double p_content_soybean_too_low;

    @JsonProperty("novo_teor_inicial_baixo_fosforo_solo_soja")
    private Double p_content_soybean_low_i;

    @JsonProperty("novo_teor_final_baixo_fosforo_solo_soja")
    private Double p_content_soybean_low_f;

    @JsonProperty("novo_teor_inicial_medio_fosforo_solo_soja")
    private Double p_content_soybean_medium_i;

    @JsonProperty("novo_teor_final_medio_fosforo_solo_soja")
    private Double p_content_soybean_medium_f;

    @JsonProperty("novo_teor_inicial_alto_fosforo_solo_soja")
    private Double p_content_soybean_hight_i;

    @JsonProperty("novo_teor_final_alto_fosforo_solo_soja")
    private Double p_content_soybean_hight_f;

    @JsonProperty("novo_maior_teor_fosforo_solo_soja")
    private Double p_content_soybean_too_hight;
}
