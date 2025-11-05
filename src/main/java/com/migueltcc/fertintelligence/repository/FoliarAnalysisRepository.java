package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarAnalysisModels.FoliarAnalysisModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoliarAnalysisRepository extends JpaRepository<FoliarAnalysisModel, Long> {

    Optional<FoliarAnalysisModel> findByCropAndCollectDate(CropModel crop, Date collectDate);

    List<FoliarAnalysisModel> findAllByCrop(CropModel crop);
}
