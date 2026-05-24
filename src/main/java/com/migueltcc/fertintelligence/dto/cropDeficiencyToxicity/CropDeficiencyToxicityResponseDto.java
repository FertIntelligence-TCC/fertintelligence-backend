package com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.DeficiencyToxicityNutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.NutrientType;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropDeficiencyToxicityResponseDto {
    Long id;

    @JsonProperty("crop_id")
    Long cropId;

    @JsonProperty("nutrient_type")
    NutrientType nutrientType;

    @JsonProperty("nutrient")
    DeficiencyToxicityNutrient nutrient;

    @JsonProperty("healthy_plant_image_id")
    String healthyPlantImageId;

    @JsonProperty("symptomatic_plant_image_id")
    String symptomaticPlantImageId;

    @JsonProperty("observations")
    String observations;
}
