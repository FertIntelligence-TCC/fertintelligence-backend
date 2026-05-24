package com.migueltcc.fertintelligence.dto.fertigram;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FertigramNutrientDto {
    private String nutrient;
    private Double measuredValue;
    private Double recommendedMinimum;
    private Double recommendedMaximum;
    private String unit;
    private String interpretation;
}
