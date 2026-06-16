package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.ChelatedFertilizerPhotoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChelatedFertilizerPhotoRepository extends JpaRepository<ChelatedFertilizerPhotoModel, Long> {

    List<ChelatedFertilizerPhotoModel> findAllByFertilizerIdOrderByOrdemAsc(Long fertilizerId);

    void deleteAllByFertilizerId(Long fertilizerId);
}
