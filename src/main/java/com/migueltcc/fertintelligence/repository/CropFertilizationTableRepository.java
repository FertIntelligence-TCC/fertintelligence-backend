package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.CropFertilizationTableModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropFertilizationTableRepository extends JpaRepository<CropFertilizationTableModel, Long> {

    List<CropFertilizationTableModel> findAllByCreator(UserModel creator);
    List<CropFertilizationTableModel> findAllByPublicTableTrue();

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CropFertilizationTableModel c WHERE c.crop_common_name = :nomeComum AND c.region = :regiao")
    boolean existsByCropCommonNameAndRegion(@Param("nomeComum") NomeComum nomeComum, @Param("regiao") Regiao regiao);

}