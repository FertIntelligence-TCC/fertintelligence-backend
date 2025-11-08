package com.migueltcc.fertintelligence.dto.foliarFertilization.liquid;

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
public class LiquidSourcePostRequestDto {

    @Schema(example = "21/06/2025")
    @JsonProperty("novo_data")
    Date date;

    @Schema(example = "ZINCO")
    @JsonProperty("novo_micronutriente_aplicado")
    AppliedMicronutrient micronutrient;

    @Schema(example = "Sulfato de Zinco")
    @JsonProperty("novo_fonte")
    String source;

    @Schema(example = "20.0")
    @JsonProperty("novo_concentracao")
    Double concentration;

    @Schema(example = "1.3")
    @JsonProperty("novo_densidade")
    Double density;

    @Schema(example = "3.0")
    @JsonProperty("novo_volume_aplicado")
    Double applied_volume;

    @Schema(example = "250.0")
    @JsonProperty("novo_volume_calda")
    Double tail_volume;
}