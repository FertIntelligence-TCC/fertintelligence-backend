package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.ExchangeableSodiumModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeableSodiumRepository extends JpaRepository<ExchangeableSodiumModel, Long> {

    Optional<ExchangeableSodiumModel> findByTable(SoilFertilityInterpretationCriteriaTableModel table);

    Optional<ExchangeableSodiumModel> findFirstByTableOrderByIdAsc(SoilFertilityInterpretationCriteriaTableModel table);
}
