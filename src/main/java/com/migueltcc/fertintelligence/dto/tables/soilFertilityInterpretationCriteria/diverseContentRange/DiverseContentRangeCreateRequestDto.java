package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiverseContentRangeCreateRequestDto {

    // --- CARBONO ORGÂNICO ---
    @JsonProperty("menor_teor_carbono_organico")
    private Double organic_carbon_too_low;
    @JsonProperty("teor_inicial_baixo_carbono_organico")
    private Double organic_carbon_low_i;
    @JsonProperty("teor_final_baixo_carbono_organico")
    private Double organic_carbon_low_f;
    @JsonProperty("teor_inicial_medio_carbono_organico")
    private Double organic_carbon_medium_i;
    @JsonProperty("teor_final_medio_carbono_organico")
    private Double organic_carbon_medium_f;
    @JsonProperty("teor_inicial_alto_carbono_organico")
    private Double organic_carbon_hight_i;
    @JsonProperty("teor_final_alto_carbono_organico")
    private Double organic_carbon_hight_f;
    @JsonProperty("maior_teor_carbono_organico")
    private Double organic_carbon_too_hight;

    // --- MATÉRIA ORGÂNICA ---
    @JsonProperty("menor_teor_materia_organica")
    private Double organic_matter_too_low;
    @JsonProperty("teor_inicial_baixo_materia_organica")
    private Double organic_matter_low_i;
    @JsonProperty("teor_final_baixo_materia_organica")
    private Double organic_matter_low_f;
    @JsonProperty("teor_inicial_medio_materia_organica")
    private Double organic_matter_medium_i;
    @JsonProperty("teor_final_medio_materia_organica")
    private Double organic_matter_medium_f;
    @JsonProperty("teor_inicial_alto_materia_organica")
    private Double organic_matter_hight_i;
    @JsonProperty("teor_final_alto_materia_organica")
    private Double organic_matter_hight_f;
    @JsonProperty("maior_teor_materia_organica")
    private Double organic_matter_too_hight;

    // --- CÁLCIO ---
    @JsonProperty("menor_teor_calcio")
    private Double calcium_too_low;
    @JsonProperty("teor_inicial_baixo_calcio")
    private Double calcium_low_i;
    @JsonProperty("teor_final_baixo_calcio")
    private Double calcium_low_f;
    @JsonProperty("teor_inicial_medio_calcio")
    private Double calcium_medium_i;
    @JsonProperty("teor_final_medio_calcio")
    private Double calcium_medium_f;
    @JsonProperty("teor_inicial_alto_calcio")
    private Double calcium_hight_i;
    @JsonProperty("teor_final_alto_calcio")
    private Double calcium_hight_f;
    @JsonProperty("maior_teor_calcio")
    private Double calcium_too_hight;

    // --- MAGNÉSIO ---
    @JsonProperty("menor_teor_magnesio")
    private Double magnesium_too_low;
    @JsonProperty("teor_inicial_baixo_magnesio")
    private Double magnesium_low_i;
    @JsonProperty("teor_final_baixo_magnesio")
    private Double magnesium_low_f;
    @JsonProperty("teor_inicial_medio_magnesio")
    private Double magnesium_medium_i;
    @JsonProperty("teor_final_medio_magnesio")
    private Double magnesium_medium_f;
    @JsonProperty("teor_inicial_alto_magnesio")
    private Double magnesium_hight_i;
    @JsonProperty("teor_final_alto_magnesio")
    private Double magnesium_hight_f;
    @JsonProperty("maior_teor_magnesio")
    private Double magnesium_too_hight;

    // --- POTÁSSIO ---
    @JsonProperty("menor_teor_potassio")
    private Double potassium_too_low;
    @JsonProperty("teor_inicial_baixo_potassio")
    private Double potassium_low_i;
    @JsonProperty("teor_final_baixo_potassio")
    private Double potassium_low_f;
    @JsonProperty("teor_inicial_medio_potassio")
    private Double potassium_medium_i;
    @JsonProperty("teor_final_medio_potassio")
    private Double potassium_medium_f;
    @JsonProperty("teor_inicial_alto_potassio")
    private Double potassium_hight_i;
    @JsonProperty("teor_final_alto_potassio")
    private Double potassium_hight_f;
    @JsonProperty("maior_teor_potassio")
    private Double potassium_too_hight;

    // --- SÓDIO ---
    @JsonProperty("menor_teor_sodio")
    private Double sodium_too_low;
    @JsonProperty("teor_inicial_baixo_sodio")
    private Double sodium_low_i;
    @JsonProperty("teor_final_baixo_sodio")
    private Double sodium_low_f;
    @JsonProperty("teor_inicial_medio_sodio")
    private Double sodium_medium_i;
    @JsonProperty("teor_final_medio_sodio")
    private Double sodium_medium_f;
    @JsonProperty("teor_inicial_alto_sodio")
    private Double sodium_hight_i;
    @JsonProperty("teor_final_alto_sodio")
    private Double sodium_hight_f;
    @JsonProperty("maior_teor_sodio")
    private Double sodium_too_hight;

    // --- SOMA DE BASES ---
    @JsonProperty("menor_teor_soma_bases")
    private Double sum_of_bases_too_low;
    @JsonProperty("teor_inicial_baixo_soma_bases")
    private Double sum_of_bases_low_i;
    @JsonProperty("teor_final_baixo_soma_bases")
    private Double sum_of_bases_low_f;
    @JsonProperty("teor_inicial_medio_soma_bases")
    private Double sum_of_bases_medium_i;
    @JsonProperty("teor_final_medio_soma_bases")
    private Double sum_of_bases_medium_f;
    @JsonProperty("teor_inicial_alto_soma_bases")
    private Double sum_of_bases_hight_i;
    @JsonProperty("teor_final_alto_soma_bases")
    private Double sum_of_bases_hight_f;
    @JsonProperty("maior_teor_soma_bases")
    private Double sum_of_bases_too_hight;

    // --- ALUMÍNIO ---
    @JsonProperty("menor_teor_aluminio")
    @JsonAlias("menor_teor_al_trocavel")
    private Double aluminum_too_low;
    @JsonProperty("teor_inicial_baixo_aluminio")
    @JsonAlias("teor_inicial_baixo_al_trocavel")
    private Double aluminum_low_i;
    @JsonProperty("teor_final_baixo_aluminio")
    @JsonAlias("teor_final_baixo_al_trocavel")
    private Double aluminum_low_f;
    @JsonProperty("teor_inicial_medio_aluminio")
    @JsonAlias("teor_inicial_medio_al_trocavel")
    private Double aluminum_medium_i;
    @JsonProperty("teor_final_medio_aluminio")
    @JsonAlias("teor_final_medio_al_trocavel")
    private Double aluminum_medium_f;
    @JsonProperty("teor_inicial_alto_aluminio")
    @JsonAlias("teor_inicial_alto_al_trocavel")
    private Double aluminum_hight_i;
    @JsonProperty("teor_final_alto_aluminio")
    @JsonAlias("teor_final_alto_al_trocavel")
    private Double aluminum_hight_f;
    @JsonProperty("maior_teor_aluminio")
    @JsonAlias("maior_teor_al_trocavel")
    private Double aluminum_too_hight;

    // --- ACIDEZ POTENCIAL ---
    @JsonProperty("menor_teor_acidez_potencial")
    @JsonAlias({"menor_teor_h_al", "menor_teor_h_mais_al", "menor_teor_aluminio_mais_hidrogenio"})
    private Double potential_acidity_too_low;
    @JsonProperty("teor_inicial_baixo_acidez_potencial")
    @JsonAlias({"teor_inicial_baixo_h_al", "teor_inicial_baixo_h_mais_al", "teor_inicial_baixo_aluminio_mais_hidrogenio"})
    private Double potential_acidity_low_i;
    @JsonProperty("teor_final_baixo_acidez_potencial")
    @JsonAlias({"teor_final_baixo_h_al", "teor_final_baixo_h_mais_al", "teor_final_baixo_aluminio_mais_hidrogenio"})
    private Double potential_acidity_low_f;
    @JsonProperty("teor_inicial_medio_acidez_potencial")
    @JsonAlias({"teor_inicial_medio_h_al", "teor_inicial_medio_h_mais_al", "teor_inicial_medio_aluminio_mais_hidrogenio"})
    private Double potential_acidity_medium_i;
    @JsonProperty("teor_final_medio_acidez_potencial")
    @JsonAlias({"teor_final_medio_h_al", "teor_final_medio_h_mais_al", "teor_final_medio_aluminio_mais_hidrogenio"})
    private Double potential_acidity_medium_f;
    @JsonProperty("teor_inicial_alto_acidez_potencial")
    @JsonAlias({"teor_inicial_alto_h_al", "teor_inicial_alto_h_mais_al", "teor_inicial_alto_aluminio_mais_hidrogenio"})
    private Double potential_acidity_hight_i;
    @JsonProperty("teor_final_alto_acidez_potencial")
    @JsonAlias({"teor_final_alto_h_al", "teor_final_alto_h_mais_al", "teor_final_alto_aluminio_mais_hidrogenio"})
    private Double potential_acidity_hight_f;
    @JsonProperty("maior_teor_acidez_potencial")
    @JsonAlias({"maior_teor_h_al", "maior_teor_h_mais_al", "maior_teor_aluminio_mais_hidrogenio"})
    private Double potential_acidity_too_hight;

    // --- CTC EFETIVA ---
    @JsonProperty("menor_teor_ctc_efetiva")
    @JsonAlias({"menor_teor_ctc_t", "menor_teor_ctc_efetiva_t"})
    private Double effective_cec_too_low;
    @JsonProperty("teor_inicial_baixo_ctc_efetiva")
    @JsonAlias({"teor_inicial_baixo_ctc_t", "teor_inicial_baixo_ctc_efetiva_t"})
    private Double effective_cec_low_i;
    @JsonProperty("teor_final_baixo_ctc_efetiva")
    @JsonAlias({"teor_final_baixo_ctc_t", "teor_final_baixo_ctc_efetiva_t"})
    private Double effective_cec_low_f;
    @JsonProperty("teor_inicial_medio_ctc_efetiva")
    @JsonAlias({"teor_inicial_medio_ctc_t", "teor_inicial_medio_ctc_efetiva_t"})
    private Double effective_cec_medium_i;
    @JsonProperty("teor_final_medio_ctc_efetiva")
    @JsonAlias({"teor_final_medio_ctc_t", "teor_final_medio_ctc_efetiva_t"})
    private Double effective_cec_medium_f;
    @JsonProperty("teor_inicial_alto_ctc_efetiva")
    @JsonAlias({"teor_inicial_alto_ctc_t", "teor_inicial_alto_ctc_efetiva_t"})
    private Double effective_cec_hight_i;
    @JsonProperty("teor_final_alto_ctc_efetiva")
    @JsonAlias({"teor_final_alto_ctc_t", "teor_final_alto_ctc_efetiva_t"})
    private Double effective_cec_hight_f;
    @JsonProperty("maior_teor_ctc_efetiva")
    @JsonAlias({"maior_teor_ctc_t", "maior_teor_ctc_efetiva_t"})
    private Double effective_cec_too_hight;

    // --- CTC pH 7,0 ---
    @JsonProperty("menor_teor_ctc_ph_7")
    @JsonAlias({"menor_teor_ctc_T", "menor_teor_ctc_total", "menor_teor_ctc_t_ph7", "menor_teor_ctc_ph7"})
    private Double ph7_cec_too_low;
    @JsonProperty("teor_inicial_baixo_ctc_ph_7")
    @JsonAlias({"teor_inicial_baixo_ctc_T", "teor_inicial_baixo_ctc_total", "teor_inicial_baixo_ctc_t_ph7", "teor_inicial_baixo_ctc_ph7"})
    private Double ph7_cec_low_i;
    @JsonProperty("teor_final_baixo_ctc_ph_7")
    @JsonAlias({"teor_final_baixo_ctc_T", "teor_final_baixo_ctc_total", "teor_final_baixo_ctc_t_ph7", "teor_final_baixo_ctc_ph7"})
    private Double ph7_cec_low_f;
    @JsonProperty("teor_inicial_medio_ctc_ph_7")
    @JsonAlias({"teor_inicial_medio_ctc_T", "teor_inicial_medio_ctc_total", "teor_inicial_medio_ctc_t_ph7", "teor_inicial_medio_ctc_ph7"})
    private Double ph7_cec_medium_i;
    @JsonProperty("teor_final_medio_ctc_ph_7")
    @JsonAlias({"teor_final_medio_ctc_T", "teor_final_medio_ctc_total", "teor_final_medio_ctc_t_ph7", "teor_final_medio_ctc_ph7"})
    private Double ph7_cec_medium_f;
    @JsonProperty("teor_inicial_alto_ctc_ph_7")
    @JsonAlias({"teor_inicial_alto_ctc_T", "teor_inicial_alto_ctc_total", "teor_inicial_alto_ctc_t_ph7", "teor_inicial_alto_ctc_ph7"})
    private Double ph7_cec_hight_i;
    @JsonProperty("teor_final_alto_ctc_ph_7")
    @JsonAlias({"teor_final_alto_ctc_T", "teor_final_alto_ctc_total", "teor_final_alto_ctc_t_ph7", "teor_final_alto_ctc_ph7"})
    private Double ph7_cec_hight_f;
    @JsonProperty("maior_teor_ctc_ph_7")
    @JsonAlias({"maior_teor_ctc_T", "maior_teor_ctc_total", "maior_teor_ctc_t_ph7", "maior_teor_ctc_ph7"})
    private Double ph7_cec_too_hight;

    // --- SATURAÇÃO POR ALUMÍNIO ---
    @JsonProperty("menor_teor_saturacao_aluminio")
    private Double aluminum_saturation_too_low;
    @JsonProperty("teor_inicial_baixo_saturacao_aluminio")
    private Double aluminum_saturation_low_i;
    @JsonProperty("teor_final_baixo_saturacao_aluminio")
    private Double aluminum_saturation_low_f;
    @JsonProperty("teor_inicial_medio_saturacao_aluminio")
    private Double aluminum_saturation_medium_i;
    @JsonProperty("teor_final_medio_saturacao_aluminio")
    private Double aluminum_saturation_medium_f;
    @JsonProperty("teor_inicial_alto_saturacao_aluminio")
    private Double aluminum_saturation_hight_i;
    @JsonProperty("teor_final_alto_saturacao_aluminio")
    private Double aluminum_saturation_hight_f;
    @JsonProperty("maior_teor_saturacao_aluminio")
    private Double aluminum_saturation_too_hight;

    // --- SATURAÇÃO POR BASES ---
    @JsonProperty("menor_teor_saturacao_bases")
    private Double base_saturation_too_low;
    @JsonProperty("teor_inicial_baixo_saturacao_bases")
    private Double base_saturation_low_i;
    @JsonProperty("teor_final_baixo_saturacao_bases")
    private Double base_saturation_low_f;
    @JsonProperty("teor_inicial_medio_saturacao_bases")
    private Double base_saturation_medium_i;
    @JsonProperty("teor_final_medio_saturacao_bases")
    private Double base_saturation_medium_f;
    @JsonProperty("teor_inicial_alto_saturacao_bases")
    private Double base_saturation_hight_i;
    @JsonProperty("teor_final_alto_saturacao_bases")
    private Double base_saturation_hight_f;
    @JsonProperty("maior_teor_saturacao_bases")
    private Double base_saturation_too_hight;

    // --- PST ---
    @JsonProperty("menor_teor_pst")
    private Double sodium_saturation_too_low;
    @JsonProperty("teor_inicial_baixo_pst")
    private Double sodium_saturation_low_i;
    @JsonProperty("teor_final_baixo_pst")
    private Double sodium_saturation_low_f;
    @JsonProperty("teor_inicial_medio_pst")
    private Double sodium_saturation_medium_i;
    @JsonProperty("teor_final_medio_pst")
    private Double sodium_saturation_medium_f;
    @JsonProperty("teor_inicial_alto_pst")
    private Double sodium_saturation_hight_i;
    @JsonProperty("teor_final_alto_pst")
    private Double sodium_saturation_hight_f;
    @JsonProperty("maior_teor_pst")
    private Double sodium_saturation_too_hight;

    // --- pH EM ÁGUA ---
    @JsonProperty("menor_valor_ph")
    @JsonAlias({"menor_valor_ph_agua", "menor_teor_ph_agua"})
    private Double ph_too_low;
    @JsonProperty("valor_inicial_baixo_ph")
    @JsonAlias({"valor_inicial_baixo_ph_agua", "teor_inicial_baixo_ph_agua"})
    private Double ph_low_i;
    @JsonProperty("valor_final_baixo_ph")
    @JsonAlias({"valor_final_baixo_ph_agua", "teor_final_baixo_ph_agua"})
    private Double ph_low_f;
    @JsonProperty("valor_inicial_medio_ph")
    @JsonAlias({"valor_inicial_medio_ph_agua", "teor_inicial_medio_ph_agua"})
    private Double ph_medium_i;
    @JsonProperty("valor_final_medio_ph")
    @JsonAlias({"valor_final_medio_ph_agua", "teor_final_medio_ph_agua"})
    private Double ph_medium_f;
    @JsonProperty("valor_inicial_alto_ph")
    @JsonAlias({"valor_inicial_alto_ph_agua", "teor_inicial_alto_ph_agua"})
    private Double ph_hight_i;
    @JsonProperty("valor_final_alto_ph")
    @JsonAlias({"valor_final_alto_ph_agua", "teor_final_alto_ph_agua"})
    private Double ph_hight_f;
    @JsonProperty("maior_valor_ph")
    @JsonAlias({"maior_valor_ph_agua", "maior_teor_ph_agua"})
    private Double ph_too_hight;

    // --- pH EM CaCl2 0,01 mol/L ---
    @JsonProperty("menor_valor_ph_cacl2")
    @JsonAlias("menor_valor_ph_cacl2_0_01_mol_l")
    private Double ph_cacl2_too_low;
    @JsonProperty("valor_inicial_baixo_ph_cacl2")
    @JsonAlias("valor_inicial_baixo_ph_cacl2_0_01_mol_l")
    private Double ph_cacl2_low_i;
    @JsonProperty("valor_final_baixo_ph_cacl2")
    @JsonAlias("valor_final_baixo_ph_cacl2_0_01_mol_l")
    private Double ph_cacl2_low_f;
    @JsonProperty("valor_inicial_medio_ph_cacl2")
    @JsonAlias("valor_inicial_medio_ph_cacl2_0_01_mol_l")
    private Double ph_cacl2_medium_i;
    @JsonProperty("valor_final_medio_ph_cacl2")
    @JsonAlias("valor_final_medio_ph_cacl2_0_01_mol_l")
    private Double ph_cacl2_medium_f;
    @JsonProperty("valor_inicial_alto_ph_cacl2")
    @JsonAlias("valor_inicial_alto_ph_cacl2_0_01_mol_l")
    private Double ph_cacl2_hight_i;
    @JsonProperty("valor_final_alto_ph_cacl2")
    @JsonAlias("valor_final_alto_ph_cacl2_0_01_mol_l")
    private Double ph_cacl2_hight_f;
    @JsonProperty("maior_valor_ph_cacl2")
    @JsonAlias("maior_valor_ph_cacl2_0_01_mol_l")
    private Double ph_cacl2_too_hight;

    // --- BORO ---
    @JsonProperty("menor_teor_boro")
    private Double boron_too_low;
    @JsonProperty("teor_inicial_baixo_boro")
    private Double boron_low_i;
    @JsonProperty("teor_final_baixo_boro")
    private Double boron_low_f;
    @JsonProperty("teor_inicial_medio_boro")
    private Double boron_medium_i;
    @JsonProperty("teor_final_medio_boro")
    private Double boron_medium_f;
    @JsonProperty("teor_inicial_alto_boro")
    private Double boron_hight_i;
    @JsonProperty("teor_final_alto_boro")
    private Double boron_hight_f;
    @JsonProperty("maior_teor_boro")
    private Double boron_too_hight;

    // --- COBRE ---
    @JsonProperty("menor_teor_cobre")
    private Double copper_too_low;
    @JsonProperty("teor_inicial_baixo_cobre")
    private Double copper_low_i;
    @JsonProperty("teor_final_baixo_cobre")
    private Double copper_low_f;
    @JsonProperty("teor_inicial_medio_cobre")
    private Double copper_medium_i;
    @JsonProperty("teor_final_medio_cobre")
    private Double copper_medium_f;
    @JsonProperty("teor_inicial_alto_cobre")
    private Double copper_hight_i;
    @JsonProperty("teor_final_alto_cobre")
    private Double copper_hight_f;
    @JsonProperty("maior_teor_cobre")
    private Double copper_too_hight;

    // --- FERRO ---
    @JsonProperty("menor_teor_ferro")
    private Double iron_too_low;
    @JsonProperty("teor_inicial_baixo_ferro")
    private Double iron_low_i;
    @JsonProperty("teor_final_baixo_ferro")
    private Double iron_low_f;
    @JsonProperty("teor_inicial_medio_ferro")
    private Double iron_medium_i;
    @JsonProperty("teor_final_medio_ferro")
    private Double iron_medium_f;
    @JsonProperty("teor_inicial_alto_ferro")
    private Double iron_hight_i;
    @JsonProperty("teor_final_alto_ferro")
    private Double iron_hight_f;
    @JsonProperty("maior_teor_ferro")
    private Double iron_too_hight;

    // --- MANGANÊS ---
    @JsonProperty("menor_teor_manganes")
    private Double manganese_too_low;
    @JsonProperty("teor_inicial_baixo_manganes")
    private Double manganese_low_i;
    @JsonProperty("teor_final_baixo_manganes")
    private Double manganese_low_f;
    @JsonProperty("teor_inicial_medio_manganes")
    private Double manganese_medium_i;
    @JsonProperty("teor_final_medio_manganes")
    private Double manganese_medium_f;
    @JsonProperty("teor_inicial_alto_manganes")
    private Double manganese_hight_i;
    @JsonProperty("teor_final_alto_manganes")
    private Double manganese_hight_f;
    @JsonProperty("maior_teor_manganes")
    private Double manganese_too_hight;

    // --- ZINCO ---
    @JsonProperty("menor_teor_zinco")
    private Double zinc_too_low;
    @JsonProperty("teor_inicial_baixo_zinco")
    private Double zinc_low_i;
    @JsonProperty("teor_final_baixo_zinco")
    private Double zinc_low_f;
    @JsonProperty("teor_inicial_medio_zinco")
    private Double zinc_medium_i;
    @JsonProperty("teor_final_medio_zinco")
    private Double zinc_medium_f;
    @JsonProperty("teor_inicial_alto_zinco")
    private Double zinc_hight_i;
    @JsonProperty("teor_final_alto_zinco")
    private Double zinc_hight_f;
    @JsonProperty("maior_teor_zinco")
    private Double zinc_too_hight;
}
