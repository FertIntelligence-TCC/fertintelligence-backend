package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LayerExtractRepository extends JpaRepository<LayerExtractModel, Long> {

    List<LayerExtractModel> findAllByAnalysis(SoilAnalysisModel analysis);
}
