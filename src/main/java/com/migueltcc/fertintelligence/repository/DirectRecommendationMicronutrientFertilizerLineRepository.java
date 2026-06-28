package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationMicronutrientFertilizerLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectRecommendationMicronutrientFertilizerLineRepository
        extends JpaRepository<DirectRecommendationMicronutrientFertilizerLineModel, Long> {

    List<DirectRecommendationMicronutrientFertilizerLineModel> findAllByDirectRecommendationOrderByIdAsc(
            DirectRecommendationModel directRecommendation);

    void deleteAllByDirectRecommendation(DirectRecommendationModel directRecommendation);
}
