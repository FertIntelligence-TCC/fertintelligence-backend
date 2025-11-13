package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CropFoliarAnalysisInterpretationTableLineController;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLinePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CropFoliarAnalysisInterpretationTableLineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/crop-foliar-analysis-interpretation-table-line")
public class CropFoliarAnalysisInterpretationTableLineControllerImpl
        implements CropFoliarAnalysisInterpretationTableLineController {

    @Autowired
    private CropFoliarAnalysisInterpretationTableLineService tableLineService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableLineResponseDto>
    createCropFoliarAnalysisInterpretationTableLine(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody CropFoliarAnalysisInterpretationTableLineCreateRequestDto createRequestDto,
            Authentication authentication) {

        CropFoliarAnalysisInterpretationTableLineResponseDto createdLine = tableLineService
                .createCropFoliarAnalysisInterpretationTableLine(tableId, createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/crop-foliar-analysis-interpretation-table-line/get")
                .queryParam("lineId", createdLine.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdLine);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableLineResponseDto>
    getCropFoliarAnalysisInterpretationTableLine(
            @RequestParam(name = "lineId") Long lineId,
            Authentication authentication) {

        CropFoliarAnalysisInterpretationTableLineResponseDto line = tableLineService
                .getCropFoliarAnalysisInterpretationTableLineById(lineId, authentication.getName());
        return ResponseEntity.ok(line);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<List<CropFoliarAnalysisInterpretationTableLineResponseDto>>
    getCropFoliarAnalysisInterpretationTableLinesByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {

        List<CropFoliarAnalysisInterpretationTableLineResponseDto> lines = tableLineService
                .getAllCropFoliarAnalysisInterpretationTableLinesByTable(tableId, authentication.getName());
        return ResponseEntity.ok(lines);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableLineResponseDto>
    updateCropFoliarAnalysisInterpretationTableLine(
            @RequestParam(name = "lineId") Long lineId,
            @Valid @RequestBody CropFoliarAnalysisInterpretationTableLinePostRequestDto updateRequestDto,
            Authentication authentication) {

        CropFoliarAnalysisInterpretationTableLineResponseDto updatedLine = tableLineService
                .updateCropFoliarAnalysisInterpretationTableLine(lineId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedLine);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCropFoliarAnalysisInterpretationTableLine(
            @RequestParam(name = "lineId") Long lineId,
            Authentication authentication) {

        tableLineService.deleteCropFoliarAnalysisInterpretationTableLine(lineId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}