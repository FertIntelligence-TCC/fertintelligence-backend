package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CropFertilizationTableController;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CropFertilizationTableService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/crop-fertilization-table")
public class CropFertilizationTableControllerImpl implements CropFertilizationTableController {

    @Autowired
    private CropFertilizationTableService cropFertilizationTableService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<CropFertilizationTableResponseDto> createCropFertilizationTable(
            @Valid @RequestBody CropFertilizationTableCreateRequestDto createRequestDto,
            Authentication authentication) {

        CropFertilizationTableResponseDto createdTable = cropFertilizationTableService
                .createCropFertilizationTable(createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/crop-fertilization-table/get")
                .queryParam("tableId", createdTable.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdTable);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CropFertilizationTableResponseDto> getCropFertilizationTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        CropFertilizationTableResponseDto table = cropFertilizationTableService
                .getCropFertilizationTableById(tableId, authentication.getName());
        return ResponseEntity.ok(table);
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<CropFertilizationTableResponseDto>> getCropFertilizationTables(Authentication authentication) {
        List<CropFertilizationTableResponseDto> tables = cropFertilizationTableService
                .getAllCropFertilizationTablesByCreator(authentication.getName());
        return ResponseEntity.ok(tables);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CropFertilizationTableResponseDto> updateCropFertilizationTable(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody CropFertilizationTablePostRequestDto updateRequestDto,
            Authentication authentication) {
        CropFertilizationTableResponseDto updatedTable = cropFertilizationTableService
                .updateCropFertilizationTable(tableId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedTable);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCropFertilizationTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        cropFertilizationTableService.deleteCropFertilizationTable(tableId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}