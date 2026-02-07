package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.SaturationExtractAnalysisExtractController;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.service.documentation.SaturationExtractAnalysisExtractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/saturation-extract-analysis-extract")
public class SaturationExtractAnalysisExtractControllerImpl implements SaturationExtractAnalysisExtractController {

    @Autowired
    private SaturationExtractAnalysisExtractService saturationExtractAnalysisExtractService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<SaturationExtractAnalysisExtractResponseDto> createSaturationExtractAnalysisExtract(
            @RequestParam(name = "rangeExtractId", required = false) Long rangeExtractId,
            @RequestParam(name = "layerExtractId", required = false) Long layerExtractId,
            @Valid @RequestBody SaturationExtractAnalysisExtractCreateRequestDto createRequestDto,
            Authentication authentication) {

        SaturationExtractAnalysisExtractResponseDto createdExtract = saturationExtractAnalysisExtractService.createSaturationExtractAnalysisExtract(
                rangeExtractId,
                layerExtractId,
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/saturation-extract-analysis-extract/get")
                .queryParam("saturationExtractAnalysisExtractId", createdExtract.getId())
                .build().toUri();

        return ResponseEntity.created(location).body(createdExtract);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<SaturationExtractAnalysisExtractResponseDto> getSaturationExtractAnalysisExtract(
            @RequestParam(name = "saturationExtractAnalysisExtractId") Long saturationExtractAnalysisExtractId,
            Authentication authentication) {
        SaturationExtractAnalysisExtractResponseDto extract = saturationExtractAnalysisExtractService.getSaturationExtractAnalysisExtractById(
                saturationExtractAnalysisExtractId,
                authentication.getName()
        );
        return ResponseEntity.ok(extract);
    }

    // NOVO: Busca por Talhão
    @Override
    @GetMapping("/get-by-plot")
    public ResponseEntity<List<SaturationExtractAnalysisExtractResponseDto>> getSaturationExtractAnalysisExtractsByPlot(
            @RequestParam(name = "plotId") Long plotId,
            Authentication authentication) {
        return ResponseEntity.ok(saturationExtractAnalysisExtractService.getAllByPlotId(plotId, authentication.getName()));
    }

    @Override
    @GetMapping("/get-by-range")
    public ResponseEntity<List<SaturationExtractAnalysisExtractResponseDto>> getSaturationExtractAnalysisExtractsByRange(
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            Authentication authentication) {
        List<SaturationExtractAnalysisExtractResponseDto> extracts = saturationExtractAnalysisExtractService.getSaturationExtractAnalysisExtractsByRange(
                rangeExtractId,
                authentication.getName()
        );
        return ResponseEntity.ok(extracts);
    }

    @Override
    @GetMapping("/get-by-layer")
    public ResponseEntity<List<SaturationExtractAnalysisExtractResponseDto>> getSaturationExtractAnalysisExtractsByLayer(
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            Authentication authentication) {
        List<SaturationExtractAnalysisExtractResponseDto> extracts = saturationExtractAnalysisExtractService.getSaturationExtractAnalysisExtractsByLayer(
                layerExtractId,
                authentication.getName()
        );
        return ResponseEntity.ok(extracts);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<SaturationExtractAnalysisExtractResponseDto> updateSaturationExtractAnalysisExtract(
            @RequestParam(name = "saturationExtractAnalysisExtractId") Long saturationExtractAnalysisExtractId,
            @Valid @RequestBody SaturationExtractAnalysisExtractPostRequestDto updateRequestDto,
            Authentication authentication) {
        SaturationExtractAnalysisExtractResponseDto updatedExtract = saturationExtractAnalysisExtractService.updateSaturationExtractAnalysisExtract(
                saturationExtractAnalysisExtractId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedExtract);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSaturationExtractAnalysisExtract(
            @RequestParam(name = "saturationExtractAnalysisExtractId") Long saturationExtractAnalysisExtractId,
            Authentication authentication) {
        saturationExtractAnalysisExtractService.deleteSaturationExtractAnalysisExtract(
                saturationExtractAnalysisExtractId,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }
}