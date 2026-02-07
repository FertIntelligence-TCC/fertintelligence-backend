package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.FertilityAnalysisExtractController;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.service.documentation.FertilityAnalysisExtractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/fertility-analysis-extract")
public class FertilityAnalysisExtractControllerImpl implements FertilityAnalysisExtractController {

    @Autowired
    private FertilityAnalysisExtractService fertilityAnalysisExtractService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<FertilityAnalysisExtractResponseDto> createFertilityAnalysisExtract(
            @RequestParam(name = "rangeExtractId", required = false) Long rangeExtractId,
            @RequestParam(name = "layerExtractId", required = false) Long layerExtractId,
            @Valid @RequestBody FertilityAnalysisExtractCreateRequestDto createRequestDto,
            Authentication authentication) {

        FertilityAnalysisExtractResponseDto createdExtract = fertilityAnalysisExtractService.createFertilityAnalysisExtract(
                rangeExtractId,
                layerExtractId,
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/fertility-analysis-extract/get")
                .queryParam("fertilityAnalysisExtractId", createdExtract.getId())
                .build().toUri();

        return ResponseEntity.created(location).body(createdExtract);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<FertilityAnalysisExtractResponseDto> getFertilityAnalysisExtract(
            @RequestParam(name = "fertilityAnalysisExtractId") Long fertilityAnalysisExtractId,
            Authentication authentication) {
        FertilityAnalysisExtractResponseDto extract = fertilityAnalysisExtractService.getFertilityAnalysisExtractById(
                fertilityAnalysisExtractId,
                authentication.getName()
        );
        return ResponseEntity.ok(extract);
    }

    // NOVO: Busca por Talhão
    @Override
    @GetMapping("/get-by-plot")
    public ResponseEntity<List<FertilityAnalysisExtractResponseDto>> getFertilityAnalysisExtractsByPlot(
            @RequestParam(name = "plotId") Long plotId,
            Authentication authentication) {
        return ResponseEntity.ok(fertilityAnalysisExtractService.getAllByPlotId(plotId, authentication.getName()));
    }

    @Override
    @GetMapping("/get-by-range")
    public ResponseEntity<List<FertilityAnalysisExtractResponseDto>> getFertilityAnalysisExtractsByRange(
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            Authentication authentication) {
        List<FertilityAnalysisExtractResponseDto> extracts = fertilityAnalysisExtractService.getFertilityAnalysisExtractsByRange(
                rangeExtractId,
                authentication.getName()
        );
        return ResponseEntity.ok(extracts);
    }

    @Override
    @GetMapping("/get-by-layer")
    public ResponseEntity<List<FertilityAnalysisExtractResponseDto>> getFertilityAnalysisExtractsByLayer(
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            Authentication authentication) {
        List<FertilityAnalysisExtractResponseDto> extracts = fertilityAnalysisExtractService.getFertilityAnalysisExtractsByLayer(
                layerExtractId,
                authentication.getName()
        );
        return ResponseEntity.ok(extracts);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<FertilityAnalysisExtractResponseDto> updateFertilityAnalysisExtract(
            @RequestParam(name = "fertilityAnalysisExtractId") Long fertilityAnalysisExtractId,
            @Valid @RequestBody FertilityAnalysisExtractPostRequestDto updateRequestDto,
            Authentication authentication) {
        FertilityAnalysisExtractResponseDto updatedExtract = fertilityAnalysisExtractService.updateFertilityAnalysisExtract(
                fertilityAnalysisExtractId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedExtract);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFertilityAnalysisExtract(
            @RequestParam(name = "fertilityAnalysisExtractId") Long fertilityAnalysisExtractId,
            Authentication authentication) {
        fertilityAnalysisExtractService.deleteFertilityAnalysisExtract(
                fertilityAnalysisExtractId,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }
}