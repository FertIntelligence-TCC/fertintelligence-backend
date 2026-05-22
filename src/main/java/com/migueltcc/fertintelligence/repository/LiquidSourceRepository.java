package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels.LiquidSourceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LiquidSourceRepository extends JpaRepository<LiquidSourceModel, Long> {

    List<LiquidSourceModel> findAllByCrop(CropModel crop);

    boolean existsByCropAndSource(CropModel crop, String source);

    @Modifying
    @Transactional
    void deleteAllByCropId(Long cropId);

}