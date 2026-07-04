package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationResponseDto;

import java.util.List;

public interface CorrectiveP2O5FertilizationService {

    CorrectiveP2O5FertilizationResponseDto createCorrectiveP2O5Fertilization(Long tableId, CorrectiveP2O5FertilizationCreateRequestDto createRequestDto, String username);

    CorrectiveP2O5FertilizationResponseDto getCorrectiveP2O5FertilizationById(Long criterionId, String username);

    List<CorrectiveP2O5FertilizationResponseDto> getCorrectiveP2O5FertilizationByTable(Long tableId, String username);

    CorrectiveP2O5FertilizationResponseDto updateCorrectiveP2O5Fertilization(Long criterionId, CorrectiveP2O5FertilizationPostRequestDto updateRequestDto, String username);

    void deleteCorrectiveP2O5Fertilization(Long criterionId, String username);
}
