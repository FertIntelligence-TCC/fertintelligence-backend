package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractResponseDto;
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

@Tag(name = "Extratos de Saturação do Extrato", description = "Endpoints para gerenciamento dos resultados de análises de saturação por extrato")
@SecurityRequirement(name = "bearerAuth")
public interface SaturationExtractAnalysisExtractController {

    @Operation(summary = "Registrar um novo extrato de análise de saturação")
    ResponseEntity<SaturationExtractAnalysisExtractResponseDto> createSaturationExtractAnalysisExtract(
            @Parameter(description = "ID do extrato por intervalo associado", required = false)
            @RequestParam(name = "rangeExtractId", required = false) Long rangeExtractId,
            @Parameter(description = "ID do extrato por camada associado", required = false)
            @RequestParam(name = "layerExtractId", required = false) Long layerExtractId,
            @Parameter(description = "Dados do extrato de análise de saturação", required = true)
            @Valid @RequestBody SaturationExtractAnalysisExtractCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar um extrato de análise de saturação pelo ID")
    ResponseEntity<SaturationExtractAnalysisExtractResponseDto> getSaturationExtractAnalysisExtract(
            @Parameter(description = "ID do extrato de análise de saturação", required = true)
            @RequestParam(name = "saturationExtractAnalysisExtractId") Long saturationExtractAnalysisExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos os extratos de análise de saturação de um talhão")
    ResponseEntity<List<SaturationExtractAnalysisExtractResponseDto>> getSaturationExtractAnalysisExtractsByPlot(
            @Parameter(description = "ID do talhão", required = true)
            @RequestParam(name = "plotId") Long plotId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar extratos de análise de saturação por extrato de intervalo")
    ResponseEntity<List<SaturationExtractAnalysisExtractResponseDto>> getSaturationExtractAnalysisExtractsByRange(
            @Parameter(description = "ID do extrato por intervalo para listagem", required = true)
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar extratos de análise de saturação por extrato de camada")
    ResponseEntity<List<SaturationExtractAnalysisExtractResponseDto>> getSaturationExtractAnalysisExtractsByLayer(
            @Parameter(description = "ID do extrato por camada para listagem", required = true)
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar um extrato de análise de saturação")
    ResponseEntity<SaturationExtractAnalysisExtractResponseDto> updateSaturationExtractAnalysisExtract(
            @Parameter(description = "ID do extrato de análise de saturação a ser atualizado", required = true)
            @RequestParam(name = "saturationExtractAnalysisExtractId") Long saturationExtractAnalysisExtractId,
            @Parameter(description = "Dados a serem atualizados", required = true)
            @Valid @RequestBody SaturationExtractAnalysisExtractPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Deletar um extrato de análise de saturação")
    ResponseEntity<Void> deleteSaturationExtractAnalysisExtract(
            @Parameter(description = "ID do extrato de análise de saturação a ser removido", required = true)
            @RequestParam(name = "saturationExtractAnalysisExtractId") Long saturationExtractAnalysisExtractId,
            @Parameter(hidden = true) Authentication authentication
    );
}