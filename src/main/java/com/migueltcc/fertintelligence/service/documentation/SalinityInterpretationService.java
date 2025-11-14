package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationResponseDto;

public interface SalinityInterpretationService {

    SalinityInterpretationResponseDto createSalinityInterpretation(
            Long tableId,
            SalinityInterpretationCreateRequestDto createRequestDto,
            String username);

    SalinityInterpretationResponseDto getSalinityInterpretationById(Long criterionId, String username);

    SalinityInterpretationResponseDto getSalinityInterpretationByTable(Long tableId, String username);

    SalinityInterpretationResponseDto updateSalinityInterpretation(
            Long criterionId,
            SalinityInterpretationPostRequestDto updateRequestDto,
            String username);

    void deleteSalinityInterpretation(Long criterionId, String username);
}