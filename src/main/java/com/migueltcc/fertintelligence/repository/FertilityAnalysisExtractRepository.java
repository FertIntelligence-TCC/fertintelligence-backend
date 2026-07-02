package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FertilityAnalysisExtractRepository extends JpaRepository<FertilityAnalysisExtractModel, Long> {

    List<FertilityAnalysisExtractModel> findAllByRangeExtract(RangeExtractModel rangeExtract);

    List<FertilityAnalysisExtractModel> findAllByLayerExtract(LayerExtractModel layerExtract);

    List<FertilityAnalysisExtractModel> findAllByRangeExtractAnalysis(SoilAnalysisModel analysis);

    List<FertilityAnalysisExtractModel> findAllByLayerExtractAnalysis(SoilAnalysisModel analysis);

    boolean existsByRangeExtract(RangeExtractModel rangeExtract);

    boolean existsByLayerExtract(LayerExtractModel layerExtract);
}
