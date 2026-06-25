package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDoseResponseDto;

public interface SulfurDoseService {

    SulfurDoseResponseDto createSulfurDose(Long tableId, SulfurDoseCreateRequestDto createRequestDto, String username);

    SulfurDoseResponseDto getSulfurDoseById(Long criterionId, String username);

    SulfurDoseResponseDto getSulfurDoseByTable(Long tableId, String username);

    SulfurDoseResponseDto updateSulfurDose(Long criterionId, SulfurDosePostRequestDto updateRequestDto, String username);

    void deleteSulfurDose(Long criterionId, String username);
}
