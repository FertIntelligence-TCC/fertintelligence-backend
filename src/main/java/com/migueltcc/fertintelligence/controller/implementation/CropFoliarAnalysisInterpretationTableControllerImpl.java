package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CropFoliarAnalysisInterpretationTableController;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CropFoliarAnalysisInterpretationTableService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/crop-foliar-analysis-interpretation-table")
public class CropFoliarAnalysisInterpretationTableControllerImpl
        implements CropFoliarAnalysisInterpretationTableController {

    @Autowired
    private CropFoliarAnalysisInterpretationTableService tableService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    createCropFoliarAnalysisInterpretationTable(
            @Valid @RequestBody CropFoliarAnalysisInterpretationTableCreateRequestDto createRequestDto,
            Authentication authentication) {

        CropFoliarAnalysisInterpretationTableResponseDto createdTable = tableService
                .createCropFoliarAnalysisInterpretationTable(createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/crop-foliar-analysis-interpretation-table/get")
                .queryParam("tableId", createdTable.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdTable);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    getCropFoliarAnalysisInterpretationTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {

        CropFoliarAnalysisInterpretationTableResponseDto table = tableService
                .getCropFoliarAnalysisInterpretationTableById(tableId, authentication.getName());
        return ResponseEntity.ok(table);
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<CropFoliarAnalysisInterpretationTableResponseDto>>
    getCropFoliarAnalysisInterpretationTables(Authentication authentication) {

        List<CropFoliarAnalysisInterpretationTableResponseDto> tables = tableService
                .getAllCropFoliarAnalysisInterpretationTablesByCreator(authentication.getName());
        return ResponseEntity.ok(tables);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    updateCropFoliarAnalysisInterpretationTable(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody CropFoliarAnalysisInterpretationTablePostRequestDto updateRequestDto,
            Authentication authentication) {

        CropFoliarAnalysisInterpretationTableResponseDto updatedTable = tableService
                .updateCropFoliarAnalysisInterpretationTable(tableId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedTable);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCropFoliarAnalysisInterpretationTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {

        tableService.deleteCropFoliarAnalysisInterpretationTable(tableId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}

