package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDoseResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Critérios - Doses de S", description = "Endpoints para gerenciamento das doses de enxofre por classe de S disponível")
@SecurityRequirement(name = "bearerAuth")
public interface SulfurDoseController {

    ResponseEntity<SulfurDoseResponseDto> createSulfurDose(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação da tabela de doses", required = true)
            @Valid @RequestBody SulfurDoseCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SulfurDoseResponseDto> getSulfurDose(
            @Parameter(description = "ID da tabela de doses", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SulfurDoseResponseDto> getSulfurDoseByTable(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SulfurDoseResponseDto> updateSulfurDose(
            @Parameter(description = "ID da tabela de doses", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(description = "Dados para atualização da tabela de doses", required = true)
            @Valid @RequestBody SulfurDosePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteSulfurDose(
            @Parameter(description = "ID da tabela de doses", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );
}
