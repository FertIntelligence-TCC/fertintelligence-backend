package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "GeneralRecommendation", description = "Endpoints para gerenciamento da Recomendação Geral")
@SecurityRequirement(name = "bearerAuth")
public interface GeneralRecommendationController {

    ResponseEntity<GeneralRecommendationResponseDto> create(
            @Valid @RequestBody GeneralRecommendationCreateRequestDto dto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<GeneralRecommendationResponseDto> get(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<GeneralRecommendationResponseDto> getByRecommendation(
            @RequestParam(name = "recommendationId") Long recommendationId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<GeneralRecommendationResponseDto> update(
            @RequestParam(name = "id") Long id,
            @Valid @RequestBody GeneralRecommendationPostRequestDto dto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> delete(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );
}
