package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLinePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Linhas de Interpretação Foliar",
        description = "Endpoints para gerenciamento das linhas das tabelas de interpretação de análise foliar")
@SecurityRequirement(name = "bearerAuth")
public interface CropFoliarAnalysisInterpretationTableLineController {

    @Operation(summary = "Criar nova linha na tabela")
    @PostMapping("/register")
    ResponseEntity<CropFoliarAnalysisInterpretationTableLineResponseDto>
    createCropFoliarAnalysisInterpretationTableLine(
            @Parameter(description = "ID da tabela de interpretação associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados da linha a ser criada", required = true)
            @Valid @RequestBody CropFoliarAnalysisInterpretationTableLineCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar linha por ID")
    @GetMapping("/get")
    ResponseEntity<CropFoliarAnalysisInterpretationTableLineResponseDto>
    getCropFoliarAnalysisInterpretationTableLine(
            @Parameter(description = "ID da linha a ser buscada", required = true)
            @RequestParam(name = "lineId") Long lineId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todas as linhas de uma tabela")
    @GetMapping("/get-by-table") // FIX: Nome da rota corrigido para bater com o frontend
    ResponseEntity<List<CropFoliarAnalysisInterpretationTableLineResponseDto>>
    getCropFoliarAnalysisInterpretationTableLinesByTable(
            @Parameter(description = "ID da tabela de interpretação", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar linha")
    @PutMapping("/update")
    ResponseEntity<CropFoliarAnalysisInterpretationTableLineResponseDto>
    updateCropFoliarAnalysisInterpretationTableLine(
            @Parameter(description = "ID da linha a ser atualizada", required = true)
            @RequestParam(name = "lineId") Long lineId,
            @Parameter(description = "Dados para atualização da linha", required = true)
            @Valid @RequestBody CropFoliarAnalysisInterpretationTableLinePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Deletar linha")
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteCropFoliarAnalysisInterpretationTableLine(
            @Parameter(description = "ID da linha a ser removida", required = true)
            @RequestParam(name = "lineId") Long lineId,
            @Parameter(hidden = true) Authentication authentication
    );
}