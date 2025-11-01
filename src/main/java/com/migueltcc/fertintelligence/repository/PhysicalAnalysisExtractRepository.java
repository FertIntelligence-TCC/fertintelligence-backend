package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.ExtractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.RangeExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhysicalAnalysisExtractRepository extends JpaRepository<PhysicalAnalysisExtractModel, Long> {

    List<PhysicalAnalysisExtractModel> findAllByRangeExtract(RangeExtractModel rangeExtract);

    List<PhysicalAnalysisExtractModel> findAllByLayerExtract(LayerExtractModel layerExtract);
}