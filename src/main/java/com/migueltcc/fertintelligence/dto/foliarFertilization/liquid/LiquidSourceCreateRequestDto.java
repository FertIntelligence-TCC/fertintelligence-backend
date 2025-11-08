package com.migueltcc.fertintelligence.dto.foliarFertilization.liquid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidSourceCreateRequestDto {

    @JsonProperty("data")
    @NotNull
    Date date;

    @JsonProperty("micronutriente_aplicado")
    @NotNull
    AppliedMicronutrient micronutrient;

    @JsonProperty("fonte")
    @NotNull
    String source;

    @JsonProperty("concentracao")
    @NotNull
    Double concentration;

    @JsonProperty("densidade")
    @NotNull
    Double density;

    @JsonProperty("volume_aplicado")
    @NotNull
    Double applied_volume;

    @JsonProperty("volume_calda")
    @NotNull
    Double tail_volume;
}