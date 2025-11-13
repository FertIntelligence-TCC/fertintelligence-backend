package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Tabelas de Interpretação Foliar",
        description = "Endpoints para gerenciamento de tabelas de interpretação de análise foliar de culturas")
@SecurityRequirement(name = "bearerAuth")
public interface CropFoliarAnalysisInterpretationTableController {

    ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    createCropFoliarAnalysisInterpretationTable(
            @Parameter(description = "Dados para criação da tabela de interpretação", required = true)
            @Valid @RequestBody CropFoliarAnalysisInterpretationTableCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    getCropFoliarAnalysisInterpretationTable(
            @Parameter(description = "ID da tabela de interpretação", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<CropFoliarAnalysisInterpretationTableResponseDto>>
    getCropFoliarAnalysisInterpretationTables(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    updateCropFoliarAnalysisInterpretationTable(
            @Parameter(description = "ID da tabela de interpretação a ser atualizada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para atualização da tabela", required = true)
            @Valid @RequestBody CropFoliarAnalysisInterpretationTablePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteCropFoliarAnalysisInterpretationTable(
            @Parameter(description = "ID da tabela de interpretação a ser removida", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );
}