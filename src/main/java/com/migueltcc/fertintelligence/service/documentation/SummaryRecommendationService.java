package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SummaryRecommendationModel;

public interface SummaryRecommendationService {
    SummaryRecommendationResponseDto create(SummaryRecommendationCreateRequestDto dto, String username);
    SummaryRecommendationModel createInitial(RecommendationModel recommendation, String technicalReport);
    SummaryRecommendationResponseDto get(Long id, String username);
    SummaryRecommendationResponseDto getByRecommendation(Long recommendationId, String username);
    SummaryRecommendationResponseDto update(Long id, SummaryRecommendationPostRequestDto dto, String username);
    void delete(Long id, String username);
}
