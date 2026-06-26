package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends JpaRepository<RecommendationModel, Long> {
    List<RecommendationModel> findAllByCreator(UserModel creator);
    List<RecommendationModel> findAllByProperty(PropertyModel property);
    List<RecommendationModel> findAllByPlot(PlotModel plot);
    List<RecommendationModel> findAllByCreatorOrderByCreatedAtDesc(UserModel creator);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RecommendationModel r where r.id = :id")
    Optional<RecommendationModel> findByIdForUpdate(@Param("id") Long id);
}
