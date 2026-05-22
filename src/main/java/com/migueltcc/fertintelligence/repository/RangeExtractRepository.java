package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RangeExtractRepository extends JpaRepository<RangeExtractModel, Long> {

    List<RangeExtractModel> findAllByAnalysis(SoilAnalysisModel analysis);

    @Query("""
        SELECT r FROM RangeExtractModel r
        WHERE r.analysis = :analysis
          AND r.profundidade_inicial = :profundidadeInicial
          AND r.profundidade_final = :profundidadeFinal
    """)
    Optional<RangeExtractModel> findByAnalysisAndProfundidadeInicialAndProfundidadeFinal(
            @Param("analysis") SoilAnalysisModel analysis,
            @Param("profundidadeInicial") Integer profundidadeInicial,
            @Param("profundidadeFinal") Integer profundidadeFinal
    );
}