package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationPlantingFormulatedFertilizerLineModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectRecommendationPlantingFormulatedFertilizerLineRepository
        extends JpaRepository<DirectRecommendationPlantingFormulatedFertilizerLineModel, Long> {

    List<DirectRecommendationPlantingFormulatedFertilizerLineModel> findAllByDirectRecommendationOrderByDoseKgHaDescIdAsc(
            DirectRecommendationModel directRecommendation);

    void deleteAllByDirectRecommendation(DirectRecommendationModel directRecommendation);
}
