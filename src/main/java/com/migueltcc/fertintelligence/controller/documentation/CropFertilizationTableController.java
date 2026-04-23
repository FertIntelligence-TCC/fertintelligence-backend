package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Tabelas de Adubação", description = "Endpoints para gerenciamento de tabelas de adubação por cultura")
@SecurityRequirement(name = "bearerAuth")
public interface CropFertilizationTableController {

    ResponseEntity<CropFertilizationTableResponseDto> createCropFertilizationTable(
            @Parameter(description = "Dados para criação da tabela de adubação", required = true)
            @Valid @RequestBody CropFertilizationTableCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CropFertilizationTableResponseDto> getCropFertilizationTable(
            @Parameter(description = "ID da tabela de adubação a ser buscada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<CropFertilizationTableResponseDto>> getCropFertilizationTables(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<CropFertilizationTableResponseDto>> getPublicCropFertilizationTables(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CropFertilizationTableResponseDto> updateCropFertilizationTable(
            @Parameter(description = "ID da tabela de adubação a ser atualizada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para atualização da tabela", required = true)
            @Valid @RequestBody CropFertilizationTablePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteCropFertilizationTable(
            @Parameter(description = "ID da tabela de adubação a ser removida", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );
}