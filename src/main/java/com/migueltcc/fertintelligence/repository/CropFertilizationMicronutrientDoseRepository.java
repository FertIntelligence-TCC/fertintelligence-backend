package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationMicronutrientDoseModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropFertilizationMicronutrientDoseRepository extends JpaRepository<CropFertilizationMicronutrientDoseModel, Long> {
    List<CropFertilizationMicronutrientDoseModel> findAllByTableOrderByMicronutrientAsc(CropFertilizationTableModel table);
    Optional<CropFertilizationMicronutrientDoseModel> findByTableAndMicronutrient(CropFertilizationTableModel table, AppliedMicronutrient micronutrient);
    void deleteAllByTable(CropFertilizationTableModel table);
}
