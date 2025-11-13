package com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Regiao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SoilFertilityInterpretationCriteriaTableCreateRequestDto {

    @JsonProperty("regiao")
    @NotNull
    @Schema(example = "NORDESTE")
    private Regiao region;
}