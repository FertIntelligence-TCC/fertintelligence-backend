package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.TopdressingFertilizationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopDressingFertilizationRepository extends JpaRepository<TopdressingFertilizationModel, Long> {

    Optional<TopdressingFertilizationModel> findByCropAndOrder(CropModel crop, Integer order);

    List<TopdressingFertilizationModel> findAllByCrop(CropModel crop);
}