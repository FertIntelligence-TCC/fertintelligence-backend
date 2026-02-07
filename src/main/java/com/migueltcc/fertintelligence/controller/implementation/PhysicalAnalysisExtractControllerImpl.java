package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.PhysicalAnalysisExtractController;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.service.documentation.PhysicalAnalysisExtractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

    @RestController
    @RequestMapping("/physical-analysis-extract")
    public class PhysicalAnalysisExtractControllerImpl implements PhysicalAnalysisExtractController {

        @Autowired
        private PhysicalAnalysisExtractService physicalAnalysisExtractService;

        @Override
        @PostMapping("/register")
        public ResponseEntity<PhysicalAnalysisExtractResponseDto> createPhysicalAnalysisExtract(
                @RequestParam(name = "rangeExtractId", required = false) Long rangeExtractId,
                @RequestParam(name = "layerExtractId", required = false) Long layerExtractId,
                @Valid @RequestBody PhysicalAnalysisExtractCreateRequestDto createRequestDto,
                Authentication authentication) {

            PhysicalAnalysisExtractResponseDto createdExtract = physicalAnalysisExtractService.createPhysicalAnalysisExtract(
                    rangeExtractId,
                    layerExtractId,
                    createRequestDto,
                    authentication.getName()
            );

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/physical-analysis-extract/get")
                    .queryParam("physicalAnalysisExtractId", createdExtract.getId())
                    .build()
                    .toUri();

            return ResponseEntity.created(location).body(createdExtract);
        }

        @Override
        @GetMapping("/get")
        public ResponseEntity<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtract(
                @RequestParam(name = "physicalAnalysisExtractId") Long physicalAnalysisExtractId,
                Authentication authentication) {
            PhysicalAnalysisExtractResponseDto extract = physicalAnalysisExtractService.getPhysicalAnalysisExtractById(
                    physicalAnalysisExtractId,
                    authentication.getName()
            );
            return ResponseEntity.ok(extract);
        }

        @Override
        @GetMapping("/get-by-range")
        public ResponseEntity<List<PhysicalAnalysisExtractResponseDto>> getPhysicalAnalysisExtractsByRange(
                @RequestParam(name = "rangeExtractId") Long rangeExtractId,
                Authentication authentication) {
            List<PhysicalAnalysisExtractResponseDto> extracts = physicalAnalysisExtractService.getPhysicalAnalysisExtractsByRange(
                    rangeExtractId,
                    authentication.getName()
            );
            return ResponseEntity.ok(extracts);
        }

        @Override
        @GetMapping("/get-by-layer")
        public ResponseEntity<List<PhysicalAnalysisExtractResponseDto>> getPhysicalAnalysisExtractsByLayer(
                @RequestParam(name = "layerExtractId") Long layerExtractId,
                Authentication authentication) {
            List<PhysicalAnalysisExtractResponseDto> extracts = physicalAnalysisExtractService.getPhysicalAnalysisExtractsByLayer(
                    layerExtractId,
                    authentication.getName()
            );
            return ResponseEntity.ok(extracts);
        }

        @Override
        @PutMapping("/update")
        public ResponseEntity<PhysicalAnalysisExtractResponseDto> updatePhysicalAnalysisExtract(
                @RequestParam(name = "physicalAnalysisExtractId") Long physicalAnalysisExtractId,
                @Valid @RequestBody PhysicalAnalysisExtractPostRequestDto updateRequestDto,
                Authentication authentication) {
            PhysicalAnalysisExtractResponseDto updatedExtract = physicalAnalysisExtractService.updatePhysicalAnalysisExtract(
                    physicalAnalysisExtractId,
                    updateRequestDto,
                    authentication.getName()
            );
            return ResponseEntity.ok(updatedExtract);
        }

        @Override
        @DeleteMapping("/delete")
        public ResponseEntity<Void> deletePhysicalAnalysisExtract(
                @RequestParam(name = "physicalAnalysisExtractId") Long physicalAnalysisExtractId,
                Authentication authentication) {
            physicalAnalysisExtractService.deletePhysicalAnalysisExtract(
                    physicalAnalysisExtractId,
                    authentication.getName()
            );
            return ResponseEntity.noContent().build();
        }
    }