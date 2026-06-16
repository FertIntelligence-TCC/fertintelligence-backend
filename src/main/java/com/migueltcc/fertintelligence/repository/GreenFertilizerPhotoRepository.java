package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.GreenFertilizerPhotoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GreenFertilizerPhotoRepository extends JpaRepository<GreenFertilizerPhotoModel, Long> {

    List<GreenFertilizerPhotoModel> findAllByFertilizerIdOrderByOrdemAsc(Long fertilizerId);

    void deleteAllByFertilizerId(Long fertilizerId);
}
