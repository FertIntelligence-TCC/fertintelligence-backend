package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "SummaryRecommendation", description = "Endpoints para gerenciamento da Recomendação Resumida")
@SecurityRequirement(name = "bearerAuth")
public interface SummaryRecommendationController {

    ResponseEntity<SummaryRecommendationResponseDto> create(
            @Valid @RequestBody SummaryRecommendationCreateRequestDto dto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SummaryRecommendationResponseDto> get(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SummaryRecommendationResponseDto> getByRecommendation(
            @RequestParam(name = "recommendationId") Long recommendationId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SummaryRecommendationResponseDto> update(
            @RequestParam(name = "id") Long id,
            @Valid @RequestBody SummaryRecommendationPostRequestDto dto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> delete(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );
}
