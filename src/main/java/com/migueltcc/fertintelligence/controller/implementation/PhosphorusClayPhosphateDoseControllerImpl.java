package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.PhosphorusClayPhosphateDoseController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.phosphorusClayPhosphateDose.PhosphorusClayPhosphateDoseResponseDto;
import com.migueltcc.fertintelligence.service.documentation.PhosphorusClayPhosphateDoseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping({
        "/phosphorus-clay-phosphate-dose",
        "/phosphorus-clay-content-and-phosphate-dose"
})
public class PhosphorusClayPhosphateDoseControllerImpl implements PhosphorusClayPhosphateDoseController {

    @Autowired
    private PhosphorusClayPhosphateDoseService phosphorusClayPhosphateDoseService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<PhosphorusClayPhosphateDoseResponseDto> createPhosphorusClayPhosphateDose(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody PhosphorusClayPhosphateDoseCreateRequestDto createRequestDto,
            Authentication authentication) {

        PhosphorusClayPhosphateDoseResponseDto createdCriterion = phosphorusClayPhosphateDoseService.createPhosphorusClayPhosphateDose(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/phosphorus-clay-phosphate-dose/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<PhosphorusClayPhosphateDoseResponseDto> getPhosphorusClayPhosphateDose(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        PhosphorusClayPhosphateDoseResponseDto criterion = phosphorusClayPhosphateDoseService.getPhosphorusClayPhosphateDoseById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<PhosphorusClayPhosphateDoseResponseDto> getPhosphorusClayPhosphateDoseByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        PhosphorusClayPhosphateDoseResponseDto criterion = phosphorusClayPhosphateDoseService.getPhosphorusClayPhosphateDoseByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<PhosphorusClayPhosphateDoseResponseDto> updatePhosphorusClayPhosphateDose(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody PhosphorusClayPhosphateDosePostRequestDto updateRequestDto,
            Authentication authentication) {
        PhosphorusClayPhosphateDoseResponseDto updatedCriterion = phosphorusClayPhosphateDoseService.updatePhosphorusClayPhosphateDose(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deletePhosphorusClayPhosphateDose(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        phosphorusClayPhosphateDoseService.deletePhosphorusClayPhosphateDose(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
