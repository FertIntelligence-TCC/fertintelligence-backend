package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.MicronutrientDoseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MicronutrientDoseRepository extends JpaRepository<MicronutrientDoseModel, Long> {

    Optional<MicronutrientDoseModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);
}
