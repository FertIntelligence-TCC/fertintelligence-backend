package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels.ChelatedFertilizerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChelatedFertilizerRepository extends JpaRepository<ChelatedFertilizerModel, Long> {
    List<ChelatedFertilizerModel> findAllByUserAndPublicoFalseOrderByNameAsc(UserModel user);

    List<ChelatedFertilizerModel> findAllByUser(UserModel user);

    List<ChelatedFertilizerModel> findAllByUserOrderByNameAsc(UserModel user);

    @Query("select f from ChelatedFertilizerModel f where f.user.username = :username order by f.name asc")
    List<ChelatedFertilizerModel> findAllByUserUsernameOrderByNameAsc(@Param("username") String username);

    @Query("select f from ChelatedFertilizerModel f where f.user = :user or (f.user.cargo = :defaultCreatorCargo and f.publico = true) order by f.name asc")
    List<ChelatedFertilizerModel> findAllByUserOrDefaultCreator(@Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<ChelatedFertilizerModel> findAllByNameContainingIgnoreCaseAndUser(String name, UserModel user);

    @Query("select f from ChelatedFertilizerModel f where lower(f.name) like lower(concat('%', :name, '%')) and (f.user = :user or (f.user.cargo = :defaultCreatorCargo and f.publico = true)) order by f.name asc")
    List<ChelatedFertilizerModel> findAllByNameContainingIgnoreCaseAndUserOrDefaultCreator(@Param("name") String name, @Param("user") UserModel user, @Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<ChelatedFertilizerModel> findAllByPublicoTrueOrderByNameAsc();

    List<ChelatedFertilizerModel> findAllByPublicoTrueAndUser_CargoNotOrderByNameAsc(Cargo cargo);

    @Query("select f from ChelatedFertilizerModel f where f.publico = true or (f.user.cargo = :defaultCreatorCargo and f.publico = true) order by f.name asc")
    List<ChelatedFertilizerModel> findAllByPublicoTrueOrDefaultCreatorOrderByNameAsc(@Param("defaultCreatorCargo") Cargo defaultCreatorCargo);

    List<ChelatedFertilizerModel> findAllByUser_CargoOrderByNameAsc(Cargo cargo);

}
