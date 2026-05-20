package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationResponseDto;

import java.util.List;

public interface RecommendationService {
    RecommendationResponseDto generate(RecommendationCreateRequestDto dto, String username);
    RecommendationResponseDto preparePrint(Long id, String username);
    RecommendationResponseDto get(Long id, String username);
    List<RecommendationResponseDto> getMine(String username);
    List<RecommendationResponseDto> getByProperty(Long propertyId, String username);
    List<RecommendationResponseDto> getByPlot(Long plotId, String username);
    void delete(Long id, String username);
}
