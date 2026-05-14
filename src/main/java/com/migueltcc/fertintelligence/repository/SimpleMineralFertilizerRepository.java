package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimpleMineralFertilizerRepository extends JpaRepository<SimpleMineralFertilizerModel, Long> {

    List<SimpleMineralFertilizerModel> findAllByUser(UserModel user);

    List<SimpleMineralFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);

    List<SimpleMineralFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

}