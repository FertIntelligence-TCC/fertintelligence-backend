package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.SoilAnalysisController;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisResponseDto;
import com.migueltcc.fertintelligence.service.documentation.SoilAnalysisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/soil-analysis")
public class SoilAnalysisControllerImpl implements SoilAnalysisController {

    @Autowired
    private SoilAnalysisService soilAnalysisService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<SoilAnalysisResponseDto> createSoilAnalysis(
            @Valid @RequestBody SoilAnalysisCreateRequestDto createRequestDto,
            Authentication authentication) {

        SoilAnalysisResponseDto createdAnalysis = soilAnalysisService.createSoilAnalysis(createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/soil-analysis/get")
                .queryParam("analysisId", createdAnalysis.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdAnalysis);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<SoilAnalysisResponseDto> getSoilAnalysis(
            @RequestParam(name = "analysisId") Long analysisId,
            Authentication authentication) {
        SoilAnalysisResponseDto analysis = soilAnalysisService.getSoilAnalysisById(analysisId, authentication.getName());
        return ResponseEntity.ok(analysis);
    }

    @Override
    @GetMapping("/get-by-plot")
    public ResponseEntity<List<SoilAnalysisResponseDto>> getSoilAnalysesByPlot(
            @RequestParam(name = "plotId") Long plotId,
            Authentication authentication) {
        List<SoilAnalysisResponseDto> analyses = soilAnalysisService.getAllSoilAnalysesByPlot(plotId, authentication.getName());
        return ResponseEntity.ok(analyses);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<SoilAnalysisResponseDto> updateSoilAnalysis(
            @RequestParam(name = "analysisId") Long analysisId,
            @Valid @RequestBody SoilAnalysisPostRequestDto updateRequestDto,
            Authentication authentication) {
        SoilAnalysisResponseDto updatedAnalysis = soilAnalysisService.updateSoilAnalysis(analysisId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedAnalysis);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSoilAnalysis(
            @RequestParam(name = "analysisId") Long analysisId,
            Authentication authentication) {
        soilAnalysisService.deleteSoilAnalysis(analysisId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}