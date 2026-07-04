package com.migueltcc.fertintelligence.model.fertintelligence;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "LINHAS_ADUBACAO_COBERTURA_RECOMENDACAO_DIRETA")
@EqualsAndHashCode(exclude = "directRecommendation")
public class DirectRecommendationCoverageFormulatedFertilizerLineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DIRECT_RECOMMENDATION", nullable = false)
    DirectRecommendationModel directRecommendation;

    @Column(name = "COVERAGE_ORDER")
    Integer coverageOrder;

    @Column(name = "PHASE", nullable = false)
    String phase;

    @Column(name = "FERTILIZER_ID")
    Long fertilizerId;

    @Column(name = "REQUIRED_N_KG_HA")
    Double requiredN;

    @Column(name = "REQUIRED_P2O5_KG_HA")
    Double requiredP2O5;

    @Column(name = "REQUIRED_K2O_KG_HA")
    Double requiredK2O;

    @Column(name = "RELATION_USED")
    String relationUsed;

    @Column(name = "SELECTION_TYPE")
    String selectionType;

    @Column(name = "DOSE_KG_HA")
    Double doseKgHa;

    @Column(name = "DOSE_UNIT_MODE")
    String doseUnitMode;

    @Column(name = "DOSE_UNIT_LABEL")
    String doseUnitLabel;

    @Column(name = "GRAMS_PER_LINEAR_METER")
    Double gramsPerLinearMeter;

    @Column(name = "GRAMS_PER_PIT")
    Double gramsPerPit;

    @Column(name = "TECHNICAL_OBSERVATION", length = 1000)
    String technicalObservation;
}
