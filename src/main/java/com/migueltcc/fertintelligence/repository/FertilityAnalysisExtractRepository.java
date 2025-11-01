package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.ExtractAnalysisModels.FertilityAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.RangeExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FertilityAnalysisExtractRepository extends JpaRepository<FertilityAnalysisExtractModel, Long> {

    List<FertilityAnalysisExtractModel> findAllByRangeExtract(RangeExtractModel rangeExtract);

    List<FertilityAnalysisExtractModel> findAllByLayerExtract(LayerExtractModel layerExtract);
}