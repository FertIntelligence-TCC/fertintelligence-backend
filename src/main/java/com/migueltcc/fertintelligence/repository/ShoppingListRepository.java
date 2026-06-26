package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ShoppingListModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingListModel, Long> {
    Optional<ShoppingListModel> findByRecommendation(RecommendationModel recommendation);
    Optional<ShoppingListModel> findByRecommendationId(Long recommendationId);
    boolean existsByRecommendation(RecommendationModel recommendation);
}
