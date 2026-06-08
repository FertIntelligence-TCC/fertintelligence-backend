package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropFertilizationTableRepository extends JpaRepository<CropFertilizationTableModel, Long> {

    List<CropFertilizationTableModel> findAllByCreator_CargoAndPublicTableTrue(Cargo cargo);

    Optional<CropFertilizationTableModel> findByIdAndCreator_CargoAndPublicTableTrue(Long id, Cargo cargo);

    List<CropFertilizationTableModel> findAllByCreator(UserModel creator);
    List<CropFertilizationTableModel> findAllByCreatorAndCreator_CargoNot(UserModel creator, Cargo cargo);
    List<CropFertilizationTableModel> findAllByCreator_Cargo(Cargo cargo);
    List<CropFertilizationTableModel> findAllByPublicTableTrue();
    List<CropFertilizationTableModel> findAllByPublicTableTrueAndCreator_CargoNot(Cargo cargo);
    Optional<CropFertilizationTableModel> findByIdAndCreator(Long id, UserModel creator);
    Optional<CropFertilizationTableModel> findByIdAndCreatorAndCreator_CargoNot(Long id, UserModel creator, Cargo cargo);
    Optional<CropFertilizationTableModel> findByIdAndPublicTableTrue(Long id);
    Optional<CropFertilizationTableModel> findByIdAndPublicTableTrueAndCreator_CargoNot(Long id, Cargo cargo);
    Optional<CropFertilizationTableModel> findByIdAndCreator_Cargo(Long id, Cargo cargo);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CropFertilizationTableModel c WHERE c.crop_common_name = :nomeComum AND c.region = :regiao")
    boolean existsByCropCommonNameAndRegion(@Param("nomeComum") NomeComum nomeComum, @Param("regiao") Regiao regiao);

}
