package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Recommendation", description = "Endpoints para geração e consulta de recomendações")
@SecurityRequirement(name = "bearerAuth")
public interface RecommendationController {
    ResponseEntity<RecommendationResponseDto> generate(
            @Valid @RequestBody RecommendationCreateRequestDto dto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<RecommendationResponseDto> get(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<RecommendationResponseDto> preparePrint(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<RecommendationResponseDto>> getMine(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<RecommendationResponseDto>> getByProperty(
            @RequestParam(name = "propertyId") Long propertyId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<RecommendationResponseDto>> getByPlot(
            @RequestParam(name = "plotId") Long plotId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> delete(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );
}
