package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.BioFertilizerPhotoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BioFertilizerPhotoRepository extends JpaRepository<BioFertilizerPhotoModel, Long> {

    List<BioFertilizerPhotoModel> findAllByFertilizerIdOrderByOrdemAsc(Long fertilizerId);

    void deleteAllByFertilizerId(Long fertilizerId);
}
