package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose.MicronutrientDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose.MicronutrientDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose.MicronutrientDoseResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Critérios - Doses de micronutrientes", description = "Endpoints para gerenciamento das doses de B, Cu, Fe, Mn e Zn por classe de teor")
@SecurityRequirement(name = "bearerAuth")
public interface MicronutrientDoseController {

    ResponseEntity<MicronutrientDoseResponseDto> createMicronutrientDose(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação da tabela de doses", required = true)
            @Valid @RequestBody MicronutrientDoseCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<MicronutrientDoseResponseDto> getMicronutrientDose(
            @Parameter(description = "ID da tabela de doses", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<MicronutrientDoseResponseDto> getMicronutrientDoseByTable(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<MicronutrientDoseResponseDto> updateMicronutrientDose(
            @Parameter(description = "ID da tabela de doses", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(description = "Dados para atualização da tabela de doses", required = true)
            @Valid @RequestBody MicronutrientDosePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteMicronutrientDose(
            @Parameter(description = "ID da tabela de doses", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );
}
