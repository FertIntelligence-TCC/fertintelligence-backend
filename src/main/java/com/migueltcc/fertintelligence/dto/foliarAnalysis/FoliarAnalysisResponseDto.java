package com.migueltcc.fertintelligence.dto.foliarAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FoliarAnalysisResponseDto {

    @Schema(example = "404")
    @JsonProperty("id")
    Long id;

    @Schema(example = "16/05/2018")
    @JsonProperty("data_coleta")
    Date collectDate;

    @Schema(example = "EMBRAPA Algodão")
    @JsonProperty("laboratorio")
    String laboratory;

    @JsonProperty("micronutrientes")
    MicronutrientsContentDto micronutrients;

    @JsonProperty("macronutrientes")
    MacronutrientsContentDto macronutrients;

    @JsonProperty("elementos_beneficos")
    BeneficialElementsContentDto elements;

    @Schema(example = "201")
    @JsonProperty("id_cultura")
    Long cropId;

    @Schema(example = "Soja")
    @JsonProperty("nome_cultura")
    NomeComum cropName;

    @Schema(example = "TMG 7062 IPRO")
    @JsonProperty("variedade_cultura")
    String cropVariety;

}