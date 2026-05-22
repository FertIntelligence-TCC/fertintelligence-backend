package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RangeExtractRepository extends JpaRepository<RangeExtractModel, Long> {

    List<RangeExtractModel> findAllByAnalysis(SoilAnalysisModel analysis);

    Optional<RangeExtractModel> findByAnalysisAndProfundidadeInicialAndProfundidadeFinal(SoilAnalysisModel analysis, Integer profundidade_inicial, Integer profundidade_final);
}