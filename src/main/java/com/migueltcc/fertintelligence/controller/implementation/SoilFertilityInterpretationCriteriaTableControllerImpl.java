package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.SoilFertilityInterpretationCriteriaTableController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.table.SoilFertilityInterpretationCriteriaTableResponseDto;
import com.migueltcc.fertintelligence.service.documentation.SoilFertilityInterpretationCriteriaTableService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/soil-fertility-interpretation-criteria-table")
public class SoilFertilityInterpretationCriteriaTableControllerImpl implements SoilFertilityInterpretationCriteriaTableController {

    @Autowired
    private SoilFertilityInterpretationCriteriaTableService soilFertilityInterpretationCriteriaTableService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<SoilFertilityInterpretationCriteriaTableResponseDto> createSoilFertilityInterpretationCriteriaTable(
            @Valid @RequestBody SoilFertilityInterpretationCriteriaTableCreateRequestDto createRequestDto,
            Authentication authentication) {

        SoilFertilityInterpretationCriteriaTableResponseDto createdTable = soilFertilityInterpretationCriteriaTableService
                .createSoilFertilityInterpretationCriteriaTable(createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/soil-fertility-interpretation-criteria-table/get")
                .queryParam("tableId", createdTable.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdTable);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<SoilFertilityInterpretationCriteriaTableResponseDto> getSoilFertilityInterpretationCriteriaTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        SoilFertilityInterpretationCriteriaTableResponseDto table = soilFertilityInterpretationCriteriaTableService
                .getSoilFertilityInterpretationCriteriaTableById(tableId, authentication.getName());
        return ResponseEntity.ok(table);
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<SoilFertilityInterpretationCriteriaTableResponseDto>> getSoilFertilityInterpretationCriteriaTables(
            Authentication authentication) {
        List<SoilFertilityInterpretationCriteriaTableResponseDto> tables = soilFertilityInterpretationCriteriaTableService
                .getAllSoilFertilityInterpretationCriteriaTablesByCreator(authentication.getName());
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