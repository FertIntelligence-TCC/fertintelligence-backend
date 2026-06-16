package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.FormulatedMineralFertilizerPhotoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormulatedMineralFertilizerPhotoRepository extends JpaRepository<FormulatedMineralFertilizerPhotoModel, Long> {

    List<FormulatedMineralFertilizerPhotoModel> findAllByFertilizerIdOrderByOrdemAsc(Long fertilizerId);

    void deleteAllByFertilizerId(Long fertilizerId);
}
