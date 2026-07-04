package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDoseResponseDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDoseSectionDto;
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
@Table(name = "TEORES_FOSFORO_ARGILA_DOSES_FOSFATO")
public class PhosphorusClayPhosphateDoseModel {

    public static final String DISPLAY_NAME = "Teores de Fósforo e Argila, e Doses de Fosfato";
    public static final String CLAY_CONTENT_UNIT = "g/kg";
    public static final String DOSE_UNIT = "kg/ha";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "SEQUEIRO_MENOR_TEOR_ARGILA", nullable = false)
    Double drylandLowerClayContent;
    @Column(name = "SEQUEIRO_MENOR_ARGILA_DOSE_P_MUITO_BAIXO", nullable = false)
    Double drylandLowerClayVeryLowPDose;
    @Column(name = "SEQUEIRO_MENOR_ARGILA_DOSE_P_BAIXO", nullable = false)
    Double drylandLowerClayLowPDose;
    @Column(name = "SEQUEIRO_MENOR_ARGILA_DOSE_P_MEDIO", nullable = false)
    Double drylandLowerClayMediumPDose;
    @Column(name = "SEQUEIRO_INTERVALO_1_MENOR_TEOR_ARGILA", nullable = false)
    Double drylandInterval1LowerClayContent;
    @Column(name = "SEQUEIRO_INTERVALO_1_MAIOR_TEOR_ARGILA", nullable = false)
    Double drylandInterval1HigherClayContent;
    @Column(name = "SEQUEIRO_INTERVALO_1_DOSE_P_MUITO_BAIXO", nullable = false)
    Double drylandInterval1VeryLowPDose;
    @Column(name = "SEQUEIRO_INTERVALO_1_DOSE_P_BAIXO", nullable = false)
    Double drylandInterval1LowPDose;
    @Column(name = "SEQUEIRO_INTERVALO_1_DOSE_P_MEDIO", nullable = false)
    Double drylandInterval1MediumPDose;
    @Column(name = "SEQUEIRO_INTERVALO_2_MENOR_TEOR_ARGILA", nullable = false)
    Double drylandInterval2LowerClayContent;
    @Column(name = "SEQUEIRO_INTERVALO_2_MAIOR_TEOR_ARGILA", nullable = false)
    Double drylandInterval2HigherClayContent;
    @Column(name = "SEQUEIRO_INTERVALO_2_DOSE_P_MUITO_BAIXO", nullable = false)
    Double drylandInterval2VeryLowPDose;
    @Column(name = "SEQUEIRO_INTERVALO_2_DOSE_P_BAIXO", nullable = false)
    Double drylandInterval2LowPDose;
    @Column(name = "SEQUEIRO_INTERVALO_2_DOSE_P_MEDIO", nullable = false)
    Double drylandInterval2MediumPDose;
    @Column(name = "SEQUEIRO_MAIOR_TEOR_ARGILA", nullable = false)
    Double drylandHigherClayContent;
    @Column(name = "SEQUEIRO_MAIOR_ARGILA_DOSE_P_MUITO_BAIXO", nullable = false)
    Double drylandHigherClayVeryLowPDose;
    @Column(name = "SEQUEIRO_MAIOR_ARGILA_DOSE_P_BAIXO", nullable = false)
    Double drylandHigherClayLowPDose;
    @Column(name = "SEQUEIRO_MAIOR_ARGILA_DOSE_P_MEDIO", nullable = false)
    Double drylandHigherClayMediumPDose;

    @Column(name = "IRRIGADO_MENOR_TEOR_ARGILA", nullable = false)
    Double irrigatedLowerClayContent;
    @Column(name = "IRRIGADO_MENOR_ARGILA_DOSE_P_MUITO_BAIXO", nullable = false)
    Double irrigatedLowerClayVeryLowPDose;
    @Column(name = "IRRIGADO_MENOR_ARGILA_DOSE_P_BAIXO", nullable = false)
    Double irrigatedLowerClayLowPDose;
    @Column(name = "IRRIGADO_MENOR_ARGILA_DOSE_P_MEDIO", nullable = false)
    Double irrigatedLowerClayMediumPDose;
    @Column(name = "IRRIGADO_INTERVALO_1_MENOR_TEOR_ARGILA", nullable = false)
    Double irrigatedInterval1LowerClayContent;
    @Column(name = "IRRIGADO_INTERVALO_1_MAIOR_TEOR_ARGILA", nullable = false)
    Double irrigatedInterval1HigherClayContent;
    @Column(name = "IRRIGADO_INTERVALO_1_DOSE_P_MUITO_BAIXO", nullable = false)
    Double irrigatedInterval1VeryLowPDose;
    @Column(name = "IRRIGADO_INTERVALO_1_DOSE_P_BAIXO", nullable = false)
    Double irrigatedInterval1LowPDose;
    @Column(name = "IRRIGADO_INTERVALO_1_DOSE_P_MEDIO", nullable = false)
    Double irrigatedInterval1MediumPDose;
    @Column(name = "IRRIGADO_INTERVALO_2_MENOR_TEOR_ARGILA", nullable = false)
    Double irrigatedInterval2LowerClayContent;
    @Column(name = "IRRIGADO_INTERVALO_2_MAIOR_TEOR_ARGILA", nullable = false)
    Double irrigatedInterval2HigherClayContent;
    @Column(name = "IRRIGADO_INTERVALO_2_DOSE_P_MUITO_BAIXO", nullable = false)
    Double irrigatedInterval2VeryLowPDose;
    @Column(name = "IRRIGADO_INTERVALO_2_DOSE_P_BAIXO", nullable = false)
    Double irrigatedInterval2LowPDose;
    @Column(name = "IRRIGADO_INTERVALO_2_DOSE_P_MEDIO", nullable = false)
    Double irrigatedInterval2MediumPDose;
    @Column(name = "IRRIGADO_MAIOR_TEOR_ARGILA", nullable = false)
    Double irrigatedHigherClayContent;
    @Column(name = "IRRIGADO_MAIOR_ARGILA_DOSE_P_MUITO_BAIXO", nullable = false)
    Double irrigatedHigherClayVeryLowPDose;
    @Column(name = "IRRIGADO_MAIOR_ARGILA_DOSE_P_BAIXO", nullable = false)
    Double irrigatedHigherClayLowPDose;
    @Column(name = "IRRIGADO_MAIOR_ARGILA_DOSE_P_MEDIO", nullable = false)
    Double irrigatedHigherClayMediumPDose;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;

    public PhosphorusClayPhosphateDoseResponseDto toDto() {
        return PhosphorusClayPhosphateDoseResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .displayName(DISPLAY_NAME)
                .clayContentUnit(CLAY_CONTENT_UNIT)
                .doseUnit(DOSE_UNIT)
                .drylandSection(toDrylandSectionDto())
                .irrigatedSection(toIrrigatedSectionDto())
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }

    private PhosphorusClayPhosphateDoseSectionDto toDrylandSectionDto() {
        return PhosphorusClayPhosphateDoseSectionDto.builder()
                .lowerClayContent(this.drylandLowerClayContent)
                .lowerClayVeryLowPDose(this.drylandLowerClayVeryLowPDose)
                .lowerClayLowPDose(this.drylandLowerClayLowPDose)
                .lowerClayMediumPDose(this.drylandLowerClayMediumPDose)
                .interval1LowerClayContent(this.drylandInterval1LowerClayContent)
                .interval1HigherClayContent(this.drylandInterval1HigherClayContent)
                .interval1VeryLowPDose(this.drylandInterval1VeryLowPDose)
                .interval1LowPDose(this.drylandInterval1LowPDose)
                .interval1MediumPDose(this.drylandInterval1MediumPDose)
                .interval2LowerClayContent(this.drylandInterval2LowerClayContent)
                .interval2HigherClayContent(this.drylandInterval2HigherClayContent)
                .interval2VeryLowPDose(this.drylandInterval2VeryLowPDose)
                .interval2LowPDose(this.drylandInterval2LowPDose)
                .interval2MediumPDose(this.drylandInterval2MediumPDose)
                .higherClayContent(this.drylandHigherClayContent)
                .higherClayVeryLowPDose(this.drylandHigherClayVeryLowPDose)
                .higherClayLowPDose(this.drylandHigherClayLowPDose)
                .higherClayMediumPDose(this.drylandHigherClayMediumPDose)
                .build();
    }

    private PhosphorusClayPhosphateDoseSectionDto toIrrigatedSectionDto() {
        return PhosphorusClayPhosphateDoseSectionDto.builder()
                .lowerClayContent(this.irrigatedLowerClayContent)
                .lowerClayVeryLowPDose(this.irrigatedLowerClayVeryLowPDose)
                .lowerClayLowPDose(this.irrigatedLowerClayLowPDose)
                .lowerClayMediumPDose(this.irrigatedLowerClayMediumPDose)
                .interval1LowerClayContent(this.irrigatedInterval1LowerClayContent)
                .interval1HigherClayContent(this.irrigatedInterval1HigherClayContent)
                .interval1VeryLowPDose(this.irrigatedInterval1VeryLowPDose)
                .interval1LowPDose(this.irrigatedInterval1LowPDose)
                .interval1MediumPDose(this.irrigatedInterval1MediumPDose)
                .interval2LowerClayContent(this.irrigatedInterval2LowerClayContent)
                .interval2HigherClayContent(this.irrigatedInterval2HigherClayContent)
                .interval2VeryLowPDose(this.irrigatedInterval2VeryLowPDose)
                .interval2LowPDose(this.irrigatedInterval2LowPDose)
                .interval2MediumPDose(this.irrigatedInterval2MediumPDose)
                .higherClayContent(this.irrigatedHigherClayContent)
                .higherClayVeryLowPDose(this.irrigatedHigherClayVeryLowPDose)
                .higherClayLowPDose(this.irrigatedHigherClayLowPDose)
                .higherClayMediumPDose(this.irrigatedHigherClayMediumPDose)
                .build();
    }
}
