package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tabelas de Interpretação Foliar",
        description = "Endpoints para gerenciamento de tabelas de interpretação de análise foliar de culturas")
@SecurityRequirement(name = "bearerAuth")
public interface CropFoliarAnalysisInterpretationTableController {

    @Operation(summary = "Criar nova tabela de interpretação")
    @PostMapping("/register")
    ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    createCropFoliarAnalysisInterpretationTable(
            @Parameter(description = "Dados para criação da tabela de interpretação", required = true)
            @Valid @RequestBody CropFoliarAnalysisInterpretationTableCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar tabela por ID")
    @GetMapping("/get")
    ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    getCropFoliarAnalysisInterpretationTable(
            @Parameter(description = "ID da tabela de interpretação", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todas as tabelas do usuário")
    @GetMapping("/get-all")
    ResponseEntity<List<CropFoliarAnalysisInterpretationTableResponseDto>>
    getCropFoliarAnalysisInterpretationTables(
            @Parameter(description = "Grupo da tabela: PRIVADAS, PUBLICAS ou PADRAO")
            @RequestParam(name = "grupo", required = false) TechnicalTableGroup group,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todas as tabelas públicas")
    @GetMapping("/get-all-public")
    ResponseEntity<List<CropFoliarAnalysisInterpretationTableResponseDto>>
    getPublicCropFoliarAnalysisInterpretationTables(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar tabelas padrão")
    @GetMapping("/get-all-default")
    ResponseEntity<List<CropFoliarAnalysisInterpretationTableResponseDto>>
    getDefaultCropFoliarAnalysisInterpretationTables(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar tabela")
    @PutMapping("/update")
    ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    updateCropFoliarAnalysisInterpretationTable(
            @Parameter(description = "ID da tabela de interpretação a ser atualizada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para atualização da tabela", required = true)
            @Valid @RequestBody CropFoliarAnalysisInterpretationTablePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Deletar tabela")
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteCropFoliarAnalysisInterpretationTable(
            @Parameter(description = "ID da tabela de interpretação a ser removida", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );
}
