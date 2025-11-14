package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorResponseDto;

public interface AvailablePAnionExchangeResinExtractorService {

    AvailablePAnionExchangeResinExtractorResponseDto createAvailablePAnionExchangeResinExtractor(
            Long tableId,
            AvailablePAnionExchangeResinExtractorCreateRequestDto createRequestDto,
            String username);

    AvailablePAnionExchangeResinExtractorResponseDto getAvailablePAnionExchangeResinExtractorById(
            Long criterionId,
            String username);

    AvailablePAnionExchangeResinExtractorResponseDto getAvailablePAnionExchangeResinExtractorByTable(
            Long tableId,
            String username);

    AvailablePAnionExchangeResinExtractorResponseDto updateAvailablePAnionExchangeResinExtractor(
            Long criterionId,
            AvailablePAnionExchangeResinExtractorPostRequestDto updateRequestDto,
            String username);

    void deleteAvailablePAnionExchangeResinExtractor(Long criterionId, String username);
}
