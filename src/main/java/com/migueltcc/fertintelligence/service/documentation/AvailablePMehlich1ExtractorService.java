package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorResponseDto;

public interface AvailablePMehlich1ExtractorService {

    AvailablePMehlich1ExtractorResponseDto createAvailablePMehlich1Extractor(
            Long tableId,
            AvailablePMehlich1ExtractorCreateRequestDto createRequestDto,
            String username);

    AvailablePMehlich1ExtractorResponseDto getAvailablePMehlich1ExtractorById(
            Long criterionId,
            String username);

    AvailablePMehlich1ExtractorResponseDto getAvailablePMehlich1ExtractorByTable(
            Long tableId,
            String username);

    AvailablePMehlich1ExtractorResponseDto updateAvailablePMehlich1Extractor(
            Long criterionId,
            AvailablePMehlich1ExtractorPostRequestDto updateRequestDto,
            String username);

    void deleteAvailablePMehlich1Extractor(Long criterionId, String username);
}