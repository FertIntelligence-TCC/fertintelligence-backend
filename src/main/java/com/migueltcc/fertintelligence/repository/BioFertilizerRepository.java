package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.BioFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BioFertilizerRepository extends JpaRepository<BioFertilizerModel, Long> {

    List<BioFertilizerModel> findAllByUser(UserModel user);

    List<BioFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);


    List<BioFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

}