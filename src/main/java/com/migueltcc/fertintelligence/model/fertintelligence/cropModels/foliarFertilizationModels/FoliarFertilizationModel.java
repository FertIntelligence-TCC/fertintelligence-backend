package com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels;

import com.migueltcc.fertintelligence.composedAttributes.Crop.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "ADUBACAO_FOLIAR")
@EqualsAndHashCode
public class FoliarFertilizationModel {

    Long id;

    Date date;

}
