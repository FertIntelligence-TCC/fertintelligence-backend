package com.migueltcc.fertintelligence.dto.foliarFertilization.liquidSource;

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
public class LiquidSourceResponseDto {

    @Schema(example = "1")
    @JsonProperty("id")
    Long id;

    @Schema(example = "10")
    @JsonProperty("id_cultura")
    Long crop_id;

    @Schema(example = "{\"day\": 20, \"month\": 6, \"year\": 2024}")
    @JsonProperty("data")
    Date date;

    @Schema(example = "BORO")
    @JsonProperty("micronutriente_aplicado")
    AppliedMicronutrient micronutrient;

    @Schema(example = "Ácido Bórico")
    @JsonProperty("fonte")
    String source;

    @Schema(example = "17.0")
    @JsonProperty("concentracao")
    Double concentration;

    @Schema(example = "1.2")
    @JsonProperty("densidade")
    Double density;

    @Schema(example = "2.5")
    @JsonProperty("volume_aplicado")
    Double applied_volume;

    @Schema(example = "200.0")
    @JsonProperty("volume_calda")
    Double tail_volume;
}