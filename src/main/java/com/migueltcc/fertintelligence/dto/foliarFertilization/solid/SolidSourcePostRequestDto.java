package com.migueltcc.fertintelligence.dto.foliarFertilization.solid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolidSourcePostRequestDto {

    @Schema(example = "26/07/2025")
    @JsonProperty("novo_data")
    Date date;

    @Schema(example = "COBRE")
    @JsonProperty("novo_micronutriente_aplicado")
    AppliedMicronutrient micronutrient;

    @Schema(example = "Sulfato de Cobre")
    @JsonProperty("novo_fonte")
    String source;

    @Schema(example = "24.0")
    @JsonProperty("novo_concentracao")
    Double concentration;

    @Schema(example = "2.5")
    @JsonProperty("novo_quantidade_aplicada")
    Double quantity;
}