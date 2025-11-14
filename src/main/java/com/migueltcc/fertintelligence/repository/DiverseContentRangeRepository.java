package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.DiverseContentRangeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiverseContentRangeRepository extends JpaRepository<DiverseContentRangeModel, Long> {

    Optional<DiverseContentRangeModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);
}