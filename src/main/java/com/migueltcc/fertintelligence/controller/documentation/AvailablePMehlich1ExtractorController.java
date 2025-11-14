package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Critérios - Fósforo (Extrator Mehlich-1)", description = "Endpoints para gerenciamento dos critérios de fósforo disponível com extrator Mehlich-1")
@SecurityRequirement(name = "bearerAuth")
public interface AvailablePMehlich1ExtractorController {

    ResponseEntity<AvailablePMehlich1ExtractorResponseDto> createAvailablePMehlich1Extractor(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação do critério", required = true)
            @Valid @RequestBody AvailablePMehlich1ExtractorCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<AvailablePMehlich1ExtractorResponseDto> getAvailablePMehlich1Extractor(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<AvailablePMehlich1ExtractorResponseDto> getAvailablePMehlich1ExtractorByTable(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<AvailablePMehlich1ExtractorResponseDto> updateAvailablePMehlich1Extractor(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(description = "Dados para atualização do critério", required = true)
            @Valid @RequestBody AvailablePMehlich1ExtractorPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteAvailablePMehlich1Extractor(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );
}