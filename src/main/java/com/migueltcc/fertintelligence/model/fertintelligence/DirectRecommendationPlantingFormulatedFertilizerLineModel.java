package com.migueltcc.fertintelligence.model.fertintelligence;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "DIRECT_RECOMMENDATION_PLANTING_FORMULATED_FERTILIZER_LINES")
@EqualsAndHashCode(exclude = "directRecommendation")
public class DirectRecommendationPlantingFormulatedFertilizerLineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DIRECT_RECOMMENDATION", nullable = false)
    DirectRecommendationModel directRecommendation;

    @Column(name = "PHASE", nullable = false)
    String phase;

    @Column(name = "FERTILIZER_ID")
    Long fertilizerId;

    @Column(name = "FERTILIZER_NAME")
    String fertilizerName;

    @Column(name = "N_PERCENT")
    Double nitrogenPercent;

    @Column(name = "P2O5_PERCENT")
    Double p2o5Percent;

    @Column(name = "K2O_PERCENT")
    Double k2oPercent;

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
