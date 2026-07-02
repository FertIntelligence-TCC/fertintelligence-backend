package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.CtcSaturationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CtcSaturationRepository extends JpaRepository<CtcSaturationModel, Long> {

    Optional<CtcSaturationModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);
}
