package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.DirectRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.service.implementation.RecommendationEngine.RecommendationCalculationService;

import java.util.List;

public interface DirectRecommendationService {
    DirectRecommendationResponseDto create(DirectRecommendationCreateRequestDto dto, String username);
    DirectRecommendationModel createInitial(RecommendationModel recommendation, String technicalReport);
    DirectRecommendationModel createInitial(
            RecommendationModel recommendation,
            String technicalReport,
            List<RecommendationCalculationService.MicronutrientFertilizerRecommendationRow> micronutrientFertilizerRows);
    DirectRecommendationResponseDto get(Long id, String username);
    DirectRecommendationResponseDto getByRecommendation(Long recommendationId, String username);
    DirectRecommendationResponseDto update(Long id, DirectRecommendationPostRequestDto dto, String username);
    void delete(Long id, String username);
}
