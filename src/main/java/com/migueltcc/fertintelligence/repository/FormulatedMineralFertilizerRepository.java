package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormulatedMineralFertilizerRepository extends JpaRepository<FormulatedMineralFertilizerModel, Long> {

    List<FormulatedMineralFertilizerModel> findAllByUser(UserModel user);

}