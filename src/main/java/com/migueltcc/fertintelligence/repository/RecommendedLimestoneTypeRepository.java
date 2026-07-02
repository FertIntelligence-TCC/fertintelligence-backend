package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.RecommendedLimestoneTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecommendedLimestoneTypeRepository extends JpaRepository<RecommendedLimestoneTypeModel, Long> {

    Optional<RecommendedLimestoneTypeModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);
}
