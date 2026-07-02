package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationResponseDto;
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
@Table(name = "CRITERIOS_DE_INTERPRETACAO_DA_SALINIDADE_DO_SOLO")
public class SalinityInterpretationModel {

    public static final String DEFAULT_RAS_UNIT = "(mmolc)**0.5";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "UNIDADE_RAS", nullable = false)
    @Builder.Default
    String rasUnit = DEFAULT_RAS_UNIT;

    // --- SOLO NORMAL ---
    @Column(name = "MAIOR_CE_SOLO_NORMAL", nullable = false)
    Double normal_soil_highest_ce; // ds per m

    @Column(name = "MAIOR_PST_SOLO_NORMAL", nullable = false)
    Double normal_soil_highest_pst; // %

    @Column(name = "MAIOR_PH_SOLO_NORMAL", nullable = false)
    Double normal_soil_highest_ph;

    @Column(name = "MAIOR_RAS_SOLO_NORMAL", nullable = false)
    Double normal_soil_highest_ras; // mmolc per mmol**0.5

    // --- SOLO SALINO ---
    @Column(name = "MENOR_CE_SOLO_SALINO", nullable = false)
    Double saline_soil_lowest_ce; // ds per m

    @Column(name = "MAIOR_PST_SOLO_SALINO", nullable = false)
    Double saline_soil_highest_pst; // %

    @Column(name = "MAIOR_PH_SOLO_SALINO", nullable = false)
    Double saline_soil_highest_ph;

    @Column(name = "MAIOR_RAS_SOLO_SALINO", nullable = false)
    Double saline_soil_highest_ras; // mmolc per mmol**0.5

    // --- SOLO SALINO-SÓDICO ---
    @Column(name = "MAIOR_CE_SOLO_SALINO_SODICO", nullable = false)
    Double sodic_saline_soil_highest_ce; // ds per m

    @Column(name = "MENOR_PST_SOLO_SALINO_SODICO", nullable = false)
    Double sodic_saline_soil_lowest_pst; // %

    @Column(name = "MENOR_PH_SOLO_SALINO_SODICO", nullable = false)
    Double sodic_saline_soil_lowest_ph;

    @Column(name = "MENOR_RAS_SOLO_SALINO_SODICO", nullable = false)
    Double sodic_saline_soil_lowest_ras; // mmolc per mmol**0.5

    // --- SOLO SÓDICO ---
    @Column(name = "MAIOR_CE_SOLO_SODICO", nullable = false)
    Double sodic_soil_highest_ce; // ds per m

    @Column(name = "MENOR_PST_SOLO_SODICO", nullable = false)
    Double sodic_soil_lowest_pst; // %

    @Column(name = "MENOR_PH_SOLO_SODICO", nullable = false)
    Double sodic_soil_lowest_ph;

    @Column(name = "MENOR_RAS_SOLO_SODICO", nullable = false)
    Double sodic_soil_lowest_ras; // mmolc per mmol**0.5

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;

    @PrePersist
    @PreUpdate
    private void normalizeUnits() {
        this.rasUnit = DEFAULT_RAS_UNIT;
    }

    /**
     * Converte a entidade para DTO.
     * Assume a existência de SalinityInterpretationDto com Builder pattern.
     */
    public SalinityInterpretationResponseDto toDto() {
        return SalinityInterpretationResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .rasUnit(DEFAULT_RAS_UNIT)

                // Normal Soil
                .normal_soil_highest_ce(this.normal_soil_highest_ce)
                .normal_soil_highest_pst(this.normal_soil_highest_pst)
                .normal_soil_highest_ph(this.normal_soil_highest_ph)
                .normal_soil_highest_ras(this.normal_soil_highest_ras)

                // Saline Soil
                .saline_soil_lowest_ce(this.saline_soil_lowest_ce)
                .saline_soil_highest_pst(this.saline_soil_highest_pst)
                .saline_soil_highest_ph(this.saline_soil_highest_ph)
                .saline_soil_highest_ras(this.saline_soil_highest_ras)

                // Sodic-Saline Soil
                .sodic_saline_soil_highest_ce(this.sodic_saline_soil_highest_ce)
                .sodic_saline_soil_lowest_pst(this.sodic_saline_soil_lowest_pst)
                .sodic_saline_soil_lowest_ph(this.sodic_saline_soil_lowest_ph)
                .sodic_saline_soil_lowest_ras(this.sodic_saline_soil_lowest_ras)

                // Sodic Soil
                .sodic_soil_highest_ce(this.sodic_soil_highest_ce)
                .sodic_soil_lowest_pst(this.sodic_soil_lowest_pst)
                .sodic_soil_lowest_ph(this.sodic_soil_lowest_ph)
                .sodic_soil_lowest_ras(this.sodic_soil_lowest_ras)
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }
}
