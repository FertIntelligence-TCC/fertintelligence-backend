package com.migueltcc.fertintelligence.dto.tables.cropFertilization;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropFertilizationTableResolveLimingCriterionRequestDto {

    @NotNull
    @JsonProperty("cropFertilizationTableId")
    private Long cropFertilizationTableId;

    @NotNull
    @JsonProperty("propertyId")
    private Long propertyId;

    @NotNull
    @JsonProperty("plotId")
    private Long plotId;

    @JsonProperty("physicalAnalysisId")
    private Long physicalAnalysisId;

    @NotNull
    @JsonProperty("fertilityAnalysisId")
    private Long fertilityAnalysisId;
}
