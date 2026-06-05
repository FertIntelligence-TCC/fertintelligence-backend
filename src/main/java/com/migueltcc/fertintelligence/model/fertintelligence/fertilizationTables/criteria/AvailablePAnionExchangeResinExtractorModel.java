package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorResponseDto;
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
@Table(name = "FOSFORO_DISPONIVEL_COM_EXTRATOR_RESINA_TROCA_ANIONICA")
public class AvailablePAnionExchangeResinExtractorModel {

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
     * Regras de negócio, para cada cultura, os teores de fosforo (mg/dm3) no solo tem relação:
     * - too_low = low_i - 0.1
     * - low_f = medium_i - 0.1;
     * - medium_f = hight_i - 0.1;
     * - hight_f = too_hight - 0.1;
     */

    // ALGODAO:

    @Column(name = "MENOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_ALGODAO", nullable = false)
    Double p_content_cotton_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_ALGODAO", nullable = false)
    Double p_content_cotton_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_ALGODAO", nullable = false)
    Double p_content_cotton_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_ALGODAO", nullable = false)
    Double p_content_cotton_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_ALGODAO", nullable = false)
    Double p_content_cotton_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_ALGODAO", nullable = false)
    Double p_content_cotton_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_ALGODAO", nullable = false)
    Double p_content_cotton_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_ALGODAO", nullable = false)
    Double p_content_cotton_too_hight;

    // AMENDOIM:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_AMENDOIM", nullable = false)
    Double p_content_peanut_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_AMENDOIM", nullable = false)
    Double p_content_peanut_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_AMENDOIM", nullable = false)
    Double p_content_peanut_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_AMENDOIM", nullable = false)
    Double p_content_peanut_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_AMENDOIM", nullable = false)
    Double p_content_peanut_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_AMENDOIM", nullable = false)
    Double p_content_peanut_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_AMENDOIM", nullable = false)
    Double p_content_peanut_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_AMENDOIM", nullable = false)
    Double p_content_peanut_too_hight;

    // CANA_DE_ACUCAR:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_CANA_DE_ACUCAR", nullable = false)
    Double p_content_sugar_cane_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_CANA_DE_ACUCAR", nullable = false)
    Double p_content_sugar_cane_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_CANA_DE_ACUCAR", nullable = false)
    Double p_content_sugar_cane_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_CANA_DE_ACUCAR", nullable = false)
    Double p_content_sugar_cane_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_CANA_DE_ACUCAR", nullable = false)
    Double p_content_sugar_cane_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_CANA_DE_ACUCAR", nullable = false)
    Double p_content_sugar_cane_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_CANA_DE_ACUCAR", nullable = false)
    Double p_content_sugar_cane_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_CANA_DE_ACUCAR", nullable = false)
    Double p_content_sugar_cane_too_hight;

    // FEIJAO_CAUPI:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_CAUPI", nullable = false)
    Double p_content_cowpea_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_CAUPI", nullable = false)
    Double p_content_cowpea_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_CAUPI", nullable = false)
    Double p_content_cowpea_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_CAUPI", nullable = false)
    Double p_content_cowpea_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_CAUPI", nullable = false)
    Double p_content_cowpea_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_CAUPI", nullable = false)
    Double p_content_cowpea_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_CAUPI", nullable = false)
    Double p_content_cowpea_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_CAUPI", nullable = false)
    Double p_content_cowpea_too_hight;

    // FEIJAO_COMUM:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_COMUM", nullable = false)
    Double p_content_common_bean_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_COMUM", nullable = false)
    Double p_content_common_bean_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_COMUM", nullable = false)
    Double p_content_common_bean_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_COMUM", nullable = false)
    Double p_content_common_bean_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_COMUM", nullable = false)
    Double p_content_common_bean_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_COMUM", nullable = false)
    Double p_content_common_bean_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_COMUM", nullable = false)
    Double p_content_common_bean_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_FEIJAO_COMUM", nullable = false)
    Double p_content_common_bean_too_hight;

    // GERGELIM:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_GERGELIM", nullable = false)
    Double p_content_sesame_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_GERGELIM", nullable = false)
    Double p_content_sesame_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_GERGELIM", nullable = false)
    Double p_content_sesame_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_GERGELIM", nullable = false)
    Double p_content_sesame_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_GERGELIM", nullable = false)
    Double p_content_sesame_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_GERGELIM", nullable = false)
    Double p_content_sesame_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_GERGELIM", nullable = false)
    Double p_content_sesame_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_GERGELIM", nullable = false)
    Double p_content_sesame_too_hight;

    // MAMONA:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_MAMONA", nullable = false)
    Double p_content_castor_bean_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_MAMONA", nullable = false)
    Double p_content_castor_bean_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_MAMONA", nullable = false)
    Double p_content_castor_bean_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_MAMONA", nullable = false)
    Double p_content_castor_bean_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_MAMONA", nullable = false)
    Double p_content_castor_bean_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_MAMONA", nullable = false)
    Double p_content_castor_bean_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_MAMONA", nullable = false)
    Double p_content_castor_bean_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_MAMONA", nullable = false)
    Double p_content_castor_bean_too_hight;

    // MILHO:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_MILHO", nullable = false)
    Double p_content_corn_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_MILHO", nullable = false)
    Double p_content_corn_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_MILHO", nullable = false)
    Double p_content_corn_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_MILHO", nullable = false)
    Double p_content_corn_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_MILHO", nullable = false)
    Double p_content_corn_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_MILHO", nullable = false)
    Double p_content_corn_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_MILHO", nullable = false)
    Double p_content_corn_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_MILHO", nullable = false)
    Double p_content_corn_too_hight;

    // SISAL:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_SISAL", nullable = false)
    Double p_content_sisal_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_SISAL", nullable = false)
    Double p_content_sisal_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_SISAL", nullable = false)
    Double p_content_sisal_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_SISAL", nullable = false)
    Double p_content_sisal_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_SISAL", nullable = false)
    Double p_content_sisal_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_SISAL", nullable = false)
    Double p_content_sisal_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_SISAL", nullable = false)
    Double p_content_sisal_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_SISAL", nullable = false)
    Double p_content_sisal_too_hight;

    // SOJA:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_SOJA", nullable = false)
    Double p_content_soybean_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_SOJA", nullable = false)
    Double p_content_soybean_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_NO_SOLO_PARA_SOJA", nullable = false)
    Double p_content_soybean_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_SOJA", nullable = false)
    Double p_content_soybean_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_NO_SOLO_PARA_SOJA", nullable = false)
    Double p_content_soybean_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_SOJA", nullable = false)
    Double p_content_soybean_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_NO_SOLO_PARA_SOJA", nullable = false)
    Double p_content_soybean_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_NO_SOLO_PARA_SOJA", nullable = false)
    Double p_content_soybean_too_hight;

    public AvailablePAnionExchangeResinExtractorResponseDto toDto() {
        return AvailablePAnionExchangeResinExtractorResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .observations(this.observations)
                .sources(this.sources)

                // Cotton
                .p_content_cotton_too_low(this.p_content_cotton_too_low)
                .p_content_cotton_low_i(this.p_content_cotton_low_i)
                .p_content_cotton_low_f(this.p_content_cotton_low_f)
                .p_content_cotton_medium_i(this.p_content_cotton_medium_i)
                .p_content_cotton_medium_f(this.p_content_cotton_medium_f)
                .p_content_cotton_hight_i(this.p_content_cotton_hight_i)
                .p_content_cotton_hight_f(this.p_content_cotton_hight_f)
                .p_content_cotton_too_hight(this.p_content_cotton_too_hight)

                // Peanut
                .p_content_peanut_too_low(this.p_content_peanut_too_low)
                .p_content_peanut_low_i(this.p_content_peanut_low_i)
                .p_content_peanut_low_f(this.p_content_peanut_low_f)
                .p_content_peanut_medium_i(this.p_content_peanut_medium_i)
                .p_content_peanut_medium_f(this.p_content_peanut_medium_f)
                .p_content_peanut_hight_i(this.p_content_peanut_hight_i)
                .p_content_peanut_hight_f(this.p_content_peanut_hight_f)
                .p_content_peanut_too_hight(this.p_content_peanut_too_hight)

                // Sugar Cane
                .p_content_sugar_cane_too_low(this.p_content_sugar_cane_too_low)
                .p_content_sugar_cane_low_i(this.p_content_sugar_cane_low_i)
                .p_content_sugar_cane_low_f(this.p_content_sugar_cane_low_f)
                .p_content_sugar_cane_medium_i(this.p_content_sugar_cane_medium_i)
                .p_content_sugar_cane_medium_f(this.p_content_sugar_cane_medium_f)
                .p_content_sugar_cane_hight_i(this.p_content_sugar_cane_hight_i)
                .p_content_sugar_cane_hight_f(this.p_content_sugar_cane_hight_f)
                .p_content_sugar_cane_too_hight(this.p_content_sugar_cane_too_hight)

                // Cowpea
                .p_content_cowpea_too_low(this.p_content_cowpea_too_low)
                .p_content_cowpea_low_i(this.p_content_cowpea_low_i)
                .p_content_cowpea_low_f(this.p_content_cowpea_low_f)
                .p_content_cowpea_medium_i(this.p_content_cowpea_medium_i)
                .p_content_cowpea_medium_f(this.p_content_cowpea_medium_f)
                .p_content_cowpea_hight_i(this.p_content_cowpea_hight_i)
                .p_content_cowpea_hight_f(this.p_content_cowpea_hight_f)
                .p_content_cowpea_too_hight(this.p_content_cowpea_too_hight)

                // Common Bean
                .p_content_common_bean_too_low(this.p_content_common_bean_too_low)
                .p_content_common_bean_low_i(this.p_content_common_bean_low_i)
                .p_content_common_bean_low_f(this.p_content_common_bean_low_f)
                .p_content_common_bean_medium_i(this.p_content_common_bean_medium_i)
                .p_content_common_bean_medium_f(this.p_content_common_bean_medium_f)
                .p_content_common_bean_hight_i(this.p_content_common_bean_hight_i)
                .p_content_common_bean_hight_f(this.p_content_common_bean_hight_f)
                .p_content_common_bean_too_hight(this.p_content_common_bean_too_hight)

                // Sesame
                .p_content_sesame_too_low(this.p_content_sesame_too_low)
                .p_content_sesame_low_i(this.p_content_sesame_low_i)
                .p_content_sesame_low_f(this.p_content_sesame_low_f)
                .p_content_sesame_medium_i(this.p_content_sesame_medium_i)
                .p_content_sesame_medium_f(this.p_content_sesame_medium_f)
                .p_content_sesame_hight_i(this.p_content_sesame_hight_i)
                .p_content_sesame_hight_f(this.p_content_sesame_hight_f)
                .p_content_sesame_too_hight(this.p_content_sesame_too_hight)

                // Castor Bean
                .p_content_castor_bean_too_low(this.p_content_castor_bean_too_low)
                .p_content_castor_bean_low_i(this.p_content_castor_bean_low_i)
                .p_content_castor_bean_low_f(this.p_content_castor_bean_low_f)
                .p_content_castor_bean_medium_i(this.p_content_castor_bean_medium_i)
                .p_content_castor_bean_medium_f(this.p_content_castor_bean_medium_f)
                .p_content_castor_bean_hight_i(this.p_content_castor_bean_hight_i)
                .p_content_castor_bean_hight_f(this.p_content_castor_bean_hight_f)
                .p_content_castor_bean_too_hight(this.p_content_castor_bean_too_hight)

                // Corn
                .p_content_corn_too_low(this.p_content_corn_too_low)
                .p_content_corn_low_i(this.p_content_corn_low_i)
                .p_content_corn_low_f(this.p_content_corn_low_f)
                .p_content_corn_medium_i(this.p_content_corn_medium_i)
                .p_content_corn_medium_f(this.p_content_corn_medium_f)
                .p_content_corn_hight_i(this.p_content_corn_hight_i)
                .p_content_corn_hight_f(this.p_content_corn_hight_f)
                .p_content_corn_too_hight(this.p_content_corn_too_hight)

                // Sisal
                .p_content_sisal_too_low(this.p_content_sisal_too_low)
                .p_content_sisal_low_i(this.p_content_sisal_low_i)
                .p_content_sisal_low_f(this.p_content_sisal_low_f)
                .p_content_sisal_medium_i(this.p_content_sisal_medium_i)
                .p_content_sisal_medium_f(this.p_content_sisal_medium_f)
                .p_content_sisal_hight_i(this.p_content_sisal_hight_i)
                .p_content_sisal_hight_f(this.p_content_sisal_hight_f)
                .p_content_sisal_too_hight(this.p_content_sisal_too_hight)

                // Soybean
                .p_content_soybean_too_low(this.p_content_soybean_too_low)
                .p_content_soybean_low_i(this.p_content_soybean_low_i)
                .p_content_soybean_low_f(this.p_content_soybean_low_f)
                .p_content_soybean_medium_i(this.p_content_soybean_medium_i)
                .p_content_soybean_medium_f(this.p_content_soybean_medium_f)
                .p_content_soybean_hight_i(this.p_content_soybean_hight_i)
                .p_content_soybean_hight_f(this.p_content_soybean_hight_f)
                .p_content_soybean_too_hight(this.p_content_soybean_too_hight)
                .build();
    }

}
