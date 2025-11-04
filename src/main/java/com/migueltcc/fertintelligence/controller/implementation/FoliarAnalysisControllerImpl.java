package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.FoliarAnalysisController;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisResponseDto;
import com.migueltcc.fertintelligence.service.documentation.FoliarAnalysisService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/foliar-analysis")
public class FoliarAnalysisControllerImpl implements FoliarAnalysisController {

    @Autowired
    private FoliarAnalysisService foliarAnalysisService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<FoliarAnalysisResponseDto> createFoliarAnalysis(
            @RequestParam(name = "cropId") Long cropId,
            @Valid @RequestBody FoliarAnalysisCreateRequestDto createRequestDto,
            Authentication authentication) {

        FoliarAnalysisResponseDto createdAnalysis = foliarAnalysisService.createFoliarAnalysis(
                cropId,
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/foliar-analysis/get")
                .queryParam("analysisId", createdAnalysis.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdAnalysis);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<FoliarAnalysisResponseDto> getFoliarAnalysis(
            @RequestParam(name = "analysisId") Long analysisId,
            Authentication authentication) {
        FoliarAnalysisResponseDto analysis = foliarAnalysisService.getFoliarAnalysisById(analysisId, authentication.getName());
        return ResponseEntity.ok(analysis);
    }

    @Override
    @GetMapping("/get-by-crop")
    public ResponseEntity<List<FoliarAnalysisResponseDto>> getFoliarAnalysesByCrop(
            @RequestParam(name = "cropId") Long cropId,
            Authentication authentication) {
        List<FoliarAnalysisResponseDto> analyses = foliarAnalysisService.getAllFoliarAnalysesByCrop(cropId, authentication.getName());
        return ResponseEntity.ok(analyses);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<FoliarAnalysisResponseDto> updateFoliarAnalysis(
            @RequestParam(name = "analysisId") Long analysisId,
            @Valid @RequestBody FoliarAnalysisPostRequestDto updateRequestDto,
            Authentication authentication) {
        FoliarAnalysisResponseDto updatedAnalysis = foliarAnalysisService.updateFoliarAnalysis(
                analysisId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedAnalysis);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFoliarAnalysis(
            @RequestParam(name = "analysisId") Long analysisId,
            Authentication authentication) {
        foliarAnalysisService.deleteFoliarAnalysis(analysisId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
