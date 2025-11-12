package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropFertilizationTableRepository extends JpaRepository<CropFertilizationTableModel, Long> {

    List<CropFertilizationTableModel> findAllByCreator(UserModel creator);
}