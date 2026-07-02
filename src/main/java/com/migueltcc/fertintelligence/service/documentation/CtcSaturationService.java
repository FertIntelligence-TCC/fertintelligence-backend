package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.ctcSaturation.CtcSaturationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.ctcSaturation.CtcSaturationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.ctcSaturation.CtcSaturationResponseDto;

public interface CtcSaturationService {

    CtcSaturationResponseDto createCtcSaturation(Long tableId, CtcSaturationCreateRequestDto createRequestDto, String username);

    CtcSaturationResponseDto getCtcSaturationById(Long criterionId, String username);

    CtcSaturationResponseDto getCtcSaturationByTable(Long tableId, String username);

    CtcSaturationResponseDto updateCtcSaturation(Long criterionId, CtcSaturationPostRequestDto updateRequestDto, String username);

    void deleteCtcSaturation(Long criterionId, String username);
}
