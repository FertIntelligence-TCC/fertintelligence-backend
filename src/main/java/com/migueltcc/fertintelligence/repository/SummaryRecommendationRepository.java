package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SummaryRecommendationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SummaryRecommendationRepository extends JpaRepository<SummaryRecommendationModel, Long> {
    Optional<SummaryRecommendationModel> findByRecommendation(RecommendationModel recommendation);
    Optional<SummaryRecommendationModel> findByRecommendationId(Long recommendationId);
    boolean existsByRecommendation(RecommendationModel recommendation);
}
