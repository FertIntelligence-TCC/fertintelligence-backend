package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.MineralFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MineralFertilizerRepository extends JpaRepository<MineralFertilizerModel, Long> {

    List<MineralFertilizerModel> findAllByUser(UserModel user);

    @Query("select f from MineralFertilizerModel f where f.user = :user or f.user.cargo = :defaultCreatorCargo order by f.name asc")
    List<MineralFertilizerModel> findAllByUserOrDefaultCreator(@Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<MineralFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);

    @Query("select f from MineralFertilizerModel f where lower(f.name) like lower(concat('%', :name, '%')) and (f.user = :user or f.user.cargo = :defaultCreatorCargo) order by f.name asc")
    List<MineralFertilizerModel> findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(@Param("name") String name, @Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<MineralFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

    @Query("select f from MineralFertilizerModel f where f.publico = true or f.user.cargo = :defaultCreatorCargo order by f.name asc")
    List<MineralFertilizerModel> findAllByPublicoTrueOrDefaultCreatorOrderByNameAsc(@Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

}
