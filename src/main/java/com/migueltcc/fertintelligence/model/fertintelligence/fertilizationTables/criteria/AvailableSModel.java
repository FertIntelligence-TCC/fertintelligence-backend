package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorResponseDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSResponseDto;
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
@Table(name = "ENXOFRE_DISPONIVEL_NO_SOLO")
public class AvailableSModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    /**
     * Regras de negócio, para cada intervalo de teor de argila, os teores de enxofre (mg/dm3) tem relação:
     * - too_low = low_i - 0.1
     * - low_f = medium_i - 0.1;
     * - medium_f = hight_i - 0.1;
     * - hight_f = too_hight - 0.1;
     */

    // Teor de Argila (dag/kg) < 15, Textura Arenoso:
    @Column(name = "MENOR_TEOR_DE_ENXOFRE_EM_SOLO_ARENOSO", nullable = false)
    Double s_content_sandy_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_ENXOFRE_EM_SOLO_ARENOSO", nullable = false)
    Double s_content_sandy_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_ENXOFRE_EM_SOLO_ARENOSO", nullable = false)
    Double s_content_sandy_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_ENXOFRE_EM_SOLO_ARENOSO", nullable = false)
    Double s_content_sandy_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_ENXOFRE_EM_SOLO_ARENOSO", nullable = false)
    Double s_content_sandy_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_ENXOFRE_EM_SOLO_ARENOSO", nullable = false)
    Double s_content_sandy_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_ENXOFRE_EM_SOLO_ARENOSO", nullable = false)
    Double s_content_sandy_hight_f;

    @Column(name = "MAIOR_TEOR_DE_ENXOFRE_EM_SOLO_ARENOSO", nullable = false)
    Double s_content_sandy_too_hight;

    // Teor de Argila (dag/kg) entre 15 e 35, Textura Média (Arenosa/Argilosa):
    @Column(name = "MENOR_TEOR_DE_ENXOFRE_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double s_content_sandy_clayey_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_ENXOFRE_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double s_content_sandy_clayey_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_ENXOFRE_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double s_content_sandy_clayey_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_ENXOFRE_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double s_content_sandy_clayey_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_ENXOFRE_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double s_content_sandy_clayey_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_ENXOFRE_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double s_content_sandy_clayey_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_ENXOFRE_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double s_content_sandy_clayey_hight_f;

    @Column(name = "MAIOR_TEOR_DE_ENXOFRE_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double s_content_sandy_clayey_too_hight;

    // Teor de Argila (dag/kg) entre 35.1 e 60, Textura Argilosa:
    @Column(name = "MENOR_TEOR_DE_ENXOFRE_EM_SOLO_ARGILOSO", nullable = false)
    Double s_content_clayey_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_ENXOFRE_EM_SOLO_ARGILOSO", nullable = false)
    Double s_content_clayey_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_ENXOFRE_EM_SOLO_ARGILOSO", nullable = false)
    Double s_content_clayey_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_ENXOFRE_EM_SOLO_ARGILOSO", nullable = false)
    Double s_content_clayey_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_ENXOFRE_EM_SOLO_ARGILOSO", nullable = false)
    Double s_content_clayey_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_ENXOFRE_EM_SOLO_ARGILOSO", nullable = false)
    Double s_content_clayey_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_ENXOFRE_EM_SOLO_ARGILOSO", nullable = false)
    Double s_content_clayey_hight_f;

    @Column(name = "MAIOR_TEOR_DE_ENXOFRE_EM_SOLO_ARGILOSO", nullable = false)
    Double s_content_clayey_too_hight;

    // Teor de Argila (dag/kg) > 60, Textura Muito Argilosa:
    @Column(name = "MENOR_TEOR_DE_ENXOFRE_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double s_content_very_clayey_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_ENXOFRE_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double s_content_very_clayey_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_ENXOFRE_EM_SOLO_MUITO_ARGILSO", nullable = false)
    Double s_content_very_clayey_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_ENXOFRE_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double s_content_very_clayey_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_ENXOFRE_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double s_content_very_clayey_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_ENXOFRE_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double s_content_very_clayey_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_ENXOFRE_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double s_content_very_clayey_hight_f;

    @Column(name = "MAIOR_TEOR_DE_ENXOFRE_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double s_content_very_clayey_too_hight;

    public AvailableSResponseDto toDto() {
        return AvailableSResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)

                // Sandy
                .s_content_sandy_too_low(this.s_content_sandy_too_low)
                .s_content_sandy_low_i(this.s_content_sandy_low_i)
                .s_content_sandy_low_f(this.s_content_sandy_low_f)
                .s_content_sandy_medium_i(this.s_content_sandy_medium_i)
                .s_content_sandy_medium_f(this.s_content_sandy_medium_f)
                .s_content_sandy_hight_i(this.s_content_sandy_hight_i)
                .s_content_sandy_hight_f(this.s_content_sandy_hight_f)
                .s_content_sandy_too_hight(this.s_content_sandy_too_hight)

                // Sandy-Clayey
                .s_content_sandy_clayey_too_low(this.s_content_sandy_clayey_too_low)
                .s_content_sandy_clayey_low_i(this.s_content_sandy_clayey_low_i)
                .s_content_sandy_clayey_low_f(this.s_content_sandy_clayey_low_f)
                .s_content_sandy_clayey_medium_i(this.s_content_sandy_clayey_medium_i)
                .s_content_sandy_clayey_medium_f(this.s_content_sandy_clayey_medium_f)
                .s_content_sandy_clayey_hight_i(this.s_content_sandy_clayey_hight_i)
                .s_content_sandy_clayey_hight_f(this.s_content_sandy_clayey_hight_f)
                .s_content_sandy_clayey_too_hight(this.s_content_sandy_clayey_too_hight)

                // Clayey
                .s_content_clayey_too_low(this.s_content_clayey_too_low)
                .s_content_clayey_low_i(this.s_content_clayey_low_i)
                .s_content_clayey_low_f(this.s_content_clayey_low_f)
                .s_content_clayey_medium_i(this.s_content_clayey_medium_i)
                .s_content_clayey_medium_f(this.s_content_clayey_medium_f)
                .s_content_clayey_hight_i(this.s_content_clayey_hight_i)
                .s_content_clayey_hight_f(this.s_content_clayey_hight_f)
                .s_content_clayey_too_hight(this.s_content_clayey_too_hight)

                // Very Clayey
                .s_content_very_clayey_too_low(this.s_content_very_clayey_too_low)
                .s_content_very_clayey_low_i(this.s_content_very_clayey_low_i)
                .s_content_very_clayey_low_f(this.s_content_very_clayey_low_f)
                .s_content_very_clayey_medium_i(this.s_content_very_clayey_medium_i)
                .s_content_very_clayey_medium_f(this.s_content_very_clayey_medium_f)
                .s_content_very_clayey_hight_i(this.s_content_very_clayey_hight_i)
                .s_content_very_clayey_hight_f(this.s_content_very_clayey_hight_f)
                .s_content_very_clayey_too_hight(this.s_content_very_clayey_too_hight)
                .build();
    }

}
