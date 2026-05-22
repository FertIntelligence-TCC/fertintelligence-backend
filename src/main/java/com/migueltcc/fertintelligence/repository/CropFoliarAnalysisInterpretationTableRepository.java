package com.migueltcc.fertintelligence.repository;

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
    Optional<CropFoliarAnalysisInterpretationTableModel> findByCreatorAndName(UserModel creator, String name);
    List<CropFoliarAnalysisInterpretationTableModel> findAllByPublicTableTrue();
}

