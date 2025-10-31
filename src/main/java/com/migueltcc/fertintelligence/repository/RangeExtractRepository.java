package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.RangeExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RangeExtractRepository extends JpaRepository<RangeExtractModel, Long> {

    List<RangeExtractModel> findAllByAnalysis(SoilAnalysisModel analysis);
}