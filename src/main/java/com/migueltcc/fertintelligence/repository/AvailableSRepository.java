package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.AvailableSModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailableSRepository extends JpaRepository<AvailableSModel, Long> {

    Optional<AvailableSModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);
}
