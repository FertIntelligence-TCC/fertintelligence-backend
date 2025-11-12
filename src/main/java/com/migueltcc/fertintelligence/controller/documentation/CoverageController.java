package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoveragePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Coberturas", description = "Endpoints para gerenciamento de coberturas de adubação")
@SecurityRequirement(name = "bearerAuth")
public interface CoverageController {

    ResponseEntity<CoverageResponseDto> createCoverage(
            @Parameter(description = "ID do intervalo de teor associado", required = true)
            @RequestParam(name = "contentRangeId") Long contentRangeId,
            @Parameter(description = "Dados para criação da cobertura", required = true)
            @Valid @RequestBody CoverageCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CoverageResponseDto> getCoverage(
            @Parameter(description = "ID da cobertura a ser buscada", required = true)
            @RequestParam(name = "coverageId") Long coverageId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<CoverageResponseDto>> getCoveragesByContentRange(
            @Parameter(description = "ID do intervalo de teor", required = true)
            @RequestParam(name = "contentRangeId") Long contentRangeId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CoverageResponseDto> updateCoverage(
            @Parameter(description = "ID da cobertura a ser atualizada", required = true)
            @RequestParam(name = "coverageId") Long coverageId,
            @Parameter(description = "Dados para atualização da cobertura", required = true)
            @Valid @RequestBody CoveragePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteCoverage(
            @Parameter(description = "ID da cobertura a ser removida", required = true)
            @RequestParam(name = "coverageId") Long coverageId,
            @Parameter(hidden = true) Authentication authentication
    );
}