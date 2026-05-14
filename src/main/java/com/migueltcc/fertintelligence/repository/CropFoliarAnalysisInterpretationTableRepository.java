package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropFoliarAnalysisInterpretationTableRepository
        extends JpaRepository<CropFoliarAnalysisInterpretationTableModel, Long> {

    List<CropFoliarAnalysisInterpretationTableModel> findAllByCreator(UserModel creator);
    List<CropFoliarAnalysisInterpretationTableModel> findAllByPublicTableTrue();
}

