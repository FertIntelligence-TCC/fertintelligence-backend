package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.SalinityInterpretationController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.salinityInterpretation.SalinityInterpretationResponseDto;
import com.migueltcc.fertintelligence.service.documentation.SalinityInterpretationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/salinity-interpretation")
public class SalinityInterpretationControllerImpl implements SalinityInterpretationController {

    @Autowired
    private SalinityInterpretationService salinityInterpretationService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<SalinityInterpretationResponseDto> createSalinityInterpretation(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody SalinityInterpretationCreateRequestDto createRequestDto,
            Authentication authentication) {

        SalinityInterpretationResponseDto createdCriterion = salinityInterpretationService.createSalinityInterpretation(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/salinity-interpretation/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<SalinityInterpretationResponseDto> getSalinityInterpretation(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        SalinityInterpretationResponseDto criterion = salinityInterpretationService.getSalinityInterpretationById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<SalinityInterpretationResponseDto> getSalinityInterpretationByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        SalinityInterpretationResponseDto criterion = salinityInterpretationService.getSalinityInterpretationByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<SalinityInterpretationResponseDto> updateSalinityInterpretation(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody SalinityInterpretationPostRequestDto updateRequestDto,
            Authentication authentication) {
        SalinityInterpretationResponseDto updatedCriterion = salinityInterpretationService.updateSalinityInterpretation(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSalinityInterpretation(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        salinityInterpretationService.deleteSalinityInterpretation(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}