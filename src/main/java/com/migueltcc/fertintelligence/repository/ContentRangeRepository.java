package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.ContentRangeModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRangeRepository extends JpaRepository<ContentRangeModel, Long> {

    List<ContentRangeModel> findAllByTableOrderByNutrientAscOrderAsc(CropFertilizationTableModel table);

    List<ContentRangeModel> findAllByTableAndNutrientOrderByOrderAsc(CropFertilizationTableModel table, Nutriente nutrient);
}