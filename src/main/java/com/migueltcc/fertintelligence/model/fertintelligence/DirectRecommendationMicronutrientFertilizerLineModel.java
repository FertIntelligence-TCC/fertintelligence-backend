package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "LINHAS_MICRONUTRIENTES_RECOMENDACAO_DIRETA")
@EqualsAndHashCode(exclude = "directRecommendation")
public class DirectRecommendationMicronutrientFertilizerLineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DIRECT_RECOMMENDATION", nullable = false)
    DirectRecommendationModel directRecommendation;

    @Column(name = "MICRONUTRIENT", nullable = false)
    @Enumerated(EnumType.STRING)
    AppliedMicronutrient micronutrient;

    @Column(name = "MICRONUTRIENT_DOSE_KG_HA")
    Double micronutrientDoseKgHa;

    @Column(name = "FERTILIZER_ID")
    Long fertilizerId;

    @Column(name = "FERTILIZER_NAME")
    String fertilizerName;

    @Column(name = "MICRONUTRIENT_CONCENTRATION_PERCENT")
    Double micronutrientConcentrationPercent;

    @Column(name = "FERTILIZER_DOSE_KG_HA")
    Double fertilizerDoseKgHa;

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
