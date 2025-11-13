package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoilFertilityInterpretationCriteriaTablePostRequestDto {

    @JsonProperty("nova_regiao")
    @Schema(example = "CENTRO_OESTE")
    private Regiao region;
}