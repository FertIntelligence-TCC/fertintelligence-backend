package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypeResponseDto;

public interface RecommendedLimestoneTypeService {

    RecommendedLimestoneTypeResponseDto createRecommendedLimestoneType(Long tableId, RecommendedLimestoneTypeCreateRequestDto createRequestDto, String username);

    RecommendedLimestoneTypeResponseDto getRecommendedLimestoneTypeById(Long criterionId, String username);

    RecommendedLimestoneTypeResponseDto getRecommendedLimestoneTypeByTable(Long tableId, String username);

    RecommendedLimestoneTypeResponseDto updateRecommendedLimestoneType(Long criterionId, RecommendedLimestoneTypePostRequestDto updateRequestDto, String username);

    void deleteRecommendedLimestoneType(Long criterionId, String username);
}
