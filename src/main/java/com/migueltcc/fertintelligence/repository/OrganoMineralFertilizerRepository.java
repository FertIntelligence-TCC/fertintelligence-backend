package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganoMineralFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganoMineralFertilizerRepository extends JpaRepository<OrganoMineralFertilizerModel, Long> {

    List<OrganoMineralFertilizerModel> findAllByUser(UserModel user);

    List<OrganoMineralFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);

}