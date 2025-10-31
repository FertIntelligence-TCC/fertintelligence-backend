package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Análises de Solo", description = "Endpoints para gerenciamento de análises de solo")
@SecurityRequirement(name = "bearerAuth")
public interface SoilAnalysisController {

    ResponseEntity<SoilAnalysisResponseDto> createSoilAnalysis(
            @Parameter(description = "Dados da análise de solo a ser criada", required = true)
            @Valid @RequestBody SoilAnalysisCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SoilAnalysisResponseDto> getSoilAnalysis(
            @Parameter(description = "ID da análise de solo a ser buscada", required = true)
            @RequestParam(name = "analysisId") Long analysisId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<SoilAnalysisResponseDto>> getSoilAnalysesByPlot(
            @Parameter(description = "ID do talhão para listar as análises de solo", required = true)
            @RequestParam(name = "plotId") Long plotId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SoilAnalysisResponseDto> updateSoilAnalysis(
            @Parameter(description = "ID da análise de solo a ser atualizada", required = true)
            @RequestParam(name = "analysisId") Long analysisId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody SoilAnalysisPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteSoilAnalysis(
            @Parameter(description = "ID da análise de solo a ser removida", required = true)
            @RequestParam(name = "analysisId") Long analysisId,
            @Parameter(hidden = true) Authentication authentication
    );
}