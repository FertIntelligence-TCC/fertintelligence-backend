package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.KExchangeableContentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KExchangeableContentRepository extends JpaRepository<KExchangeableContentModel, Long> {

    Optional<KExchangeableContentModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);
}