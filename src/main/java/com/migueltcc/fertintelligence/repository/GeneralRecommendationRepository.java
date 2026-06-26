package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.GeneralRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralRecommendationRepository extends JpaRepository<GeneralRecommendationModel, Long> {
    Optional<GeneralRecommendationModel> findByRecommendation(RecommendationModel recommendation);
    Optional<GeneralRecommendationModel> findByRecommendationId(Long recommendationId);
    boolean existsByRecommendation(RecommendationModel recommendation);
}
