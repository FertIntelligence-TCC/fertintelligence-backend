package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.SulfurDoseController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.sulfurDose.SulfurDoseResponseDto;
import com.migueltcc.fertintelligence.service.documentation.SulfurDoseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/sulfur-dose")
public class SulfurDoseControllerImpl implements SulfurDoseController {

    @Autowired
    private SulfurDoseService sulfurDoseService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<SulfurDoseResponseDto> createSulfurDose(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody SulfurDoseCreateRequestDto createRequestDto,
            Authentication authentication) {

        SulfurDoseResponseDto createdCriterion = sulfurDoseService.createSulfurDose(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/sulfur-dose/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<SulfurDoseResponseDto> getSulfurDose(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        SulfurDoseResponseDto criterion = sulfurDoseService.getSulfurDoseById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<SulfurDoseResponseDto> getSulfurDoseByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        SulfurDoseResponseDto criterion = sulfurDoseService.getSulfurDoseByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<SulfurDoseResponseDto> updateSulfurDose(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody SulfurDosePostRequestDto updateRequestDto,
            Authentication authentication) {
        SulfurDoseResponseDto updatedCriterion = sulfurDoseService.updateSulfurDose(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSulfurDose(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        sulfurDoseService.deleteSulfurDose(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
