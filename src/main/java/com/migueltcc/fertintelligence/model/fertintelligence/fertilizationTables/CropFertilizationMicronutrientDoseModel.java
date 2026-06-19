package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Data
@Table(
        name = "DOSES_MICRONUTRIENTES_TABELAS_ADUBACAO_CULTURAS",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_DOSE_MICRONUTRIENTE_TABELA_ADUBACAO",
                columnNames = {"ID_TABELA_ADUBACAO_CULTURA", "MICRONUTRIENTE"}
        )
)
public class CropFertilizationMicronutrientDoseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_TABELA_ADUBACAO_CULTURA", nullable = false)
    private CropFertilizationTableModel table;

    @Enumerated(EnumType.STRING)
    @Column(name = "MICRONUTRIENTE", nullable = false, length = 10)
    private AppliedMicronutrient micronutrient;

    @Column(name = "DOSE_MINIMA", nullable = false)
    private Double minimumDose;

    @Column(name = "DOSE_MAXIMA", nullable = false)
    private Double maximumDose;
}
