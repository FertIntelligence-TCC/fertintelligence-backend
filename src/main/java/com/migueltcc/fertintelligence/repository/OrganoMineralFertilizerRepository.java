package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganoMineralFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganoMineralFertilizerRepository extends JpaRepository<OrganoMineralFertilizerModel, Long> {

    List<OrganoMineralFertilizerModel> findAllByUser(UserModel user);

    @Query("select f from OrganoMineralFertilizerModel f where f.user = :user or (f.user.cargo = :defaultCreatorCargo and f.publico = true) order by f.name asc")
    List<OrganoMineralFertilizerModel> findAllByUserOrDefaultCreator(@Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<OrganoMineralFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);

    @Query("select f from OrganoMineralFertilizerModel f where lower(f.name) like lower(concat('%', :name, '%')) and (f.user = :user or (f.user.cargo = :defaultCreatorCargo and f.publico = true)) order by f.name asc")
    List<OrganoMineralFertilizerModel> findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(@Param("name") String name, @Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<OrganoMineralFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

    @Query("select f from OrganoMineralFertilizerModel f where f.publico = true or (f.user.cargo = :defaultCreatorCargo and f.publico = true) order by f.name asc")
    List<OrganoMineralFertilizerModel> findAllByPublicoTrueOrDefaultCreatorOrderByNameAsc(@Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<OrganoMineralFertilizerModel> findAllByUser_CargoOrderByNameAsc(Cargo cargo);

}
