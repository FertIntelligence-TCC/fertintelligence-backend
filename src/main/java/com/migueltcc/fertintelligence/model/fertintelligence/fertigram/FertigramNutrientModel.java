package com.migueltcc.fertintelligence.model.fertintelligence.fertigram;

import com.migueltcc.fertintelligence.dto.fertigram.FertigramNutrientDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "FERTIGRAMA_NUTRIENTE")
public class FertigramNutrientModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FERTIGRAMA", nullable = false)
    FertigramModel fertigram;

    @Column(name = "NUTRIENTE", nullable = false)
    String nutrient;

    @Column(name = "TIPO_GRUPO", nullable = false)
    @Enumerated(EnumType.STRING)
    FertigramNutrientGroupType groupType;

    @Column(name = "VALOR_MEDIDO", nullable = false)
    Double measuredValue;

    @Column(name = "VALOR_RECOMENDADO_MIN")
    Double recommendedMinimum;

    @Column(name = "VALOR_RECOMENDADO_MAX")
    Double recommendedMaximum;

    @Column(name = "UNIDADE")
    String unit;

    @Column(name = "INTERPRETACAO")
    String interpretation;

    public FertigramNutrientDto toDto() {
        return FertigramNutrientDto.builder()
                .id(id)
                .fertigramId(fertigram.getId())
                .nutrient(nutrient)
                .groupType(groupType != null ? groupType.name() : null)
                .measuredValue(measuredValue)
                .recommendedMinimum(recommendedMinimum)
                .recommendedMaximum(recommendedMaximum)
                .unit(unit)
                .interpretation(interpretation)
                .build();
    }
}
