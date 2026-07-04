package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

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
@Table(name = "ADUBACAO_CORRETIVA_DE_P2O5")
public class CorrectiveP2O5FertilizationModel {

    public static final String DISPLAY_NAME = "Adubação Corretiva de P2O5";
    public static final String CLAY_CONTENT_UNIT = "g/kg";
    public static final String AVAILABLE_P_MEHLICH_1_UNIT = "mg/dm³";
    public static final String DOSE_UNIT = "kg/ha";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "ARGILA_MINIMA")
    Double clayContentMinimum;

    @Column(name = "ARGILA_MAXIMA")
    Double clayContentMaximum;

    @Column(name = "P_MEHLICH_MINIMO")
    Double availablePMehlich1Minimum;

    @Column(name = "P_MEHLICH_MAXIMO")
    Double availablePMehlich1Maximum;

    @Column(name = "DOSE_P2O5", nullable = false)
    Double recommendedP2O5Dose;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;
}
