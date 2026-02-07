package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoilAnalysisRepository extends JpaRepository<SoilAnalysisModel, Long> {

    Optional<SoilAnalysisModel> findByPlotAndAnalysisYear(PlotModel plot, Integer analysisYear);

    List<SoilAnalysisModel> findAllByPlot(PlotModel plot);

    List<SoilAnalysisModel> findAllByPlotId(Long plotId);
}