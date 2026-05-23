package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableLineModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFoliarAnalysisInterpretationTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropFoliarAnalysisInterpretationTableLineRepository
        extends JpaRepository<CropFoliarAnalysisInterpretationTableLineModel, Long> {

    List<CropFoliarAnalysisInterpretationTableLineModel> findAllByTableOrderByIdAsc(
            CropFoliarAnalysisInterpretationTableModel table);

    Optional<CropFoliarAnalysisInterpretationTableLineModel> findByTableAndCrop(
            CropFoliarAnalysisInterpretationTableModel table,
            NomeComum crop);

    boolean existsByTableAndCrop(CropFoliarAnalysisInterpretationTableModel table, NomeComum crop);

    long countByTable(CropFoliarAnalysisInterpretationTableModel table);

    void deleteAllByTable(CropFoliarAnalysisInterpretationTableModel table);
}
