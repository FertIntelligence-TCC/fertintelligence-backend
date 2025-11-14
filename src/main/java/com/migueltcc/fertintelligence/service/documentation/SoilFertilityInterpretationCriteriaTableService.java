package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableResponseDto;

import java.util.List;

public interface SoilFertilityInterpretationCriteriaTableService {

    SoilFertilityInterpretationCriteriaTableResponseDto createSoilFertilityInterpretationCriteriaTable(
            SoilFertilityInterpretationCriteriaTableCreateRequestDto createRequestDto,
            String username
    );

    SoilFertilityInterpretationCriteriaTableResponseDto getSoilFertilityInterpretationCriteriaTableById(
            Long tableId,
            String username
    );

    List<SoilFertilityInterpretationCriteriaTableResponseDto> getAllSoilFertilityInterpretationCriteriaTablesByCreator(
            String username
    );

    SoilFertilityInterpretationCriteriaTableResponseDto updateSoilFertilityInterpretationCriteriaTable(
            Long tableId,
            SoilFertilityInterpretationCriteriaTablePostRequestDto updateRequestDto,
            String username
    );

    void deleteSoilFertilityInterpretationCriteriaTable(Long tableId, String username);
}