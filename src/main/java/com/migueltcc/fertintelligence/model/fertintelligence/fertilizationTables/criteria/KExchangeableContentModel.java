package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel.KExchangeableContentResponseDto;
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
@Table(name = "TEORES_TROCAVEIS_DE_POTASSIO")
public class KExchangeableContentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "OBSERVACOES")
    String observations;

    @Column(name = "FONTES")
    String sources;

    /**
     * Regras de negócio:
     * A interpretação dos teores de potássio (mg/dm3) varia de acordo com a CTC (Capacidade de Troca Catiônica) a pH 7.0.
     * As faixas são: < 20, 20-40, 41-80, 81-120 e > 120 mmolc/dm³.
     */

    // --- CTC < 20 mmolc/dm³ ---
    @Column(name = "MENOR_TEOR_K_CTC_MENOR_20", nullable = false)
    Double k_content_cec_less_20_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_K_CTC_MENOR_20", nullable = false)
    Double k_content_cec_less_20_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_K_CTC_MENOR_20", nullable = false)
    Double k_content_cec_less_20_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_K_CTC_MENOR_20", nullable = false)
    Double k_content_cec_less_20_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_K_CTC_MENOR_20", nullable = false)
    Double k_content_cec_less_20_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_K_CTC_MENOR_20", nullable = false)
    Double k_content_cec_less_20_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_K_CTC_MENOR_20", nullable = false)
    Double k_content_cec_less_20_hight_f;

    @Column(name = "MAIOR_TEOR_K_CTC_MENOR_20", nullable = false)
    Double k_content_cec_less_20_too_hight;

    // --- CTC 20 a 40 mmolc/dm³ ---
    @Column(name = "MENOR_TEOR_K_CTC_20_40", nullable = false)
    Double k_content_cec_20_40_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_K_CTC_20_40", nullable = false)
    Double k_content_cec_20_40_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_K_CTC_20_40", nullable = false)
    Double k_content_cec_20_40_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_K_CTC_20_40", nullable = false)
    Double k_content_cec_20_40_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_K_CTC_20_40", nullable = false)
    Double k_content_cec_20_40_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_K_CTC_20_40", nullable = false)
    Double k_content_cec_20_40_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_K_CTC_20_40", nullable = false)
    Double k_content_cec_20_40_hight_f;

    @Column(name = "MAIOR_TEOR_K_CTC_20_40", nullable = false)
    Double k_content_cec_20_40_too_hight;

    // --- CTC 41 a 80 mmolc/dm³ ---
    @Column(name = "MENOR_TEOR_K_CTC_41_80", nullable = false)
    Double k_content_cec_41_80_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_K_CTC_41_80", nullable = false)
    Double k_content_cec_41_80_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_K_CTC_41_80", nullable = false)
    Double k_content_cec_41_80_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_K_CTC_41_80", nullable = false)
    Double k_content_cec_41_80_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_K_CTC_41_80", nullable = false)
    Double k_content_cec_41_80_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_K_CTC_41_80", nullable = false)
    Double k_content_cec_41_80_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_K_CTC_41_80", nullable = false)
    Double k_content_cec_41_80_hight_f;

    @Column(name = "MAIOR_TEOR_K_CTC_41_80", nullable = false)
    Double k_content_cec_41_80_too_hight;

    // --- CTC 81 a 120 mmolc/dm³ ---
    @Column(name = "MENOR_TEOR_K_CTC_81_120", nullable = false)
    Double k_content_cec_81_120_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_K_CTC_81_120", nullable = false)
    Double k_content_cec_81_120_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_K_CTC_81_120", nullable = false)
    Double k_content_cec_81_120_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_K_CTC_81_120", nullable = false)
    Double k_content_cec_81_120_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_K_CTC_81_120", nullable = false)
    Double k_content_cec_81_120_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_K_CTC_81_120", nullable = false)
    Double k_content_cec_81_120_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_K_CTC_81_120", nullable = false)
    Double k_content_cec_81_120_hight_f;

    @Column(name = "MAIOR_TEOR_K_CTC_81_120", nullable = false)
    Double k_content_cec_81_120_too_hight;

    // --- CTC > 120 mmolc/dm³ ---
    @Column(name = "MENOR_TEOR_K_CTC_MAIOR_120", nullable = false)
    Double k_content_cec_greater_120_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_K_CTC_MAIOR_120", nullable = false)
    Double k_content_cec_greater_120_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_K_CTC_MAIOR_120", nullable = false)
    Double k_content_cec_greater_120_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_K_CTC_MAIOR_120", nullable = false)
    Double k_content_cec_greater_120_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_K_CTC_MAIOR_120", nullable = false)
    Double k_content_cec_greater_120_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_K_CTC_MAIOR_120", nullable = false)
    Double k_content_cec_greater_120_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_K_CTC_MAIOR_120", nullable = false)
    Double k_content_cec_greater_120_hight_f;

    @Column(name = "MAIOR_TEOR_K_CTC_MAIOR_120", nullable = false)
    Double k_content_cec_greater_120_too_hight;

    public KExchangeableContentResponseDto toDto() {
        return KExchangeableContentResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .observations(this.observations)
                .sources(this.sources)

                // CEC < 20
                .k_content_cec_less_20_too_low(this.k_content_cec_less_20_too_low)
                .k_content_cec_less_20_low_i(this.k_content_cec_less_20_low_i)
                .k_content_cec_less_20_low_f(this.k_content_cec_less_20_low_f)
                .k_content_cec_less_20_medium_i(this.k_content_cec_less_20_medium_i)
                .k_content_cec_less_20_medium_f(this.k_content_cec_less_20_medium_f)
                .k_content_cec_less_20_hight_i(this.k_content_cec_less_20_hight_i)
                .k_content_cec_less_20_hight_f(this.k_content_cec_less_20_hight_f)
                .k_content_cec_less_20_too_hight(this.k_content_cec_less_20_too_hight)

                // CEC 20-40
                .k_content_cec_20_40_too_low(this.k_content_cec_20_40_too_low)
                .k_content_cec_20_40_low_i(this.k_content_cec_20_40_low_i)
                .k_content_cec_20_40_low_f(this.k_content_cec_20_40_low_f)
                .k_content_cec_20_40_medium_i(this.k_content_cec_20_40_medium_i)
                .k_content_cec_20_40_medium_f(this.k_content_cec_20_40_medium_f)
                .k_content_cec_20_40_hight_i(this.k_content_cec_20_40_hight_i)
                .k_content_cec_20_40_hight_f(this.k_content_cec_20_40_hight_f)
                .k_content_cec_20_40_too_hight(this.k_content_cec_20_40_too_hight)

                // CEC 41-80
                .k_content_cec_41_80_too_low(this.k_content_cec_41_80_too_low)
                .k_content_cec_41_80_low_i(this.k_content_cec_41_80_low_i)
                .k_content_cec_41_80_low_f(this.k_content_cec_41_80_low_f)
                .k_content_cec_41_80_medium_i(this.k_content_cec_41_80_medium_i)
                .k_content_cec_41_80_medium_f(this.k_content_cec_41_80_medium_f)
                .k_content_cec_41_80_hight_i(this.k_content_cec_41_80_hight_i)
                .k_content_cec_41_80_hight_f(this.k_content_cec_41_80_hight_f)
                .k_content_cec_41_80_too_hight(this.k_content_cec_41_80_too_hight)

                // CEC 81-120
                .k_content_cec_81_120_too_low(this.k_content_cec_81_120_too_low)
                .k_content_cec_81_120_low_i(this.k_content_cec_81_120_low_i)
                .k_content_cec_81_120_low_f(this.k_content_cec_81_120_low_f)
                .k_content_cec_81_120_medium_i(this.k_content_cec_81_120_medium_i)
                .k_content_cec_81_120_medium_f(this.k_content_cec_81_120_medium_f)
                .k_content_cec_81_120_hight_i(this.k_content_cec_81_120_hight_i)
                .k_content_cec_81_120_hight_f(this.k_content_cec_81_120_hight_f)
                .k_content_cec_81_120_too_hight(this.k_content_cec_81_120_too_hight)

                // CEC > 120
                .k_content_cec_greater_120_too_low(this.k_content_cec_greater_120_too_low)
                .k_content_cec_greater_120_low_i(this.k_content_cec_greater_120_low_i)
                .k_content_cec_greater_120_low_f(this.k_content_cec_greater_120_low_f)
                .k_content_cec_greater_120_medium_i(this.k_content_cec_greater_120_medium_i)
                .k_content_cec_greater_120_medium_f(this.k_content_cec_greater_120_medium_f)
                .k_content_cec_greater_120_hight_i(this.k_content_cec_greater_120_hight_i)
                .k_content_cec_greater_120_hight_f(this.k_content_cec_greater_120_hight_f)
                .k_content_cec_greater_120_too_hight(this.k_content_cec_greater_120_too_hight)
                .build();
    }
}
