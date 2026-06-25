package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.SulfurDoseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SulfurDoseRepository extends JpaRepository<SulfurDoseModel, Long> {

    Optional<SulfurDoseModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);
}
