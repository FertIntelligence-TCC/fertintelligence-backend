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
@Table(name = "ADUBACAO_CORRETIVA_DE_K2O")
public class CorrectiveK2OFertilizationModel {

    public static final String DISPLAY_NAME = "Adubação Corretiva de K2O";
    public static final String CTC_UNIT = "mmolc/dm³";
    public static final String EXCHANGEABLE_K_UNIT = "mmolc/dm³";
    public static final String DOSE_UNIT = "kg/ha";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "CTC_MINIMA")
    Double ctcMinimum;

    @Column(name = "CTC_MAXIMA")
    Double ctcMaximum;

    @Column(name = "K_MINIMO")
    Double exchangeableKMinimum;

    @Column(name = "K_MAXIMO")
    Double exchangeableKMaximum;

    @Column(name = "DOSE_K2O", nullable = false)
    Double recommendedK2ODose;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;
}
