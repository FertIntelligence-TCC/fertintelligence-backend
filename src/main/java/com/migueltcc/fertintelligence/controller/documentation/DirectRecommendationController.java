package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "DirectRecommendation", description = "Endpoints para gerenciamento da Recomendação Direta")
@SecurityRequirement(name = "bearerAuth")
public interface DirectRecommendationController {

    ResponseEntity<DirectRecommendationResponseDto> create(
            @Valid @RequestBody DirectRecommendationCreateRequestDto dto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<DirectRecommendationResponseDto> get(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<DirectRecommendationResponseDto> getByRecommendation(
            @RequestParam(name = "recommendationId") Long recommendationId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<DirectRecommendationResponseDto> update(
            @RequestParam(name = "id") Long id,
            @Valid @RequestBody DirectRecommendationPostRequestDto dto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> delete(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );
}
