package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDoseResponseDto;
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
@Table(name = "DOSES_DE_ENXOFRE")
public class SulfurDoseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "MUITO_BAIXO_DOSE_ARGILA_MENOR_400", nullable = false)
    Double less400VeryLowDose;
    @Column(name = "BAIXO_DOSE_ARGILA_MENOR_400", nullable = false)
    Double less400LowDose;
    @Column(name = "MEDIO_DOSE_ARGILA_MENOR_400", nullable = false)
    Double less400MediumDose;
    @Column(name = "ALTO_DOSE_ARGILA_MENOR_400", nullable = false)
    Double less400HighDose;
    @Column(name = "MUITO_ALTO_DOSE_ARGILA_MENOR_400", nullable = false)
    Double less400VeryHighDose;

    @Column(name = "MUITO_BAIXO_DOSE_ARGILA_MAIOR_400", nullable = false)
    Double greater400VeryLowDose;
    @Column(name = "BAIXO_DOSE_ARGILA_MAIOR_400", nullable = false)
    Double greater400LowDose;
    @Column(name = "MEDIO_DOSE_ARGILA_MAIOR_400", nullable = false)
    Double greater400MediumDose;
    @Column(name = "ALTO_DOSE_ARGILA_MAIOR_400", nullable = false)
    Double greater400HighDose;
    @Column(name = "MUITO_ALTO_DOSE_ARGILA_MAIOR_400", nullable = false)
    Double greater400VeryHighDose;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;

    public SulfurDoseResponseDto toDto() {
        return SulfurDoseResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .less400VeryLowDose(this.less400VeryLowDose)
                .less400LowDose(this.less400LowDose)
                .less400MediumDose(this.less400MediumDose)
                .less400HighDose(this.less400HighDose)
                .less400VeryHighDose(this.less400VeryHighDose)
                .greater400VeryLowDose(this.greater400VeryLowDose)
                .greater400LowDose(this.greater400LowDose)
                .greater400MediumDose(this.greater400MediumDose)
                .greater400HighDose(this.greater400HighDose)
                .greater400VeryHighDose(this.greater400VeryHighDose)
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }
}
