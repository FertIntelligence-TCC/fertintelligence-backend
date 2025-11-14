package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Critérios - Enxofre", description = "Endpoints para gerenciamento dos critérios de interpretação de enxofre disponível")
@SecurityRequirement(name = "bearerAuth")
public interface AvailableSController {

    ResponseEntity<AvailableSResponseDto> createAvailableS(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação do critério", required = true)
            @Valid @RequestBody AvailableSCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<AvailableSResponseDto> getAvailableS(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<AvailableSResponseDto> getAvailableSByTable(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<AvailableSResponseDto> updateAvailableS(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(description = "Dados para atualização do critério", required = true)
            @Valid @RequestBody AvailableSPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteAvailableS(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );
}