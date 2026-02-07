package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels.PhysicalAnalysisExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhysicalAnalysisExtractRepository extends JpaRepository<PhysicalAnalysisExtractModel, Long> {

    List<PhysicalAnalysisExtractModel> findAllByRangeExtract(RangeExtractModel rangeExtract);

    List<PhysicalAnalysisExtractModel> findAllByLayerExtract(LayerExtractModel layerExtract);
}