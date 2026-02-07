package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Extratos de Análises de Fertilidade", description = "Endpoints para gerenciamento dos resultados de análises de fertilidade por extrato")
@SecurityRequirement(name = "bearerAuth")
public interface FertilityAnalysisExtractController {

    @Operation(summary = "Registrar um novo extrato de análise de fertilidade")
    ResponseEntity<FertilityAnalysisExtractResponseDto> createFertilityAnalysisExtract(
            @Parameter(description = "ID do extrato por intervalo associado", required = false)
            @RequestParam(name = "rangeExtractId", required = false) Long rangeExtractId,
            @Parameter(description = "ID do extrato por camada associado", required = false)
            @RequestParam(name = "layerExtractId", required = false) Long layerExtractId,
            @Parameter(description = "Dados do extrato de análise de fertilidade", required = true)
            @Valid @RequestBody FertilityAnalysisExtractCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar um extrato de análise de fertilidade pelo ID")
    ResponseEntity<FertilityAnalysisExtractResponseDto> getFertilityAnalysisExtract(
            @Parameter(description = "ID do extrato de análise de fertilidade", required = true)
            @RequestParam(name = "fertilityAnalysisExtractId") Long fertilityAnalysisExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos os extratos de análise de fertilidade de um talhão")
    ResponseEntity<List<FertilityAnalysisExtractResponseDto>> getFertilityAnalysisExtractsByPlot(
            @Parameter(description = "ID do talhão", required = true)
            @RequestParam(name = "plotId") Long plotId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar extratos de análise de fertilidade por extrato de intervalo")
    ResponseEntity<List<FertilityAnalysisExtractResponseDto>> getFertilityAnalysisExtractsByRange(
            @Parameter(description = "ID do extrato por intervalo para listagem", required = true)
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar extratos de análise de fertilidade por extrato de camada")
    ResponseEntity<List<FertilityAnalysisExtractResponseDto>> getFertilityAnalysisExtractsByLayer(
            @Parameter(description = "ID do extrato por camada para listagem", required = true)
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar um extrato de análise de fertilidade")
    ResponseEntity<FertilityAnalysisExtractResponseDto> updateFertilityAnalysisExtract(
            @Parameter(description = "ID do extrato de análise de fertilidade a ser atualizado", required = true)
            @RequestParam(name = "fertilityAnalysisExtractId") Long fertilityAnalysisExtractId,
            @Parameter(description = "Dados a serem atualizados", required = true)
            @Valid @RequestBody FertilityAnalysisExtractPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Deletar um extrato de análise de fertilidade")
    ResponseEntity<Void> deleteFertilityAnalysisExtract(
            @Parameter(description = "ID do extrato de análise de fertilidade a ser removido", required = true)
            @RequestParam(name = "fertilityAnalysisExtractId") Long fertilityAnalysisExtractId,
            @Parameter(hidden = true) Authentication authentication
    );
}