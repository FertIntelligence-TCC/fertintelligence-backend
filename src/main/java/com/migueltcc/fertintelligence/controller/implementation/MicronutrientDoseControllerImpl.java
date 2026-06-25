package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.MicronutrientDoseController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose.MicronutrientDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose.MicronutrientDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.micronutrientDose.MicronutrientDoseResponseDto;
import com.migueltcc.fertintelligence.service.documentation.MicronutrientDoseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/micronutrient-dose")
public class MicronutrientDoseControllerImpl implements MicronutrientDoseController {

    @Autowired
    private MicronutrientDoseService micronutrientDoseService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<MicronutrientDoseResponseDto> createMicronutrientDose(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody MicronutrientDoseCreateRequestDto createRequestDto,
            Authentication authentication) {

        MicronutrientDoseResponseDto createdCriterion = micronutrientDoseService.createMicronutrientDose(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/micronutrient-dose/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<MicronutrientDoseResponseDto> getMicronutrientDose(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        MicronutrientDoseResponseDto criterion = micronutrientDoseService.getMicronutrientDoseById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<MicronutrientDoseResponseDto> getMicronutrientDoseByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        MicronutrientDoseResponseDto criterion = micronutrientDoseService.getMicronutrientDoseByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<MicronutrientDoseResponseDto> updateMicronutrientDose(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody MicronutrientDosePostRequestDto updateRequestDto,
            Authentication authentication) {
        MicronutrientDoseResponseDto updatedCriterion = micronutrientDoseService.updateMicronutrientDose(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMicronutrientDose(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        micronutrientDoseService.deleteMicronutrientDose(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
