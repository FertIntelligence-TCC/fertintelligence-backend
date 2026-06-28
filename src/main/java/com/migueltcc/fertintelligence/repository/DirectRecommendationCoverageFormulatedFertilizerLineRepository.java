package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationCoverageFormulatedFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectRecommendationCoverageFormulatedFertilizerLineRepository
        extends JpaRepository<DirectRecommendationCoverageFormulatedFertilizerLineModel, Long> {

    List<DirectRecommendationCoverageFormulatedFertilizerLineModel>
    findAllByDirectRecommendationOrderByCoverageOrderAscDoseKgHaDescIdAsc(DirectRecommendationModel directRecommendation);

    void deleteAllByDirectRecommendation(DirectRecommendationModel directRecommendation);
}
