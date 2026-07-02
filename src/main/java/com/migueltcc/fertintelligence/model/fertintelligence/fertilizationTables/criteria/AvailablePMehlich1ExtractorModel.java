package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorResponseDto;
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
@Table(name = "FOSFORO_DISPONIVEL_COM_EXTRATOR_MEHLICH_1")
public class AvailablePMehlich1ExtractorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    /**
     * Regras de negócio, para cada intervalo de teor de argila, os teores de fosforo (mg/dm³) tem relação:
     * - too_low = low_i - 0.1
     * - low_f = medium_i - 0.1;
     * - medium_f = hight_i - 0.1;
     * - hight_f = too_hight - 0.1;
     */

    // Teor de Argila (g/kg) < 150, Textura Arenoso:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_EM_SOLO_ARENOSO", nullable = false)
    Double p_content_sandy_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_EM_SOLO_ARENOSO", nullable = false)
    Double p_content_sandy_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_EM_SOLO_ARENOSO", nullable = false)
    Double p_content_sandy_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_EM_SOLO_ARENOSO", nullable = false)
    Double p_content_sandy_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_EM_SOLO_ARENOSO", nullable = false)
    Double p_content_sandy_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_EM_SOLO_ARENOSO", nullable = false)
    Double p_content_sandy_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_EM_SOLO_ARENOSO", nullable = false)
    Double p_content_sandy_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_EM_SOLO_ARENOSO", nullable = false)
    Double p_content_sandy_too_hight;

    // Teor de Argila (g/kg) entre 150 e 350, Textura Média (Arenosa/Argilosa):
    @Column(name = "MENOR_TEOR_DE_FOSFORO_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double p_content_sandy_clayey_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double p_content_sandy_clayey_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double p_content_sandy_clayey_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double p_content_sandy_clayey_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double p_content_sandy_clayey_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double p_content_sandy_clayey_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double p_content_sandy_clayey_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_EM_SOLO_ARENOSO_ARGILOSO", nullable = false)
    Double p_content_sandy_clayey_too_hight;

    // Teor de Argila (g/kg) entre 351 e 600, Textura Argilosa:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_EM_SOLO_ARGILOSO", nullable = false)
    Double p_content_clayey_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_EM_SOLO_ARGILOSO", nullable = false)
    Double p_content_clayey_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_EM_SOLO_ARGILOSO", nullable = false)
    Double p_content_clayey_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_EM_SOLO_ARGILOSO", nullable = false)
    Double p_content_clayey_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_EM_SOLO_ARGILOSO", nullable = false)
    Double p_content_clayey_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_EM_SOLO_ARGILOSO", nullable = false)
    Double p_content_clayey_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_EM_SOLO_ARGILOSO", nullable = false)
    Double p_content_clayey_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_EM_SOLO_ARGILOSO", nullable = false)
    Double p_content_clayey_too_hight;

    // Teor de Argila (g/kg) > 600, Textura Muito Argilosa:
    @Column(name = "MENOR_TEOR_DE_FOSFORO_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double p_content_very_clayey_too_low;

    @Column(name = "TEOR_INICIAL_BAIXO_DE_FOSFORO_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double p_content_very_clayey_low_i;

    @Column(name = "TEOR_FINAL_BAIXO_DE_FOSFORO_EM_SOLO_MUITO_ARGILSO", nullable = false)
    Double p_content_very_clayey_low_f;

    @Column(name = "TEOR_INICIAL_MEDIO_DE_FOSFORO_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double p_content_very_clayey_medium_i;

    @Column(name = "TEOR_FINAL_MEDIO_DE_FOSFORO_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double p_content_very_clayey_medium_f;

    @Column(name = "TEOR_INICIAL_ALTO_DE_FOSFORO_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double p_content_very_clayey_hight_i;

    @Column(name = "TEOR_FINAL_ALTO_DE_FOSFORO_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double p_content_very_clayey_hight_f;

    @Column(name = "MAIOR_TEOR_DE_FOSFORO_EM_SOLO_MUITO_ARGILOSO", nullable = false)
    Double p_content_very_clayey_too_hight;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;

    public AvailablePMehlich1ExtractorResponseDto toDto() {
        return AvailablePMehlich1ExtractorResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)

                // Sandy
                .p_content_sandy_too_low(this.p_content_sandy_too_low)
                .p_content_sandy_low_i(this.p_content_sandy_low_i)
                .p_content_sandy_low_f(this.p_content_sandy_low_f)
                .p_content_sandy_medium_i(this.p_content_sandy_medium_i)
                .p_content_sandy_medium_f(this.p_content_sandy_medium_f)
                .p_content_sandy_hight_i(this.p_content_sandy_hight_i)
                .p_content_sandy_hight_f(this.p_content_sandy_hight_f)
                .p_content_sandy_too_hight(this.p_content_sandy_too_hight)

                // Sandy-Clayey
                .p_content_sandy_clayey_too_low(this.p_content_sandy_clayey_too_low)
                .p_content_sandy_clayey_low_i(this.p_content_sandy_clayey_low_i)
                .p_content_sandy_clayey_low_f(this.p_content_sandy_clayey_low_f)
                .p_content_sandy_clayey_medium_i(this.p_content_sandy_clayey_medium_i)
                .p_content_sandy_clayey_medium_f(this.p_content_sandy_clayey_medium_f)
                .p_content_sandy_clayey_hight_i(this.p_content_sandy_clayey_hight_i)
                .p_content_sandy_clayey_hight_f(this.p_content_sandy_clayey_hight_f)
                .p_content_sandy_clayey_too_hight(this.p_content_sandy_clayey_too_hight)

                // Clayey
                .p_content_clayey_too_low(this.p_content_clayey_too_low)
                .p_content_clayey_low_i(this.p_content_clayey_low_i)
                .p_content_clayey_low_f(this.p_content_clayey_low_f)
                .p_content_clayey_medium_i(this.p_content_clayey_medium_i)
                .p_content_clayey_medium_f(this.p_content_clayey_medium_f)
                .p_content_clayey_hight_i(this.p_content_clayey_hight_i)
                .p_content_clayey_hight_f(this.p_content_clayey_hight_f)
                .p_content_clayey_too_hight(this.p_content_clayey_too_hight)

                // Very Clayey
                .p_content_very_clayey_too_low(this.p_content_very_clayey_too_low)
                .p_content_very_clayey_low_i(this.p_content_very_clayey_low_i)
                .p_content_very_clayey_low_f(this.p_content_very_clayey_low_f)
                .p_content_very_clayey_medium_i(this.p_content_very_clayey_medium_i)
                .p_content_very_clayey_medium_f(this.p_content_very_clayey_medium_f)
                .p_content_very_clayey_hight_i(this.p_content_very_clayey_hight_i)
                .p_content_very_clayey_hight_f(this.p_content_very_clayey_hight_f)
                .p_content_very_clayey_too_hight(this.p_content_very_clayey_too_hight)
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }

}
