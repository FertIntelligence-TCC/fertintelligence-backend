package com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.DeficiencyToxicityNutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.NutrientType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropDeficiencyToxicityPostRequestDto {

    @JsonAlias("nutrient_type")
    NutrientType nutrientType;

    @JsonAlias("nutrient")
    DeficiencyToxicityNutrient nutrient;

    @JsonAlias("healthy_plant_image_id")
    String healthyPlantImageId;

    @JsonAlias("symptomatic_plant_image_id")
    String symptomaticPlantImageId;

    @JsonAlias("observations")
    String observations;
}
