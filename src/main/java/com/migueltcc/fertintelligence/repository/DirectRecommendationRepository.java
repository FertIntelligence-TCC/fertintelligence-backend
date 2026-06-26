package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectRecommendationRepository extends JpaRepository<DirectRecommendationModel, Long> {
    Optional<DirectRecommendationModel> findByRecommendation(RecommendationModel recommendation);
    Optional<DirectRecommendationModel> findByRecommendationId(Long recommendationId);
    boolean existsByRecommendation(RecommendationModel recommendation);
}
