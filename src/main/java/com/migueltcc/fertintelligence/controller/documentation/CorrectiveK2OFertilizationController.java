package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Critérios - Adubação Corretiva de K2O", description = "Endpoints para gerenciamento da tabela auxiliar de adubação corretiva de K2O")
@SecurityRequirement(name = "bearerAuth")
public interface CorrectiveK2OFertilizationController {

    ResponseEntity<CorrectiveK2OFertilizationResponseDto> createCorrectiveK2OFertilization(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação da linha", required = true)
            @Valid @RequestBody CorrectiveK2OFertilizationCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CorrectiveK2OFertilizationResponseDto> getCorrectiveK2OFertilization(
            @Parameter(description = "ID da linha", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<CorrectiveK2OFertilizationResponseDto>> getCorrectiveK2OFertilizationByTable(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CorrectiveK2OFertilizationResponseDto> updateCorrectiveK2OFertilization(
            @Parameter(description = "ID da linha", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(description = "Dados para atualização da linha", required = true)
            @Valid @RequestBody CorrectiveK2OFertilizationPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteCorrectiveK2OFertilization(
            @Parameter(description = "ID da linha", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );
}
