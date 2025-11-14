package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.SalinityInterpretationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalinityInterpretationRepository extends JpaRepository<SalinityInterpretationModel, Long> {

    Optional<SalinityInterpretationModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);
}