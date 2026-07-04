package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria.CorrectiveP2O5FertilizationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrectiveP2O5FertilizationRepository extends JpaRepository<CorrectiveP2O5FertilizationModel, Long> {

    List<CorrectiveP2O5FertilizationModel> findAllByTableOrderByClayContentMinimumAscAvailablePMehlich1MinimumAsc(
            SoilFertilityInterpretationCriteriaTableModel table);
}
