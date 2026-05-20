package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<RecommendationModel, Long> {
    List<RecommendationModel> findAllByCreator(UserModel creator);
    List<RecommendationModel> findAllByProperty(PropertyModel property);
    List<RecommendationModel> findAllByPlot(PlotModel plot);
    List<RecommendationModel> findAllByCreatorOrderByCreatedAtDesc(UserModel creator);
}
