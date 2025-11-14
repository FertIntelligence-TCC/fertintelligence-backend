package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Critérios - Salinidade", description = "Endpoints para gerenciamento dos critérios de interpretação de salinidade do solo")
@SecurityRequirement(name = "bearerAuth")
public interface SalinityInterpretationController {

    ResponseEntity<SalinityInterpretationResponseDto> createSalinityInterpretation(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação do critério", required = true)
            @Valid @RequestBody SalinityInterpretationCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SalinityInterpretationResponseDto> getSalinityInterpretation(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SalinityInterpretationResponseDto> getSalinityInterpretationByTable(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SalinityInterpretationResponseDto> updateSalinityInterpretation(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(description = "Dados para atualização do critério", required = true)
            @Valid @RequestBody SalinityInterpretationPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteSalinityInterpretation(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );
}