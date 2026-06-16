package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.FormulatedMineralFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormulatedMineralFertilizerRepository extends JpaRepository<FormulatedMineralFertilizerModel, Long> {
    
    @Query("select f from FormulatedMineralFertilizerModel f where f.user = :user and f.publico = false order by f.formulate.n asc, f.formulate.p asc, f.formulate.k asc")
    List<FormulatedMineralFertilizerModel> findAllByUserAndPublicoFalseOrderByFormulaAsc(@Param("user") UserModel user);
    

    List<FormulatedMineralFertilizerModel> findAllByUser(UserModel user);

    List<FormulatedMineralFertilizerModel> findAllByUserAndPublicoFalseOrderByIdAsc(UserModel user);

    @Query("select f from FormulatedMineralFertilizerModel f where f.user = :user or (f.user.cargo = :defaultCreatorCargo and f.publico = true) order by f.id asc")
    List<FormulatedMineralFertilizerModel> findAllByUserOrDefaultCreator(@Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<FormulatedMineralFertilizerModel> findAllByPublicoTrueOrderByIdAsc();

    List<FormulatedMineralFertilizerModel> findAllByUser_CargoOrderByIdAsc(Cargo cargo);

    @Query("select f from FormulatedMineralFertilizerModel f where f.publico = true or (f.user.cargo = :defaultCreatorCargo and f.publico = true) order by f.id asc")
    List<FormulatedMineralFertilizerModel> findAllByPublicoTrueOrDefaultCreatorOrderByIdAsc(@Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

}
