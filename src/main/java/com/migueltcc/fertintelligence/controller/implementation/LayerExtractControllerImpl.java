package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.LayerExtractController;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractResponseDto;
import com.migueltcc.fertintelligence.service.documentation.LayerExtractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/layer-extract")
public class LayerExtractControllerImpl implements LayerExtractController {

    @Autowired
    private LayerExtractService layerExtractService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<LayerExtractResponseDto> createLayerExtract(
            @RequestParam(name = "analysisId") Long analysisId,
            @Valid @RequestBody LayerExtractCreateRequestDto createRequestDto,
            Authentication authentication) {

        LayerExtractResponseDto createdExtract = layerExtractService.createLayerExtract(analysisId, createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/layer-extract/get")
                .queryParam("layerExtractId", createdExtract.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdExtract);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<LayerExtractResponseDto> getLayerExtract(
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            Authentication authentication) {
        LayerExtractResponseDto extract = layerExtractService.getLayerExtractById(layerExtractId, authentication.getName());
        return ResponseEntity.ok(extract);
    }

    @Override
    @GetMapping("/get-by-analysis")
    public ResponseEntity<List<LayerExtractResponseDto>> getLayerExtractsByAnalysis(
            @RequestParam(name = "analysisId") Long analysisId,
            Authentication authentication) {
        List<LayerExtractResponseDto> extracts = layerExtractService.getAllLayerExtractsByAnalysis(analysisId, authentication.getName());
        return ResponseEntity.ok(extracts);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<LayerExtractResponseDto> updateLayerExtract(
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            @Valid @RequestBody LayerExtractPostRequestDto updateRequestDto,
            Authentication authentication) {
        LayerExtractResponseDto updatedExtract = layerExtractService.updateLayerExtract(layerExtractId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedExtract);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteLayerExtract(
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            Authentication authentication) {
        layerExtractService.deleteLayerExtract(layerExtractId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}