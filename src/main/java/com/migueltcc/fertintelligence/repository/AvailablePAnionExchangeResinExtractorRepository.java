package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailablePAnionExchangeResinExtractorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailablePAnionExchangeResinExtractorRepository
        extends JpaRepository<AvailablePAnionExchangeResinExtractorModel, Long> {

    Optional<AvailablePAnionExchangeResinExtractorModel> findByTable(
            SoilFertilityInterpretationCriteriaTableModel table);
}