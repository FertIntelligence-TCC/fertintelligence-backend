package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Critérios - Adubação Corretiva de P2O5", description = "Endpoints para gerenciamento da tabela auxiliar de adubação corretiva de P2O5")
@SecurityRequirement(name = "bearerAuth")
public interface CorrectiveP2O5FertilizationController {

    ResponseEntity<CorrectiveP2O5FertilizationResponseDto> createCorrectiveP2O5Fertilization(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação da linha", required = true)
            @Valid @RequestBody CorrectiveP2O5FertilizationCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CorrectiveP2O5FertilizationResponseDto> getCorrectiveP2O5Fertilization(
            @Parameter(description = "ID da linha", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<CorrectiveP2O5FertilizationResponseDto>> getCorrectiveP2O5FertilizationByTable(
            @Parameter(description = "ID da tabela associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CorrectiveP2O5FertilizationResponseDto> updateCorrectiveP2O5Fertilization(
            @Parameter(description = "ID da linha", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(description = "Dados para atualização da linha", required = true)
            @Valid @RequestBody CorrectiveP2O5FertilizationPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteCorrectiveP2O5Fertilization(
            @Parameter(description = "ID da linha", required = true)
            @RequestParam(name = "criterionId") Long criterionId,
            @Parameter(hidden = true) Authentication authentication
    );
}
