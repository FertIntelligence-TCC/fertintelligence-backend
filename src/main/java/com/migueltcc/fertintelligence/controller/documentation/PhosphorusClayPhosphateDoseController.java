package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDoseResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Critérios - Teores de Fósforo e Argila, e Doses de Fosfato", description = "Endpoints para gerenciamento da tabela auxiliar de teores de fósforo e argila, e doses de fosfato")
@SecurityRequirement(name = "bearerAuth")
public interface PhosphorusClayPhosphateDoseController {

    ResponseEntity<PhosphorusClayPhosphateDoseResponseDto> createPhosphorusClayPhosphateDose(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação do critério", required = true)
            @Valid @RequestBody PhosphorusClayPhosphateDoseCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<PhosphorusClayPhosphateDoseResponseDto> getPhosphorusClayPhosphateDose(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<PhosphorusClayPhosphateDoseResponseDto> getPhosphorusClayPhosphateDoseByTable(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<PhosphorusClayPhosphateDoseResponseDto> updatePhosphorusClayPhosphateDose(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(description = "Dados para atualização do critério", required = true)
            @Valid @RequestBody PhosphorusClayPhosphateDosePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deletePhosphorusClayPhosphateDose(
            @Parameter(description = "ID do critério", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );
}
