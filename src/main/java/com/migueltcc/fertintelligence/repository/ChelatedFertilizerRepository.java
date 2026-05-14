package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.ChelatedFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChelatedFertilizerRepository extends JpaRepository<ChelatedFertilizerModel, Long> {

    List<ChelatedFertilizerModel> findAllByUser(UserModel user);

    List<ChelatedFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);
    List<ChelatedFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

}