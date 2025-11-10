package com.migueltcc.fertintelligence.dto.foliarAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import io.swagger.v3.oas.annotations.media.Schema; // Adicionado para Schema
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoliarAnalysisCreateRequestDto {

    @JsonProperty("data_coleta")
    @NotNull
    Date collectDate;

    @JsonProperty("laboratorio")
    @NotNull
    String laboratory;

    @JsonProperty("micronutrientes")
    MicronutrientsContentDto micronutrients;

    @JsonProperty("macronutrientes")
    MacronutrientsContentDto macronutrients;

    @JsonProperty("elementos_beneficos")
    BeneficialElementsContentDto elements;

}