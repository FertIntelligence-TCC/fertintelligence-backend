package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.RangeExtractController;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractResponseDto;
import com.migueltcc.fertintelligence.service.documentation.RangeExtractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/range-extract")
public class RangeExtractControllerImpl implements RangeExtractController {

    @Autowired
    private RangeExtractService rangeExtractService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<RangeExtractResponseDto> createRangeExtract(
            @RequestParam(name = "analysisId") Long analysisId,
            @Valid @RequestBody RangeExtractCreateRequestDto createRequestDto,
            Authentication authentication) {

        RangeExtractResponseDto createdExtract = rangeExtractService.createRangeExtract(analysisId, createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/range-extract/get")
                .queryParam("rangeExtractId", createdExtract.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdExtract);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<RangeExtractResponseDto> getRangeExtract(
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            Authentication authentication) {
        RangeExtractResponseDto extract = rangeExtractService.getRangeExtractById(rangeExtractId, authentication.getName());
        return ResponseEntity.ok(extract);
    }

    @Override
    @GetMapping("/get-by-analysis")
    public ResponseEntity<List<RangeExtractResponseDto>> getRangeExtractsByAnalysis(
            @RequestParam(name = "analysisId") Long analysisId,
            Authentication authentication) {
        List<RangeExtractResponseDto> extracts = rangeExtractService.getAllRangeExtractsByAnalysis(analysisId, authentication.getName());
        return ResponseEntity.ok(extracts);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<RangeExtractResponseDto> updateRangeExtract(
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            @Valid @RequestBody RangeExtractPostRequestDto updateRequestDto,
            Authentication authentication) {
        RangeExtractResponseDto updatedExtract = rangeExtractService.updateRangeExtract(rangeExtractId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedExtract);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteRangeExtract(
            @RequestParam(name = "rangeExtractId") Long rangeExtractId,
            Authentication authentication) {
        rangeExtractService.deleteRangeExtract(rangeExtractId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}