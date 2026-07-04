package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationResponseDto;

import java.util.List;

public interface CorrectiveK2OFertilizationService {

    CorrectiveK2OFertilizationResponseDto createCorrectiveK2OFertilization(Long tableId, CorrectiveK2OFertilizationCreateRequestDto createRequestDto, String username);

    CorrectiveK2OFertilizationResponseDto getCorrectiveK2OFertilizationById(Long criterionId, String username);

    List<CorrectiveK2OFertilizationResponseDto> getCorrectiveK2OFertilizationByTable(Long tableId, String username);

    CorrectiveK2OFertilizationResponseDto updateCorrectiveK2OFertilization(Long criterionId, CorrectiveK2OFertilizationPostRequestDto updateRequestDto, String username);

    void deleteCorrectiveK2OFertilization(Long criterionId, String username);
}
