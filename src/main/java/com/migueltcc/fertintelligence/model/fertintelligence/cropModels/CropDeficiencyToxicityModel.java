package com.migueltcc.fertintelligence.model.fertintelligence.cropModels;

import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.CropDeficiencyToxicityResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.DeficiencyToxicityNutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.NutrientType;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "DEFICIENCIA_TOXIDEZ_CULTURA")
@EqualsAndHashCode
public class CropDeficiencyToxicityModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CULTURA", nullable = false)
    CropModel crop;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_NUTRIENTE", nullable = false)
    NutrientType nutrientType;

    @Enumerated(EnumType.STRING)
    @Column(name = "NUTRIENTE", nullable = false)
    DeficiencyToxicityNutrient nutrient;

    @Column(name = "ID_IMAGEM_PLANTA_SAUDAVEL")
    String healthyPlantImageId;

    @Column(name = "ID_IMAGEM_PLANTA_SINTOMATICA")
    String symptomaticPlantImageId;

    @Column(name = "OBSERVACOES", columnDefinition = "TEXT")
    String observations;

    public CropDeficiencyToxicityResponseDto toDto() {
        return CropDeficiencyToxicityResponseDto.builder()
                .id(id)
                .cropId(crop.getId())
                .nutrientType(nutrientType)
                .nutrient(nutrient)
                .healthyPlantImageId(healthyPlantImageId)
                .symptomaticPlantImageId(symptomaticPlantImageId)
                .observations(observations)
                .build();
    }
}
