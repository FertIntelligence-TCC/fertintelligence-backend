package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.FertilizerSourceOption;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.RecommendationType;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "RECOMMENDATIONS")
@EqualsAndHashCode(exclude = "generalRecommendation")
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

    @Column(name = "RECOMMENDATION_FOLDER_NAME")
    String recommendationFolderName;

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

    @Column(name = "FERTILIZER_SOURCE_OPTION")
    FertilizerSourceOption origemAdubos;

    @Column(name = "CROP_FERTILIZATION_TABLE_ID")
    Long cropFertilizationTableId;

    @Column(name = "CROP_FERTILIZATION_TABLE_GROUP")
    @Enumerated(EnumType.STRING)
    TechnicalTableGroup cropFertilizationTableGroup;

    @Column(name = "SOIL_FERTILITY_INTERPRETATION_CRITERIA_TABLE_ID")
    Long soilFertilityInterpretationCriteriaTableId;

    @Column(name = "SOIL_FERTILITY_INTERPRETATION_CRITERIA_TABLE_GROUP")
    @Enumerated(EnumType.STRING)
    TechnicalTableGroup soilFertilityInterpretationCriteriaTableGroup;

    @Column(name = "CROP_FOLIAR_ANALYSIS_INTERPRETATION_TABLE_ID")
    Long cropFoliarAnalysisInterpretationTableId;

    @Column(name = "CROP_FOLIAR_ANALYSIS_INTERPRETATION_TABLE_GROUP")
    @Enumerated(EnumType.STRING)
    TechnicalTableGroup cropFoliarAnalysisInterpretationTableGroup;

    @Lob
    @Column(name = "TECHNICAL_REPORT", nullable = false)
    String technicalReport;

    @OneToOne(mappedBy = "recommendation", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    GeneralRecommendationModel generalRecommendation;

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
