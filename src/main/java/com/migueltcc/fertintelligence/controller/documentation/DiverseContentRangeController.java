package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Critérios - Faixas de Teores Diversos", description = "Endpoints para gerenciamento das faixas de teores para diversos elementos")
@SecurityRequirement(name = "bearerAuth")
public interface DiverseContentRangeController {

    ResponseEntity<DiverseContentRangeResponseDto> createDiverseContentRange(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação do critério", required = true)
            @Valid @RequestBody DiverseContentRangeCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<DiverseContentRangeResponseDto> getDiverseContentRange(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<DiverseContentRangeResponseDto> getDiverseContentRangeByTable(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<DiverseContentRangeResponseDto> updateDiverseContentRange(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(description = "Dados para atualização do critério", required = true)
            @Valid @RequestBody DiverseContentRangePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteDiverseContentRange(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );
}