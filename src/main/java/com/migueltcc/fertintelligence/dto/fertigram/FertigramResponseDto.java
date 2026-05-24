package com.migueltcc.fertintelligence.dto.fertigram;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FertigramResponseDto {
    private Long id;
    private Long foliarAnalysisId;
    private Long tableId;
    private String cropName;
    private String warning;
}
