package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.BioFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BioFertilizerRepository extends JpaRepository<BioFertilizerModel, Long> {
    List<BioFertilizerModel> findAllByUserAndPublicoFalseOrderByNameAsc(UserModel user);

    List<BioFertilizerModel> findAllByUser(UserModel user);

    @Query("select f from BioFertilizerModel f where f.user = :user or (f.user.cargo = :defaultCreatorCargo and f.publico = true) order by f.name asc")
    List<BioFertilizerModel> findAllByUserOrDefaultCreator(@Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<BioFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);

    @Query("select f from BioFertilizerModel f where lower(f.name) like lower(concat('%', :name, '%')) and (f.user = :user or (f.user.cargo = :defaultCreatorCargo and f.publico = true)) order by f.name asc")
    List<BioFertilizerModel> findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(@Param("name") String name, @Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<BioFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

    @Query("select f from BioFertilizerModel f where f.publico = true or (f.user.cargo = :defaultCreatorCargo and f.publico = true) order by f.name asc")
    List<BioFertilizerModel> findAllByPublicoTrueOrDefaultCreatorOrderByNameAsc(@Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<BioFertilizerModel> findAllByUser_CargoOrderByNameAsc(Cargo cargo);

}
