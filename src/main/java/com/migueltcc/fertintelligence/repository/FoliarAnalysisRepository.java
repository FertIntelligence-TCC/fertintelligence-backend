package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.FoliarAnalysisModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoliarAnalysisRepository extends JpaRepository<FoliarAnalysisModel, Long> {

    Optional<FoliarAnalysisModel> findByCropAndCollectDate(CropModel crop, Date collectDate);

    Optional<FoliarAnalysisModel> findTopByCropOrderByIdDesc(CropModel crop);

    List<FoliarAnalysisModel> findAllByCrop(CropModel crop);

    @Modifying
    @Transactional
    void deleteAllByCropId(Long cropId);

}
