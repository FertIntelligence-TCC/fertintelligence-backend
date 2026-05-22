package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LayerExtractRepository extends JpaRepository<LayerExtractModel, Long> {

    List<LayerExtractModel> findAllByAnalysis(SoilAnalysisModel analysis);

    @Query("""
            SELECT layerExtract
            FROM LayerExtractModel layerExtract
            WHERE layerExtract.analysis = :analysis
              AND layerExtract.layer = :layer
              AND layerExtract.sub_layer = :subLayer
            """)
    Optional<LayerExtractModel> findByAnalysisAndLayerAndSubLayer(
            @Param("analysis") SoilAnalysisModel analysis,
            @Param("layer") Camada layer,
            @Param("subLayer") Integer subLayer
    );
}