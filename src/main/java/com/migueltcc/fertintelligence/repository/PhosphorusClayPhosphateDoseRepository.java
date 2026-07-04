package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.PhosphorusClayPhosphateDoseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhosphorusClayPhosphateDoseRepository extends JpaRepository<PhosphorusClayPhosphateDoseModel, Long> {

    Optional<PhosphorusClayPhosphateDoseModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);
}
