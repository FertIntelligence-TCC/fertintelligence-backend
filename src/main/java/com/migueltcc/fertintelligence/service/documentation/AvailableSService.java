package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSResponseDto;

public interface AvailableSService {

    AvailableSResponseDto createAvailableS(Long tableId, AvailableSCreateRequestDto createRequestDto, String username);

    AvailableSResponseDto getAvailableSById(Long criterionId, String username);

    AvailableSResponseDto getAvailableSByTable(Long tableId, String username);

    AvailableSResponseDto updateAvailableS(Long criterionId, AvailableSPostRequestDto updateRequestDto, String username);

    void deleteAvailableS(Long criterionId, String username);
}