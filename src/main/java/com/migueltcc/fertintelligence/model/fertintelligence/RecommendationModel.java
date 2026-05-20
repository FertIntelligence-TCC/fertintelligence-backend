package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "RECOMMENDATIONS")
@EqualsAndHashCode
public class RecommendationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CREATOR", nullable = false)
    UserModel creator;

    @ManyToOne
    @JoinColumn(name = "ID_PROPERTY", nullable = false)
    PropertyModel property;

    @ManyToOne
    @JoinColumn(name = "ID_PLOT", nullable = false)
    PlotModel plot;

    @Column(name = "RECOMMENDATION_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    RecommendationType recommendationType;

    @Column(name = "CROP_NAME", nullable = false)
    @Enumerated(EnumType.STRING)
    NomeComum cropName;

    @Column(name = "CROP_YEAR", nullable = false)
    Integer cropYear;

    @Column(name = "LIMING_CRITERIA")
    @Enumerated(EnumType.STRING)
    CriterioCalagem limingCriteria;

    @Column(name = "CROP_FERTILIZATION_TABLE_ID")
    Long cropFertilizationTableId;

    @Column(name = "SOIL_FERTILITY_INTERPRETATION_CRITERIA_TABLE_ID")
    Long soilFertilityInterpretationCriteriaTableId;

    @Column(name = "CROP_FOLIAR_ANALYSIS_INTERPRETATION_TABLE_ID")
    Long cropFoliarAnalysisInterpretationTableId;

    @Lob
    @Column(name = "TECHNICAL_REPORT", nullable = false)
    String technicalReport;

    @Column(name = "CREATED_AT", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
