package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose.MicronutrientDoseResponseDto;
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
@Table(name = "DOSES_DE_MICRONUTRIENTES")
public class MicronutrientDoseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "BORO_BAIXO_DOSE")
    Double boronLowDose;
    @Column(name = "BORO_MEDIO_DOSE")
    Double boronMediumDose;
    @Column(name = "BORO_ALTO_DOSE")
    Double boronHighDose;

    @Column(name = "COBRE_BAIXO_DOSE")
    Double copperLowDose;
    @Column(name = "COBRE_MEDIO_DOSE")
    Double copperMediumDose;
    @Column(name = "COBRE_ALTO_DOSE")
    Double copperHighDose;

    @Column(name = "FERRO_BAIXO_DOSE")
    Double ironLowDose;
    @Column(name = "FERRO_MEDIO_DOSE")
    Double ironMediumDose;
    @Column(name = "FERRO_ALTO_DOSE")
    Double ironHighDose;

    @Column(name = "MANGANES_BAIXO_DOSE")
    Double manganeseLowDose;
    @Column(name = "MANGANES_MEDIO_DOSE")
    Double manganeseMediumDose;
    @Column(name = "MANGANES_ALTO_DOSE")
    Double manganeseHighDose;

    @Column(name = "ZINCO_BAIXO_DOSE")
    Double zincLowDose;
    @Column(name = "ZINCO_MEDIO_DOSE")
    Double zincMediumDose;
    @Column(name = "ZINCO_ALTO_DOSE")
    Double zincHighDose;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;

    public MicronutrientDoseResponseDto toDto() {
        return MicronutrientDoseResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .boronLowDose(this.boronLowDose)
                .boronMediumDose(this.boronMediumDose)
                .boronHighDose(this.boronHighDose)
                .copperLowDose(this.copperLowDose)
                .copperMediumDose(this.copperMediumDose)
                .copperHighDose(this.copperHighDose)
                .ironLowDose(this.ironLowDose)
                .ironMediumDose(this.ironMediumDose)
                .ironHighDose(this.ironHighDose)
                .manganeseLowDose(this.manganeseLowDose)
                .manganeseMediumDose(this.manganeseMediumDose)
                .manganeseHighDose(this.manganeseHighDose)
                .zincLowDose(this.zincLowDose)
                .zincMediumDose(this.zincMediumDose)
                .zincHighDose(this.zincHighDose)
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }
}
