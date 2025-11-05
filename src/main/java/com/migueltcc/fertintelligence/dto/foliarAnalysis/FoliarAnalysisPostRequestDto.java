package com.migueltcc.fertintelligence.dto.foliarAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoliarAnalysisPostRequestDto {

    @Schema(example = "20/02/2025")
    @JsonProperty("novo_data_coleta")
    Date collectDate;

    @Schema(example = "Novo Laboratório Agro")
    @JsonProperty("novo_laboratorio")
    String laboratory;

    @JsonProperty("novo_micronutrientes")
    MicronutrientsContentDto micronutrients;

    @JsonProperty("novo_macronutrientes")
    MacronutrientsContentDto macronutrients;

    @JsonProperty("novo_elementos_beneficos")
    BeneficialElementsContentDto elements;

}