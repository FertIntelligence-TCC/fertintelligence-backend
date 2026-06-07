package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.SoilFertilityInterpretationCriteriaTableController;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableResponseDto;
import com.migueltcc.fertintelligence.service.documentation.SoilFertilityInterpretationCriteriaTableService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/soil-fertility-interpretation-criteria-table")
@CrossOrigin(origins = "*")
public class SoilFertilityInterpretationCriteriaTableControllerImpl implements SoilFertilityInterpretationCriteriaTableController {

    private static final Logger logger = LoggerFactory.getLogger(SoilFertilityInterpretationCriteriaTableControllerImpl.class);

    @Autowired
    private SoilFertilityInterpretationCriteriaTableService soilFertilityInterpretationCriteriaTableService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<SoilFertilityInterpretationCriteriaTableResponseDto> createSoilFertilityInterpretationCriteriaTable(
            @Valid @RequestBody SoilFertilityInterpretationCriteriaTableCreateRequestDto createRequestDto,
            Authentication authentication) {

        try {
            SoilFertilityInterpretationCriteriaTableResponseDto table = soilFertilityInterpretationCriteriaTableService
                    .createSoilFertilityInterpretationCriteriaTable(createRequestDto, authentication.getName());

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(table.getId())
                    .toUri();

            return ResponseEntity.created(location).body(table);
        } catch (Exception e) {
            logger.error("Erro ao criar tabela de critérios: ", e);
            throw e;
        }
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<SoilFertilityInterpretationCriteriaTableResponseDto> getSoilFertilityInterpretationCriteriaTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        return ResponseEntity.ok(soilFertilityInterpretationCriteriaTableService
                .getSoilFertilityInterpretationCriteriaTableById(tableId, authentication.getName()));
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<SoilFertilityInterpretationCriteriaTableResponseDto>> getSoilFertilityInterpretationCriteriaTables(
            @RequestParam(name = "grupo", required = false) TechnicalTableGroup group,
            Authentication authentication) {
        List<SoilFertilityInterpretationCriteriaTableResponseDto> tables = soilFertilityInterpretationCriteriaTableService
                .getAllSoilFertilityInterpretationCriteriaTablesByCreator(authentication.getName(), group);
        return ResponseEntity.ok(tables);
    }

    @Override
    @GetMapping("/get-all-public")
    public ResponseEntity<List<SoilFertilityInterpretationCriteriaTableResponseDto>> getPublicSoilFertilityInterpretationCriteriaTables(
            Authentication authentication) {
        List<SoilFertilityInterpretationCriteriaTableResponseDto> tables = soilFertilityInterpretationCriteriaTableService
                .getAllPublicSoilFertilityInterpretationCriteriaTables();
        return ResponseEntity.ok(tables);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<SoilFertilityInterpretationCriteriaTableResponseDto> updateSoilFertilityInterpretationCriteriaTable(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody SoilFertilityInterpretationCriteriaTablePostRequestDto updateRequestDto,
            Authentication authentication) {
        SoilFertilityInterpretationCriteriaTableResponseDto updatedTable = soilFertilityInterpretationCriteriaTableService
                .updateSoilFertilityInterpretationCriteriaTable(tableId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedTable);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSoilFertilityInterpretationCriteriaTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        soilFertilityInterpretationCriteriaTableService.deleteSoilFertilityInterpretationCriteriaTable(tableId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
