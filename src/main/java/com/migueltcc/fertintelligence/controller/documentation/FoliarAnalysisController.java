package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Análises Foliares", description = "Endpoints para gerenciamento de análises foliares")
@SecurityRequirement(name = "bearerAuth")
public interface FoliarAnalysisController {

    ResponseEntity<FoliarAnalysisResponseDto> createFoliarAnalysis(
            @Parameter(description = "ID da cultura para associar à análise foliar", required = true)
            @RequestParam(name = "cropId") Long cropId,
            @Parameter(description = "Dados da análise foliar a ser criada", required = true)
            @Valid @RequestBody FoliarAnalysisCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<FoliarAnalysisResponseDto> getFoliarAnalysis(
            @Parameter(description = "ID da análise foliar a ser buscada", required = true)
            @RequestParam(name = "analysisId") Long analysisId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<FoliarAnalysisResponseDto>> getFoliarAnalysesByCrop(
            @Parameter(description = "ID da cultura para listar as análises foliares", required = true)
            @RequestParam(name = "cropId") Long cropId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<FoliarAnalysisResponseDto> updateFoliarAnalysis(
            @Parameter(description = "ID da análise foliar a ser atualizada", required = true)
            @RequestParam(name = "analysisId") Long analysisId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody FoliarAnalysisPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteFoliarAnalysis(
            @Parameter(description = "ID da análise foliar a ser removida", required = true)
            @RequestParam(name = "analysisId") Long analysisId,
            @Parameter(hidden = true) Authentication authentication
    );
}