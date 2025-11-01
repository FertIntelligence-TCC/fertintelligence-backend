package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.ExtractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.RangeExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaturationExtractAnalysisExtractRepository extends JpaRepository<SaturationExtractAnalysisExtractModel, Long> {

    List<SaturationExtractAnalysisExtractModel> findAllByRangeExtract(RangeExtractModel rangeExtract);

    List<SaturationExtractAnalysisExtractModel> findAllByLayerExtract(LayerExtractModel layerExtract);
}