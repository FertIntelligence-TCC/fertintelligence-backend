package com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.DeficiencyToxicityNutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.NutrientType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropDeficiencyToxicityCreateRequestDto {

    @JsonProperty("nutrient_type")
    @NotNull
    NutrientType nutrientType;

    @JsonProperty("nutrient")
    @NotNull
    DeficiencyToxicityNutrient nutrient;

    @JsonProperty("healthy_plant_image_id")
    String healthyPlantImageId;

    @JsonProperty("symptomatic_plant_image_id")
    String symptomaticPlantImageId;

    @JsonProperty("observations")
    String observations;
}
