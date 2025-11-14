package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeResponseDto;

public interface DiverseContentRangeService {

    DiverseContentRangeResponseDto createDiverseContentRange(
            Long tableId,
            DiverseContentRangeCreateRequestDto createRequestDto,
            String username);

    DiverseContentRangeResponseDto getDiverseContentRangeById(Long criterionId, String username);

    DiverseContentRangeResponseDto getDiverseContentRangeByTable(Long tableId, String username);

    DiverseContentRangeResponseDto updateDiverseContentRange(
            Long criterionId,
            DiverseContentRangePostRequestDto updateRequestDto,
            String username);

    void deleteDiverseContentRange(Long criterionId, String username);
}
