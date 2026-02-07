package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnualCropFolderRepository extends JpaRepository<AnnualCropFolderModel, Long> {

    Optional<AnnualCropFolderModel> findByPlotAndCropsYear(PlotModel plot, Integer cropsYear);

    List<AnnualCropFolderModel> findAllByPlot(PlotModel plot);

    List<AnnualCropFolderModel> findAllByPlotId(Long plotId);
}