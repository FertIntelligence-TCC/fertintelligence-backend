package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MineralFertilizerRepository extends JpaRepository<MineralFertilizerModel, Long> {

    List<MineralFertilizerModel> findAllByUser(UserModel user);

    List<MineralFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);
    List<MineralFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

}