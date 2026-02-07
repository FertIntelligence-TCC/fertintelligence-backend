package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaturationExtractAnalysisExtractRepository extends JpaRepository<SaturationExtractAnalysisExtractModel, Long> {

    List<SaturationExtractAnalysisExtractModel> findAllByRangeExtract(RangeExtractModel rangeExtract);

    List<SaturationExtractAnalysisExtractModel> findAllByLayerExtract(LayerExtractModel layerExtract);

    List<SaturationExtractAnalysisExtractModel> findAllByPlot_Id(Long plotId);
}