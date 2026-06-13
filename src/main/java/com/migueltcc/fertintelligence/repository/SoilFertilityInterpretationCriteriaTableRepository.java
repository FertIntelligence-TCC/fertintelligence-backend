package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoilFertilityInterpretationCriteriaTableRepository extends JpaRepository<SoilFertilityInterpretationCriteriaTableModel, Long> {

    List<SoilFertilityInterpretationCriteriaTableModel> findAllByCreator(UserModel creator);
    List<SoilFertilityInterpretationCriteriaTableModel> findAllByCreatorAndCreator_CargoNot(UserModel creator, Cargo cargo);
    List<SoilFertilityInterpretationCriteriaTableModel> findAllByCreator_Cargo(Cargo cargo);
    List<SoilFertilityInterpretationCriteriaTableModel> findAllByCreator_CargoAndPublicTableTrue(Cargo cargo);
    List<SoilFertilityInterpretationCriteriaTableModel> findAllByPublicTableTrue();
    List<SoilFertilityInterpretationCriteriaTableModel> findAllByPublicTableTrueAndCreator_CargoNot(Cargo cargo);
    Optional<SoilFertilityInterpretationCriteriaTableModel> findByIdAndCreator(Long id, UserModel creator);
    Optional<SoilFertilityInterpretationCriteriaTableModel> findByIdAndCreatorAndCreator_CargoNot(Long id, UserModel creator, Cargo cargo);
    Optional<SoilFertilityInterpretationCriteriaTableModel> findByIdAndPublicTableTrue(Long id);
    Optional<SoilFertilityInterpretationCriteriaTableModel> findByIdAndPublicTableTrueAndCreator_CargoNot(Long id, Cargo cargo);
    Optional<SoilFertilityInterpretationCriteriaTableModel> findByIdAndCreator_Cargo(Long id, Cargo cargo);
    Optional<SoilFertilityInterpretationCriteriaTableModel> findByIdAndCreator_CargoAndPublicTableTrue(Long id, Cargo cargo);
    boolean existsByCreatorAndName(UserModel creator, String name);
}
