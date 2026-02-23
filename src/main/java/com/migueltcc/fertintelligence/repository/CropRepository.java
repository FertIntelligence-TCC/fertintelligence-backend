package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<CropModel, Long> {

    Optional<CropModel> findByNameAndVarietyAndFolder(NomeComum name, String variety, AnnualCropFolderModel folder);

    List<CropModel> findAllByFolder(AnnualCropFolderModel folder);

    List<CropModel> findAllByFolderId(Long folderId);
}