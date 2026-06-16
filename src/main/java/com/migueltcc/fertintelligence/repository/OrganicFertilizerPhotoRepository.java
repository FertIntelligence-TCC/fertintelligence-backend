package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizerPhotos.OrganicFertilizerPhotoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganicFertilizerPhotoRepository extends JpaRepository<OrganicFertilizerPhotoModel, Long> {

    List<OrganicFertilizerPhotoModel> findAllByFertilizerIdOrderByOrdemAsc(Long fertilizerId);

    void deleteAllByFertilizerId(Long fertilizerId);
}
