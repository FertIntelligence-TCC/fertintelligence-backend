package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CoverageController;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoveragePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CoverageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/coverage")
public class CoverageControllerImpl implements CoverageController {

    @Autowired
    private CoverageService coverageService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<CoverageResponseDto> createCoverage(
            @RequestParam(name = "contentRangeId") Long contentRangeId,
            @Valid @RequestBody CoverageCreateRequestDto createRequestDto,
            Authentication authentication) {

        CoverageResponseDto createdCoverage = coverageService
                .createCoverage(contentRangeId, createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/coverage/get")
                .queryParam("coverageId", createdCoverage.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCoverage);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CoverageResponseDto> getCoverage(
            @RequestParam(name = "coverageId") Long coverageId,
            Authentication authentication) {
        CoverageResponseDto coverage = coverageService
                .getCoverageById(coverageId, authentication.getName());
        return ResponseEntity.ok(coverage);
    }

    @Override
    @GetMapping("/get-by-range")
    public ResponseEntity<List<CoverageResponseDto>> getCoveragesByContentRange(
            @RequestParam(name = "contentRangeId") Long contentRangeId,
            Authentication authentication) {
        List<CoverageResponseDto> coverages = coverageService
                .getAllCoveragesByContentRange(contentRangeId, authentication.getName());
        return ResponseEntity.ok(coverages);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CoverageResponseDto> updateCoverage(
            @RequestParam(name = "coverageId") Long coverageId,
            @Valid @RequestBody CoveragePostRequestDto updateRequestDto,
            Authentication authentication) {
        CoverageResponseDto updatedCoverage = coverageService
                .updateCoverage(coverageId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedCoverage);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCoverage(
            @RequestParam(name = "coverageId") Long coverageId,
            Authentication authentication) {
        coverageService.deleteCoverage(coverageId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}