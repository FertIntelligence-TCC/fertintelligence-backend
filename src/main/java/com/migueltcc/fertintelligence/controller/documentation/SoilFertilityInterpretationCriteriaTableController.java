package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.SoilFertilityInterpretationCriteriaTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.SoilFertilityInterpretationCriteriaTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.SoilFertilityInterpretationCriteriaTableResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Tabelas de Critérios de Interpretação da Fertilidade do Solo", description = "Endpoints para gerenciamento das tabelas de critérios de interpretação da fertilidade do solo")
@SecurityRequirement(name = "bearerAuth")
public interface SoilFertilityInterpretationCriteriaTableController {

    ResponseEntity<SoilFertilityInterpretationCriteriaTableResponseDto> createSoilFertilityInterpretationCriteriaTable(
            @Parameter(description = "Dados para criação da tabela de critérios de interpretação da fertilidade do solo", required = true)
            @Valid @RequestBody SoilFertilityInterpretationCriteriaTableCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SoilFertilityInterpretationCriteriaTableResponseDto> getSoilFertilityInterpretationCriteriaTable(
            @Parameter(description = "ID da tabela de critérios de interpretação da fertilidade do solo a ser buscada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<SoilFertilityInterpretationCriteriaTableResponseDto>> getSoilFertilityInterpretationCriteriaTables(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SoilFertilityInterpretationCriteriaTableResponseDto> updateSoilFertilityInterpretationCriteriaTable(
            @Parameter(description = "ID da tabela de critérios de interpretação da fertilidade do solo a ser atualizada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para atualização da tabela", required = true)
            @Valid @RequestBody SoilFertilityInterpretationCriteriaTablePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteSoilFertilityInterpretationCriteriaTable(
            @Parameter(description = "ID da tabela de critérios de interpretação da fertilidade do solo a ser removida", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );
}