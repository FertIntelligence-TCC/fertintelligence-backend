package com.migueltcc.fertintelligence.repository;

import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlotRepository extends JpaRepository<PlotModel, Long> {

    Optional<PlotModel> findByIdentificationAndProperty(String identification, PropertyModel property);

    List<PlotModel> findAllByProperty(PropertyModel property);
}