package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Extratos por Intervalo", description = "Endpoints para gerenciamento de extratos por intervalo")
@SecurityRequirement(name = "bearerAuth")
public interface RangeExtractController {

    ResponseEntity<RangeExtractResponseDto> createRangeExtract(
            @Parameter(description = "ID da análise de solo para criação do extrato", required = true)
            @RequestParam(name = "analysisId") Long analysisId,
            @Parameter(description = "Dados do extrato de intervalo a ser criado", required = true)
            @Valid @RequestBody RangeExtractCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<RangeExtractResponseDto> getRangeExtract(
            @Parameter(description = "ID do extrato de intervalo a ser buscado", required = true)
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<RangeExtractResponseDto>> getRangeExtractsByAnalysis(
            @Parameter(description = "ID da análise de solo para listar os extratos", required = true)
            @RequestParam(name = "analysisId") Long analysisId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<RangeExtractResponseDto> updateRangeExtract(
            @Parameter(description = "ID do extrato de intervalo a ser atualizado", required = true)
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            @Parameter(description = "Dados para atualização do extrato", required = true)
            @Valid @RequestBody RangeExtractPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteRangeExtract(
            @Parameter(description = "ID do extrato de intervalo a ser removido", required = true)
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            @Parameter(hidden = true) Authentication authentication
    );
}