package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LayerExtractRepository extends JpaRepository<LayerExtractModel, Long> {

    List<LayerExtractModel> findAllByAnalysis(SoilAnalysisModel analysis);

    Optional<LayerExtractModel> findByAnalysisAndLayerAndSubLayer(SoilAnalysisModel analysis, com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada layer, Integer subLayer);
}
