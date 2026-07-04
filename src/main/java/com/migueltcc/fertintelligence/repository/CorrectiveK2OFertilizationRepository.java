package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.CorrectiveK2OFertilizationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrectiveK2OFertilizationRepository extends JpaRepository<CorrectiveK2OFertilizationModel, Long> {

    List<CorrectiveK2OFertilizationModel> findAllByTableOrderByCtcMinimumAscExchangeableKMinimumAsc(
            SoilFertilityInterpretationCriteriaTableModel table);
}
