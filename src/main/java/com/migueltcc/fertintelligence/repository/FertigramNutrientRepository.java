package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramModel;
import com.migueltcc.fertintelligence.model.fertintelligence.fertigram.FertigramNutrientModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FertigramNutrientRepository extends JpaRepository<FertigramNutrientModel, Long> {
    List<FertigramNutrientModel> findAllByFertigramOrderByIdAsc(FertigramModel fertigram);
}
