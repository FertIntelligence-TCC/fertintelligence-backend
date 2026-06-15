package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Data
@Table(name = "FAIXAS_DE_TEORES_DIVERSOS")
public class DiverseContentRangeModel {

    private static final String ORGANIC_CARBON_UNIT = "g/dm3";
    private static final String ORGANIC_MATTER_UNIT = "g/dm3";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    // =================================================================================
    // CARBONO ORGÂNICO (C.O.) - g/dm3
    // =================================================================================
    @Column(name = "UNIDADE_CARBONO_ORGANICO", nullable = false)
    @Builder.Default
    String organic_carbon_unit = ORGANIC_CARBON_UNIT;
    @Column(name = "MENOR_TEOR_CARBONO_ORGANICO", nullable = false)
    Double organic_carbon_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_CARBONO_ORGANICO", nullable = false)
    Double organic_carbon_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_CARBONO_ORGANICO", nullable = false)
    Double organic_carbon_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_CARBONO_ORGANICO", nullable = false)
    Double organic_carbon_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_CARBONO_ORGANICO", nullable = false)
    Double organic_carbon_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_CARBONO_ORGANICO", nullable = false)
    Double organic_carbon_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_CARBONO_ORGANICO", nullable = false)
    Double organic_carbon_hight_f;
    @Column(name = "MAIOR_TEOR_CARBONO_ORGANICO", nullable = false)
    Double organic_carbon_too_hight;

    // =================================================================================
    // MATÉRIA ORGÂNICA (M.O.) - g/dm3
    // =================================================================================
    @Column(name = "UNIDADE_MATERIA_ORGANICA", nullable = false)
    @Builder.Default
    String organic_matter_unit = ORGANIC_MATTER_UNIT;
    @Column(name = "MENOR_TEOR_MATERIA_ORGANICA", nullable = false)
    Double organic_matter_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_MATERIA_ORGANICA", nullable = false)
    Double organic_matter_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_MATERIA_ORGANICA", nullable = false)
    Double organic_matter_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_MATERIA_ORGANICA", nullable = false)
    Double organic_matter_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_MATERIA_ORGANICA", nullable = false)
    Double organic_matter_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_MATERIA_ORGANICA", nullable = false)
    Double organic_matter_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_MATERIA_ORGANICA", nullable = false)
    Double organic_matter_hight_f;
    @Column(name = "MAIOR_TEOR_MATERIA_ORGANICA", nullable = false)
    Double organic_matter_too_hight;

    // =================================================================================
    // CÁLCIO TROCÁVEL (Ca2+) - mmolc/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_CALCIO", nullable = false)
    Double calcium_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_CALCIO", nullable = false)
    Double calcium_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_CALCIO", nullable = false)
    Double calcium_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_CALCIO", nullable = false)
    Double calcium_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_CALCIO", nullable = false)
    Double calcium_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_CALCIO", nullable = false)
    Double calcium_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_CALCIO", nullable = false)
    Double calcium_hight_f;
    @Column(name = "MAIOR_TEOR_CALCIO", nullable = false)
    Double calcium_too_hight;

    // =================================================================================
    // MAGNÉSIO TROCÁVEL (Mg2+) - mmolc/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_MAGNESIO", nullable = false)
    Double magnesium_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_MAGNESIO", nullable = false)
    Double magnesium_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_MAGNESIO", nullable = false)
    Double magnesium_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_MAGNESIO", nullable = false)
    Double magnesium_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_MAGNESIO", nullable = false)
    Double magnesium_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_MAGNESIO", nullable = false)
    Double magnesium_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_MAGNESIO", nullable = false)
    Double magnesium_hight_f;
    @Column(name = "MAIOR_TEOR_MAGNESIO", nullable = false)
    Double magnesium_too_hight;

    // =================================================================================
    // POTÁSSIO TROCÁVEL (K+) - mmolc/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_POTASSIO", nullable = false)
    Double potassium_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_POTASSIO", nullable = false)
    Double potassium_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_POTASSIO", nullable = false)
    Double potassium_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_POTASSIO", nullable = false)
    Double potassium_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_POTASSIO", nullable = false)
    Double potassium_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_POTASSIO", nullable = false)
    Double potassium_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_POTASSIO", nullable = false)
    Double potassium_hight_f;
    @Column(name = "MAIOR_TEOR_POTASSIO", nullable = false)
    Double potassium_too_hight;

    // =================================================================================
    // SÓDIO TROCÁVEL (Na+) - mmolc/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_SODIO", nullable = false)
    Double sodium_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_SODIO", nullable = false)
    Double sodium_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_SODIO", nullable = false)
    Double sodium_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_SODIO", nullable = false)
    Double sodium_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_SODIO", nullable = false)
    Double sodium_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_SODIO", nullable = false)
    Double sodium_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_SODIO", nullable = false)
    Double sodium_hight_f;
    @Column(name = "MAIOR_TEOR_SODIO", nullable = false)
    Double sodium_too_hight;

    // =================================================================================
    // SOMA DE BASES TROCÁVEIS (SB) - mmolc/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_SOMA_BASES", nullable = false)
    Double sum_of_bases_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_SOMA_BASES", nullable = false)
    Double sum_of_bases_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_SOMA_BASES", nullable = false)
    Double sum_of_bases_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_SOMA_BASES", nullable = false)
    Double sum_of_bases_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_SOMA_BASES", nullable = false)
    Double sum_of_bases_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_SOMA_BASES", nullable = false)
    Double sum_of_bases_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_SOMA_BASES", nullable = false)
    Double sum_of_bases_hight_f;
    @Column(name = "MAIOR_TEOR_SOMA_BASES", nullable = false)
    Double sum_of_bases_too_hight;

    // =================================================================================
    // ACIDEZ TROCÁVEL (Al3+) - cmolc/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_ALUMINIO", nullable = false)
    Double aluminum_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_ALUMINIO", nullable = false)
    Double aluminum_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_ALUMINIO", nullable = false)
    Double aluminum_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_ALUMINIO", nullable = false)
    Double aluminum_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_ALUMINIO", nullable = false)
    Double aluminum_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_ALUMINIO", nullable = false)
    Double aluminum_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_ALUMINIO", nullable = false)
    Double aluminum_hight_f;
    @Column(name = "MAIOR_TEOR_ALUMINIO", nullable = false)
    Double aluminum_too_hight;

    // =================================================================================
    // ACIDEZ POTENCIAL (H+Al) - cmolc/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_ACIDEZ_POTENCIAL", nullable = false)
    Double potential_acidity_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_ACIDEZ_POTENCIAL", nullable = false)
    Double potential_acidity_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_ACIDEZ_POTENCIAL", nullable = false)
    Double potential_acidity_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_ACIDEZ_POTENCIAL", nullable = false)
    Double potential_acidity_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_ACIDEZ_POTENCIAL", nullable = false)
    Double potential_acidity_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_ACIDEZ_POTENCIAL", nullable = false)
    Double potential_acidity_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_ACIDEZ_POTENCIAL", nullable = false)
    Double potential_acidity_hight_f;
    @Column(name = "MAIOR_TEOR_ACIDEZ_POTENCIAL", nullable = false)
    Double potential_acidity_too_hight;

    // =================================================================================
    // CTC EFETIVA (t) - cmolc/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_CTC_EFETIVA", nullable = false)
    Double effective_cec_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_CTC_EFETIVA", nullable = false)
    Double effective_cec_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_CTC_EFETIVA", nullable = false)
    Double effective_cec_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_CTC_EFETIVA", nullable = false)
    Double effective_cec_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_CTC_EFETIVA", nullable = false)
    Double effective_cec_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_CTC_EFETIVA", nullable = false)
    Double effective_cec_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_CTC_EFETIVA", nullable = false)
    Double effective_cec_hight_f;
    @Column(name = "MAIOR_TEOR_CTC_EFETIVA", nullable = false)
    Double effective_cec_too_hight;

    // =================================================================================
    // CTC pH 7,0 (T) - cmolc/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_CTC_PH_7", nullable = false)
    Double ph7_cec_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_CTC_PH_7", nullable = false)
    Double ph7_cec_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_CTC_PH_7", nullable = false)
    Double ph7_cec_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_CTC_PH_7", nullable = false)
    Double ph7_cec_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_CTC_PH_7", nullable = false)
    Double ph7_cec_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_CTC_PH_7", nullable = false)
    Double ph7_cec_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_CTC_PH_7", nullable = false)
    Double ph7_cec_hight_f;
    @Column(name = "MAIOR_TEOR_CTC_PH_7", nullable = false)
    Double ph7_cec_too_hight;

    // =================================================================================
    // SATURAÇÃO POR Al3+ (m) - %
    // =================================================================================
    @Column(name = "MENOR_TEOR_SATURACAO_ALUMINIO", nullable = false)
    Double aluminum_saturation_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_SATURACAO_ALUMINIO", nullable = false)
    Double aluminum_saturation_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_SATURACAO_ALUMINIO", nullable = false)
    Double aluminum_saturation_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_SATURACAO_ALUMINIO", nullable = false)
    Double aluminum_saturation_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_SATURACAO_ALUMINIO", nullable = false)
    Double aluminum_saturation_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_SATURACAO_ALUMINIO", nullable = false)
    Double aluminum_saturation_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_SATURACAO_ALUMINIO", nullable = false)
    Double aluminum_saturation_hight_f;
    @Column(name = "MAIOR_TEOR_SATURACAO_ALUMINIO", nullable = false)
    Double aluminum_saturation_too_hight;

    // =================================================================================
    // SATURAÇÃO POR BASES (V) - %
    // =================================================================================
    @Column(name = "MENOR_TEOR_SATURACAO_BASES", nullable = false)
    Double base_saturation_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_SATURACAO_BASES", nullable = false)
    Double base_saturation_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_SATURACAO_BASES", nullable = false)
    Double base_saturation_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_SATURACAO_BASES", nullable = false)
    Double base_saturation_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_SATURACAO_BASES", nullable = false)
    Double base_saturation_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_SATURACAO_BASES", nullable = false)
    Double base_saturation_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_SATURACAO_BASES", nullable = false)
    Double base_saturation_hight_f;
    @Column(name = "MAIOR_TEOR_SATURACAO_BASES", nullable = false)
    Double base_saturation_too_hight;

    // =================================================================================
    // % SÓDIO TROCÁVEL NA T (PST) - %
    // =================================================================================
    @Column(name = "MENOR_TEOR_PST", nullable = false)
    Double sodium_saturation_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_PST", nullable = false)
    Double sodium_saturation_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_PST", nullable = false)
    Double sodium_saturation_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_PST", nullable = false)
    Double sodium_saturation_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_PST", nullable = false)
    Double sodium_saturation_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_PST", nullable = false)
    Double sodium_saturation_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_PST", nullable = false)
    Double sodium_saturation_hight_f;
    @Column(name = "MAIOR_TEOR_PST", nullable = false)
    Double sodium_saturation_too_hight;

    // =================================================================================
    // pH EM ÁGUA (1:2,5) - Adimensional
    // =================================================================================
    @Column(name = "MENOR_VALOR_PH", nullable = false)
    Double ph_too_low;
    @Column(name = "VALOR_INICIAL_BAIXO_PH", nullable = false)
    Double ph_low_i;
    @Column(name = "VALOR_FINAL_BAIXO_PH", nullable = false)
    Double ph_low_f;
    @Column(name = "VALOR_INICIAL_MEDIO_PH", nullable = false)
    Double ph_medium_i;
    @Column(name = "VALOR_FINAL_MEDIO_PH", nullable = false)
    Double ph_medium_f;
    @Column(name = "VALOR_INICIAL_ALTO_PH", nullable = false)
    Double ph_hight_i;
    @Column(name = "VALOR_FINAL_ALTO_PH", nullable = false)
    Double ph_hight_f;
    @Column(name = "MAIOR_VALOR_PH", nullable = false)
    Double ph_too_hight;

    // =================================================================================
    // pH EM CaCl2 0,01 mol/L (1:2,5) - Adimensional
    // =================================================================================
    @Column(name = "MENOR_VALOR_PH_CACL2", nullable = false)
    Double ph_cacl2_too_low;
    @Column(name = "VALOR_INICIAL_BAIXO_PH_CACL2", nullable = false)
    Double ph_cacl2_low_i;
    @Column(name = "VALOR_FINAL_BAIXO_PH_CACL2", nullable = false)
    Double ph_cacl2_low_f;
    @Column(name = "VALOR_INICIAL_MEDIO_PH_CACL2", nullable = false)
    Double ph_cacl2_medium_i;
    @Column(name = "VALOR_FINAL_MEDIO_PH_CACL2", nullable = false)
    Double ph_cacl2_medium_f;
    @Column(name = "VALOR_INICIAL_ALTO_PH_CACL2", nullable = false)
    Double ph_cacl2_hight_i;
    @Column(name = "VALOR_FINAL_ALTO_PH_CACL2", nullable = false)
    Double ph_cacl2_hight_f;
    @Column(name = "MAIOR_VALOR_PH_CACL2", nullable = false)
    Double ph_cacl2_too_hight;

    // =================================================================================
    // BORO DISPONÍVEL (B) - mg/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_BORO", nullable = false)
    Double boron_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_BORO", nullable = false)
    Double boron_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_BORO", nullable = false)
    Double boron_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_BORO", nullable = false)
    Double boron_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_BORO", nullable = false)
    Double boron_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_BORO", nullable = false)
    Double boron_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_BORO", nullable = false)
    Double boron_hight_f;
    @Column(name = "MAIOR_TEOR_BORO", nullable = false)
    Double boron_too_hight;

    // =================================================================================
    // COBRE DISPONÍVEL (Cu) - mg/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_COBRE", nullable = false)
    Double copper_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_COBRE", nullable = false)
    Double copper_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_COBRE", nullable = false)
    Double copper_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_COBRE", nullable = false)
    Double copper_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_COBRE", nullable = false)
    Double copper_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_COBRE", nullable = false)
    Double copper_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_COBRE", nullable = false)
    Double copper_hight_f;
    @Column(name = "MAIOR_TEOR_COBRE", nullable = false)
    Double copper_too_hight;

    // =================================================================================
    // FERRO DISPONÍVEL (Fe) - mg/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_FERRO", nullable = false)
    Double iron_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_FERRO", nullable = false)
    Double iron_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_FERRO", nullable = false)
    Double iron_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_FERRO", nullable = false)
    Double iron_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_FERRO", nullable = false)
    Double iron_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_FERRO", nullable = false)
    Double iron_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_FERRO", nullable = false)
    Double iron_hight_f;
    @Column(name = "MAIOR_TEOR_FERRO", nullable = false)
    Double iron_too_hight;

    // =================================================================================
    // MANGANÊS DISPONÍVEL (Mn) - mg/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_MANGANES", nullable = false)
    Double manganese_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_MANGANES", nullable = false)
    Double manganese_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_MANGANES", nullable = false)
    Double manganese_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_MANGANES", nullable = false)
    Double manganese_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_MANGANES", nullable = false)
    Double manganese_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_MANGANES", nullable = false)
    Double manganese_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_MANGANES", nullable = false)
    Double manganese_hight_f;
    @Column(name = "MAIOR_TEOR_MANGANES", nullable = false)
    Double manganese_too_hight;

    // =================================================================================
    // ZINCO DISPONÍVEL (Zn) - mg/dm³
    // =================================================================================
    @Column(name = "MENOR_TEOR_ZINCO", nullable = false)
    Double zinc_too_low;
    @Column(name = "TEOR_INICIAL_BAIXO_ZINCO", nullable = false)
    Double zinc_low_i;
    @Column(name = "TEOR_FINAL_BAIXO_ZINCO", nullable = false)
    Double zinc_low_f;
    @Column(name = "TEOR_INICIAL_MEDIO_ZINCO", nullable = false)
    Double zinc_medium_i;
    @Column(name = "TEOR_FINAL_MEDIO_ZINCO", nullable = false)
    Double zinc_medium_f;
    @Column(name = "TEOR_INICIAL_ALTO_ZINCO", nullable = false)
    Double zinc_hight_i;
    @Column(name = "TEOR_FINAL_ALTO_ZINCO", nullable = false)
    Double zinc_hight_f;
    @Column(name = "MAIOR_TEOR_ZINCO", nullable = false)
    Double zinc_too_hight;

    @PrePersist
    @PreUpdate
    private void normalizeUnits() {
        this.organic_carbon_unit = ORGANIC_CARBON_UNIT;
        this.organic_matter_unit = ORGANIC_MATTER_UNIT;
    }

    /**
     * Converte a entidade para DTO.
     */
    public DiverseContentRangeResponseDto toDto() {
        return DiverseContentRangeResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)

                // Organic Carbon
                .organic_carbon_unit(ORGANIC_CARBON_UNIT)
                .organic_carbon_too_low(this.organic_carbon_too_low)
                .organic_carbon_low_i(this.organic_carbon_low_i)
                .organic_carbon_low_f(this.organic_carbon_low_f)
                .organic_carbon_medium_i(this.organic_carbon_medium_i)
                .organic_carbon_medium_f(this.organic_carbon_medium_f)
                .organic_carbon_hight_i(this.organic_carbon_hight_i)
                .organic_carbon_hight_f(this.organic_carbon_hight_f)
                .organic_carbon_too_hight(this.organic_carbon_too_hight)

                // Organic Matter
                .organic_matter_unit(ORGANIC_MATTER_UNIT)
                .organic_matter_too_low(this.organic_matter_too_low)
                .organic_matter_low_i(this.organic_matter_low_i)
                .organic_matter_low_f(this.organic_matter_low_f)
                .organic_matter_medium_i(this.organic_matter_medium_i)
                .organic_matter_medium_f(this.organic_matter_medium_f)
                .organic_matter_hight_i(this.organic_matter_hight_i)
                .organic_matter_hight_f(this.organic_matter_hight_f)
                .organic_matter_too_hight(this.organic_matter_too_hight)

                // Calcium
                .calcium_too_low(this.calcium_too_low)
                .calcium_low_i(this.calcium_low_i)
                .calcium_low_f(this.calcium_low_f)
                .calcium_medium_i(this.calcium_medium_i)
                .calcium_medium_f(this.calcium_medium_f)
                .calcium_hight_i(this.calcium_hight_i)
                .calcium_hight_f(this.calcium_hight_f)
                .calcium_too_hight(this.calcium_too_hight)

                // Magnesium
                .magnesium_too_low(this.magnesium_too_low)
                .magnesium_low_i(this.magnesium_low_i)
                .magnesium_low_f(this.magnesium_low_f)
                .magnesium_medium_i(this.magnesium_medium_i)
                .magnesium_medium_f(this.magnesium_medium_f)
                .magnesium_hight_i(this.magnesium_hight_i)
                .magnesium_hight_f(this.magnesium_hight_f)
                .magnesium_too_hight(this.magnesium_too_hight)

                // Potassium
                .potassium_too_low(this.potassium_too_low)
                .potassium_low_i(this.potassium_low_i)
                .potassium_low_f(this.potassium_low_f)
                .potassium_medium_i(this.potassium_medium_i)
                .potassium_medium_f(this.potassium_medium_f)
                .potassium_hight_i(this.potassium_hight_i)
                .potassium_hight_f(this.potassium_hight_f)
                .potassium_too_hight(this.potassium_too_hight)

                // Sodium
                .sodium_too_low(this.sodium_too_low)
                .sodium_low_i(this.sodium_low_i)
                .sodium_low_f(this.sodium_low_f)
                .sodium_medium_i(this.sodium_medium_i)
                .sodium_medium_f(this.sodium_medium_f)
                .sodium_hight_i(this.sodium_hight_i)
                .sodium_hight_f(this.sodium_hight_f)
                .sodium_too_hight(this.sodium_too_hight)

                // Sum of Bases
                .sum_of_bases_too_low(this.sum_of_bases_too_low)
                .sum_of_bases_low_i(this.sum_of_bases_low_i)
                .sum_of_bases_low_f(this.sum_of_bases_low_f)
                .sum_of_bases_medium_i(this.sum_of_bases_medium_i)
                .sum_of_bases_medium_f(this.sum_of_bases_medium_f)
                .sum_of_bases_hight_i(this.sum_of_bases_hight_i)
                .sum_of_bases_hight_f(this.sum_of_bases_hight_f)
                .sum_of_bases_too_hight(this.sum_of_bases_too_hight)

                // Aluminum
                .aluminum_too_low(this.aluminum_too_low)
                .aluminum_low_i(this.aluminum_low_i)
                .aluminum_low_f(this.aluminum_low_f)
                .aluminum_medium_i(this.aluminum_medium_i)
                .aluminum_medium_f(this.aluminum_medium_f)
                .aluminum_hight_i(this.aluminum_hight_i)
                .aluminum_hight_f(this.aluminum_hight_f)
                .aluminum_too_hight(this.aluminum_too_hight)

                // Potential Acidity
                .potential_acidity_too_low(this.potential_acidity_too_low)
                .potential_acidity_low_i(this.potential_acidity_low_i)
                .potential_acidity_low_f(this.potential_acidity_low_f)
                .potential_acidity_medium_i(this.potential_acidity_medium_i)
                .potential_acidity_medium_f(this.potential_acidity_medium_f)
                .potential_acidity_hight_i(this.potential_acidity_hight_i)
                .potential_acidity_hight_f(this.potential_acidity_hight_f)
                .potential_acidity_too_hight(this.potential_acidity_too_hight)

                // Effective CEC
                .effective_cec_too_low(this.effective_cec_too_low)
                .effective_cec_low_i(this.effective_cec_low_i)
                .effective_cec_low_f(this.effective_cec_low_f)
                .effective_cec_medium_i(this.effective_cec_medium_i)
                .effective_cec_medium_f(this.effective_cec_medium_f)
                .effective_cec_hight_i(this.effective_cec_hight_i)
                .effective_cec_hight_f(this.effective_cec_hight_f)
                .effective_cec_too_hight(this.effective_cec_too_hight)

                // pH 7.0 CEC
                .ph7_cec_too_low(this.ph7_cec_too_low)
                .ph7_cec_low_i(this.ph7_cec_low_i)
                .ph7_cec_low_f(this.ph7_cec_low_f)
                .ph7_cec_medium_i(this.ph7_cec_medium_i)
                .ph7_cec_medium_f(this.ph7_cec_medium_f)
                .ph7_cec_hight_i(this.ph7_cec_hight_i)
                .ph7_cec_hight_f(this.ph7_cec_hight_f)
                .ph7_cec_too_hight(this.ph7_cec_too_hight)

                // Aluminum Saturation
                .aluminum_saturation_too_low(this.aluminum_saturation_too_low)
                .aluminum_saturation_low_i(this.aluminum_saturation_low_i)
                .aluminum_saturation_low_f(this.aluminum_saturation_low_f)
                .aluminum_saturation_medium_i(this.aluminum_saturation_medium_i)
                .aluminum_saturation_medium_f(this.aluminum_saturation_medium_f)
                .aluminum_saturation_hight_i(this.aluminum_saturation_hight_i)
                .aluminum_saturation_hight_f(this.aluminum_saturation_hight_f)
                .aluminum_saturation_too_hight(this.aluminum_saturation_too_hight)

                // Base Saturation
                .base_saturation_too_low(this.base_saturation_too_low)
                .base_saturation_low_i(this.base_saturation_low_i)
                .base_saturation_low_f(this.base_saturation_low_f)
                .base_saturation_medium_i(this.base_saturation_medium_i)
                .base_saturation_medium_f(this.base_saturation_medium_f)
                .base_saturation_hight_i(this.base_saturation_hight_i)
                .base_saturation_hight_f(this.base_saturation_hight_f)
                .base_saturation_too_hight(this.base_saturation_too_hight)

                // Sodium Saturation (PST)
                .sodium_saturation_too_low(this.sodium_saturation_too_low)
                .sodium_saturation_low_i(this.sodium_saturation_low_i)
                .sodium_saturation_low_f(this.sodium_saturation_low_f)
                .sodium_saturation_medium_i(this.sodium_saturation_medium_i)
                .sodium_saturation_medium_f(this.sodium_saturation_medium_f)
                .sodium_saturation_hight_i(this.sodium_saturation_hight_i)
                .sodium_saturation_hight_f(this.sodium_saturation_hight_f)
                .sodium_saturation_too_hight(this.sodium_saturation_too_hight)

                // pH in water
                .ph_too_low(this.ph_too_low)
                .ph_low_i(this.ph_low_i)
                .ph_low_f(this.ph_low_f)
                .ph_medium_i(this.ph_medium_i)
                .ph_medium_f(this.ph_medium_f)
                .ph_hight_i(this.ph_hight_i)
                .ph_hight_f(this.ph_hight_f)
                .ph_too_hight(this.ph_too_hight)

                // pH in CaCl2
                .ph_cacl2_too_low(this.ph_cacl2_too_low)
                .ph_cacl2_low_i(this.ph_cacl2_low_i)
                .ph_cacl2_low_f(this.ph_cacl2_low_f)
                .ph_cacl2_medium_i(this.ph_cacl2_medium_i)
                .ph_cacl2_medium_f(this.ph_cacl2_medium_f)
                .ph_cacl2_hight_i(this.ph_cacl2_hight_i)
                .ph_cacl2_hight_f(this.ph_cacl2_hight_f)
                .ph_cacl2_too_hight(this.ph_cacl2_too_hight)

                // Boron
                .boron_too_low(this.boron_too_low)
                .boron_low_i(this.boron_low_i)
                .boron_low_f(this.boron_low_f)
                .boron_medium_i(this.boron_medium_i)
                .boron_medium_f(this.boron_medium_f)
                .boron_hight_i(this.boron_hight_i)
                .boron_hight_f(this.boron_hight_f)
                .boron_too_hight(this.boron_too_hight)

                // Copper
                .copper_too_low(this.copper_too_low)
                .copper_low_i(this.copper_low_i)
                .copper_low_f(this.copper_low_f)
                .copper_medium_i(this.copper_medium_i)
                .copper_medium_f(this.copper_medium_f)
                .copper_hight_i(this.copper_hight_i)
                .copper_hight_f(this.copper_hight_f)
                .copper_too_hight(this.copper_too_hight)

                // Iron
                .iron_too_low(this.iron_too_low)
                .iron_low_i(this.iron_low_i)
                .iron_low_f(this.iron_low_f)
                .iron_medium_i(this.iron_medium_i)
                .iron_medium_f(this.iron_medium_f)
                .iron_hight_i(this.iron_hight_i)
                .iron_hight_f(this.iron_hight_f)
                .iron_too_hight(this.iron_too_hight)

                // Manganese
                .manganese_too_low(this.manganese_too_low)
                .manganese_low_i(this.manganese_low_i)
                .manganese_low_f(this.manganese_low_f)
                .manganese_medium_i(this.manganese_medium_i)
                .manganese_medium_f(this.manganese_medium_f)
                .manganese_hight_i(this.manganese_hight_i)
                .manganese_hight_f(this.manganese_hight_f)
                .manganese_too_hight(this.manganese_too_hight)

                // Zinc
                .zinc_too_low(this.zinc_too_low)
                .zinc_low_i(this.zinc_low_i)
                .zinc_low_f(this.zinc_low_f)
                .zinc_medium_i(this.zinc_medium_i)
                .zinc_medium_f(this.zinc_medium_f)
                .zinc_hight_i(this.zinc_hight_i)
                .zinc_hight_f(this.zinc_hight_f)
                .zinc_too_hight(this.zinc_too_hight)
                .build();
    }
}
