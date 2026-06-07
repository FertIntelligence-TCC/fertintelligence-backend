package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.SimpleMineralFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimpleMineralFertilizerRepository extends JpaRepository<SimpleMineralFertilizerModel, Long> {

    List<SimpleMineralFertilizerModel> findAllByUser(UserModel user);

    List<SimpleMineralFertilizerModel> findAllByUserAndPublicoFalseOrderByNameAsc(UserModel user);

    @Query("select f from SimpleMineralFertilizerModel f where f.user = :user or f.user.cargo = :defaultCreatorCargo order by f.name asc")
    List<SimpleMineralFertilizerModel> findAllByUserOrDefaultCreator(@Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<SimpleMineralFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);

    @Query("select f from SimpleMineralFertilizerModel f where lower(f.name) like lower(concat('%', :name, '%')) and (f.user = :user or f.user.cargo = :defaultCreatorCargo) order by f.name asc")
    List<SimpleMineralFertilizerModel> findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(@Param("name") String name, @Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<SimpleMineralFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

    List<SimpleMineralFertilizerModel> findAllByUser_CargoOrderByNameAsc(Cargo cargo);

    @Query("select f from SimpleMineralFertilizerModel f where f.publico = true or f.user.cargo = :defaultCreatorCargo order by f.name asc")
    List<SimpleMineralFertilizerModel> findAllByPublicoTrueOrDefaultCreatorOrderByNameAsc(@Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<SimpleMineralFertilizerModel> findAllByUser_CargoOrderByNameAsc(Cargo cargo);

}
