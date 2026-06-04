package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.SaturationExtractAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaturationExtractAnalysisExtractRepository extends JpaRepository<SaturationExtractAnalysisExtractModel, Long> {

    List<SaturationExtractAnalysisExtractModel> findAllByRangeExtract(RangeExtractModel rangeExtract);

    List<SaturationExtractAnalysisExtractModel> findAllByLayerExtract(LayerExtractModel layerExtract);

    Optional<SaturationExtractAnalysisExtractModel> findTopByRangeExtractAnalysisPlotOrderByIdDesc(PlotModel plot);

    Optional<SaturationExtractAnalysisExtractModel> findTopByLayerExtractAnalysisPlotOrderByIdDesc(PlotModel plot);

    List<SaturationExtractAnalysisExtractModel> findAllByRangeExtractAnalysisPlot(PlotModel plot);

    List<SaturationExtractAnalysisExtractModel> findAllByLayerExtractAnalysisPlot(PlotModel plot);

    boolean existsByRangeExtract(RangeExtractModel rangeExtract);

    boolean existsByLayerExtract(LayerExtractModel layerExtract);
}
