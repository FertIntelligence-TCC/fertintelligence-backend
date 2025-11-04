package com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels;

import com.migueltcc.fertintelligence.composedAttributes.Crop.Date;
import jakarta.persistence.*;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "day", column = @Column(name = "DATA_DIA", nullable = false)),
            @AttributeOverride(name = "month", column = @Column(name = "DATA_MES", nullable = false)),
            @AttributeOverride(name = "year", column = @Column(name = "DATA_ANO", nullable = false))
    })
    Date date;
}
