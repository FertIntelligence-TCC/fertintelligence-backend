package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePMehlich1ExtractorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailablePMehlich1ExtractorRepository
        extends JpaRepository<AvailablePMehlich1ExtractorModel, Long> {

    Optional<AvailablePMehlich1ExtractorModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);
}