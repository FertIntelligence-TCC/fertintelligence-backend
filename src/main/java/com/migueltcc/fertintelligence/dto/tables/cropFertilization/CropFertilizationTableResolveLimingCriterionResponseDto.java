package com.migueltcc.fertintelligence.dto.tables.cropFertilization;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropFertilizationTableResolveLimingCriterionResponseDto {

    @JsonProperty("indicatedLimingCriterion")
    private String indicatedLimingCriterion;
}
