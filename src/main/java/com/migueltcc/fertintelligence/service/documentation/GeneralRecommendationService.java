package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.GeneralRecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;

public interface GeneralRecommendationService {
    GeneralRecommendationResponseDto create(GeneralRecommendationCreateRequestDto dto, String username);
    GeneralRecommendationModel createInitial(RecommendationModel recommendation, String technicalReport);
    GeneralRecommendationResponseDto get(Long id, String username);
    GeneralRecommendationResponseDto getByRecommendation(Long recommendationId, String username);
    GeneralRecommendationResponseDto update(Long id, GeneralRecommendationPostRequestDto dto, String username);
    void delete(Long id, String username);
}
