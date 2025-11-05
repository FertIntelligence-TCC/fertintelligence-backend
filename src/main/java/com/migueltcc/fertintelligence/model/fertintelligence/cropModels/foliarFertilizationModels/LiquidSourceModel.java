package com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels;

import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "FONTE_LIQUIDA")
@EqualsAndHashCode
public class LiquidSourceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_FERTILIZACAO_FOLIAR", nullable = false)
    FoliarFertilizationModel fertilization;

    @Column(name = "MICRONUTRIENTE_APLICADO", nullable = false)
    AppliedMicronutrient micronutrient;

    @Column(name = "FONTE", nullable = false)
    String source;

    @Column(name = "CONCENTRACAO", nullable = false)
    Double concentration;

    @Column(name = "DENSIDADE", nullable = false)
    Double density;

    @Column(name = "VOLUME_APLICADO", nullable = false)
    Double applied_volume;

    @Column(name = "VOLUME_CALDA", nullable = false)
    Double tail_volume;

}
