package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CropFertilizationTableController;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CropFertilizationTableService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/crop-fertilization-table")
public class CropFertilizationTableControllerImpl implements CropFertilizationTableController {

    private final CropFertilizationTableService cropFertilizationTableService;

    public CropFertilizationTableControllerImpl(CropFertilizationTableService cropFertilizationTableService) {
        this.cropFertilizationTableService = cropFertilizationTableService;
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<CropFertilizationTableResponseDto> createCropFertilizationTable(
            @Valid @RequestBody CropFertilizationTableCreateRequestDto createRequestDto,
            Authentication authentication
    ) {
        CropFertilizationTableResponseDto created = cropFertilizationTableService
                .createCropFertilizationTable(createRequestDto, authentication.getName());

        // Mantém exatamente o formato que o teste espera:
        // http://localhost/crop-fertilization-table/get?tableId=20
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/crop-fertilization-table/get")
                .queryParam("tableId", created.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CropFertilizationTableResponseDto> getCropFertilizationTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication
    ) {
        CropFertilizationTableResponseDto table = cropFertilizationTableService
                .getCropFertilizationTableById(tableId, authentication.getName());

        return ResponseEntity.ok(table);
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<CropFertilizationTableResponseDto>> getCropFertilizationTables(
            Authentication authentication
    ) {
        List<CropFertilizationTableResponseDto> tables = cropFertilizationTableService
                .getAllCropFertilizationTables(authentication.getName());

        return ResponseEntity.ok(tables);
    }

    @Override
    @GetMapping("/get-all-public")
    public ResponseEntity<List<CropFertilizationTableResponseDto>> getPublicCropFertilizationTables(
            Authentication authentication
    ) {
        List<CropFertilizationTableResponseDto> tables = cropFertilizationTableService
                .getAllPublicCropFertilizationTables();
        return ResponseEntity.ok(tables);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CropFertilizationTableResponseDto> updateCropFertilizationTable(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody CropFertilizationTablePostRequestDto updateRequestDto,
            Authentication authentication
    ) {
        CropFertilizationTableResponseDto updated = cropFertilizationTableService
                .updateCropFertilizationTable(tableId, updateRequestDto, authentication.getName());

        return ResponseEntity.ok(updated);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCropFertilizationTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication
    ) {
        cropFertilizationTableService.deleteCropFertilizationTable(tableId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
