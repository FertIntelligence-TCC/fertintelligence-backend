package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractResponseDto;
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

@Tag(name = "Extratos de Análises Físicas", description = "Endpoints para gerenciamento dos resultados de análises físicas por extrato")
@SecurityRequirement(name = "bearerAuth")
public interface PhysicalAnalysisExtractController {

    @Operation(summary = "Registrar um novo extrato de análise física")
    ResponseEntity<PhysicalAnalysisExtractResponseDto> createPhysicalAnalysisExtract(
            @Parameter(description = "ID do extrato por intervalo associado", required = false)
            @RequestParam(name = "rangeExtractId", required = false) Long rangeExtractId,
            @Parameter(description = "ID do extrato por camada associado", required = false)
            @RequestParam(name = "layerExtractId", required = false) Long layerExtractId,
            @Parameter(description = "Dados do extrato de análise física", required = true)
            @Valid @RequestBody PhysicalAnalysisExtractCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar um extrato de análise física pelo ID")
    ResponseEntity<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtract(
            @Parameter(description = "ID do extrato de análise física", required = true)
            @RequestParam(name = "physicalAnalysisExtractId") Long physicalAnalysisExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos os extratos de análise física de um talhão")
    ResponseEntity<List<PhysicalAnalysisExtractResponseDto>> getPhysicalAnalysisExtractsByPlot(
            @Parameter(description = "ID do talhão", required = true)
            @RequestParam(name = "plotId") Long plotId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar extratos de análise física por extrato de intervalo")
    ResponseEntity<List<PhysicalAnalysisExtractResponseDto>> getPhysicalAnalysisExtractsByRange(
            @Parameter(description = "ID do extrato por intervalo para listagem", required = true)
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar extratos de análise física por extrato de camada")
    ResponseEntity<List<PhysicalAnalysisExtractResponseDto>> getPhysicalAnalysisExtractsByLayer(
            @Parameter(description = "ID do extrato por camada para listagem", required = true)
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar um extrato de análise física")
    ResponseEntity<PhysicalAnalysisExtractResponseDto> updatePhysicalAnalysisExtract(
            @Parameter(description = "ID do extrato de análise física a ser atualizado", required = true)
            @RequestParam(name = "physicalAnalysisExtractId") Long physicalAnalysisExtractId,
            @Parameter(description = "Dados a serem atualizados", required = true)
            @Valid @RequestBody PhysicalAnalysisExtractPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Deletar um extrato de análise física")
    ResponseEntity<Void> deletePhysicalAnalysisExtract(
            @Parameter(description = "ID do extrato de análise física a ser removido", required = true)
            @RequestParam(name = "physicalAnalysisExtractId") Long physicalAnalysisExtractId,
            @Parameter(hidden = true) Authentication authentication
    );
}