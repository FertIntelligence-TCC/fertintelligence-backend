package com.migueltcc.fertintelligence.dto.fertigram;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FertigramResponseDto {
    private Long foliarAnalysisId;
    private Long tableId;
    private String cropName;
    @Builder.Default
    private List<FertigramNutrientDto> macronutrients = new ArrayList<>();
    @Builder.Default
    private List<FertigramNutrientDto> micronutrients = new ArrayList<>();
    private String warning;
}
