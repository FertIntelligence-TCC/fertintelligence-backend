package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropDeficiencyToxicityModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.DeficiencyToxicityNutrient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CropDeficiencyToxicityRepository extends JpaRepository<CropDeficiencyToxicityModel, Long> {

    List<CropDeficiencyToxicityModel> findAllByCrop(CropModel crop);

    List<CropDeficiencyToxicityModel> findAllByCropId(Long cropId);
    List<CropDeficiencyToxicityModel> findAllByObservationsStartingWith(String observationsPrefix);

    boolean existsByCropAndNutrientAndObservations(CropModel crop, DeficiencyToxicityNutrient nutrient, String observations);

    @Modifying
    @Transactional
    void deleteAllByCropId(Long cropId);
}
