package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.GreenFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GreenFertilizerRepository extends JpaRepository<GreenFertilizerModel, Long> {

    List<GreenFertilizerModel> findAllByUser(UserModel user);

    List<GreenFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);


    List<GreenFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

}