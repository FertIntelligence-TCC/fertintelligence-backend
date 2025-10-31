package com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels;

import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.Camada;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Data
// @Table(name = "EXTRATOS_CAMADAS")
@EqualsAndHashCode(callSuper = true)
public class LayerExtractModel extends ExtractTemplateModel {

    // Camada O, A, B, E, C
    @Column(name = "CAMADA", nullable = false)
    Camada layer;

    @Column(name = "SUBCAMADA", nullable = false)
    Integer sub_layer;
}
