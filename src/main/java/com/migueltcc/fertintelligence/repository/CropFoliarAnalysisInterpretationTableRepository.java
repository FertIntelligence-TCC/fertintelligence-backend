package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropFoliarAnalysisInterpretationTableRepository
        extends JpaRepository<CropFoliarAnalysisInterpretationTableModel, Long> {

    List<CropFoliarAnalysisInterpretationTableModel> findAllByCreator(UserModel creator);
    List<CropFoliarAnalysisInterpretationTableModel> findAllByCreator_Cargo(Cargo cargo);
    Optional<CropFoliarAnalysisInterpretationTableModel> findByCreatorAndName(UserModel creator, String name);
    List<CropFoliarAnalysisInterpretationTableModel> findAllByPublicTableTrue();
    Optional<CropFoliarAnalysisInterpretationTableModel> findByIdAndCreator(Long id, UserModel creator);
    Optional<CropFoliarAnalysisInterpretationTableModel> findByIdAndPublicTableTrue(Long id);
    Optional<CropFoliarAnalysisInterpretationTableModel> findByIdAndCreator_Cargo(Long id, Cargo cargo);
}
