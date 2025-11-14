package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Critérios - Fósforo (Extrator Resina)", description = "Endpoints para gerenciamento dos critérios de fósforo disponível com extrator de resina de troca aniônica")
@SecurityRequirement(name = "bearerAuth")
public interface AvailablePAnionExchangeResinExtractorController {

    ResponseEntity<AvailablePAnionExchangeResinExtractorResponseDto> createAvailablePAnionExchangeResinExtractor(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação do critério", required = true)
            @Valid @RequestBody AvailablePAnionExchangeResinExtractorCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<AvailablePAnionExchangeResinExtractorResponseDto> getAvailablePAnionExchangeResinExtractor(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<AvailablePAnionExchangeResinExtractorResponseDto> getAvailablePAnionExchangeResinExtractorByTable(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<AvailablePAnionExchangeResinExtractorResponseDto> updateAvailablePAnionExchangeResinExtractor(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(description = "Dados para atualização do critério", required = true)
            @Valid @RequestBody AvailablePAnionExchangeResinExtractorPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteAvailablePAnionExchangeResinExtractor(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );
}