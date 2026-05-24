package com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.DeficiencyToxicityNutrient;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.enums.NutrientType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropDeficiencyToxicityCreateRequestDto {

    @JsonAlias("nutrient_type")
    @NotNull
    NutrientType nutrientType;

    @JsonAlias("nutrient")
    @NotNull
    DeficiencyToxicityNutrient nutrient;

    @JsonAlias("healthy_plant_image_id")
    String healthyPlantImageId;

    @JsonAlias("symptomatic_plant_image_id")
    String symptomaticPlantImageId;

    @JsonAlias("observations")
    String observations;
}
