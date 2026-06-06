package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoilFertilityInterpretationCriteriaTableRepository extends JpaRepository<SoilFertilityInterpretationCriteriaTableModel, Long> {

    List<SoilFertilityInterpretationCriteriaTableModel> findAllByCreator(UserModel creator);
    List<SoilFertilityInterpretationCriteriaTableModel> findAllByCreator_Cargo(Cargo cargo);
    List<SoilFertilityInterpretationCriteriaTableModel> findAllByPublicTableTrue();
    boolean existsByCreatorAndName(UserModel creator, String name);
}
