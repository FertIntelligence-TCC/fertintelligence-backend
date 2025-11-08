package com.migueltcc.fertintelligence.dto.foliarFertilization.solidSource;

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
public class SolidSourceResponseDto {

    @Schema(example = "1")
    @JsonProperty("id")
    Long id;

    @Schema(example = "10")
    @JsonProperty("id_cultura")
    Long crop_id;

    @Schema(example = "{\"day\": 25, \"month\": 7, \"year\": 2024}")
    @JsonProperty("data")
    Date date;

    @Schema(example = "MANGANES")
    @JsonProperty("micronutriente_aplicado")
    AppliedMicronutrient micronutrient;

    @Schema(example = "Sulfato de Manganês")
    @JsonProperty("fonte")
    String source;

    @Schema(example = "30.0")
    @JsonProperty("concentracao")
    Double concentration;

    @Schema(example = "5.0")
    @JsonProperty("quantidade_aplicada")
    Double quantity;
}