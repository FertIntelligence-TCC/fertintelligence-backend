package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.GreenFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GreenFertilizerRepository extends JpaRepository<GreenFertilizerModel, Long> {

    List<GreenFertilizerModel> findAllByUser(UserModel user);

    @Query("select f from GreenFertilizerModel f where f.user = :user or f.user.cargo = :defaultCreatorCargo order by f.name asc")
    List<GreenFertilizerModel> findAllByUserOrDefaultCreator(@Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<GreenFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);

    @Query("select f from GreenFertilizerModel f where lower(f.name) like lower(concat('%', :name, '%')) and (f.user = :user or f.user.cargo = :defaultCreatorCargo) order by f.name asc")
    List<GreenFertilizerModel> findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(@Param("name") String name, @Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<GreenFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

    @Query("select f from GreenFertilizerModel f where f.publico = true or f.user.cargo = :defaultCreatorCargo order by f.name asc")
    List<GreenFertilizerModel> findAllByPublicoTrueOrDefaultCreatorOrderByNameAsc(@Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<GreenFertilizerModel> findAllByUser_CargoOrderByNameAsc(Cargo cargo);

}
