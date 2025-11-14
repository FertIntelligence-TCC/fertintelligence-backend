package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.AvailableSController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSResponseDto;
import com.migueltcc.fertintelligence.service.documentation.AvailableSService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/available-s")
public class AvailableSControllerImpl implements AvailableSController {

    @Autowired
    private AvailableSService availableSService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<AvailableSResponseDto> createAvailableS(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody AvailableSCreateRequestDto createRequestDto,
            Authentication authentication) {

        AvailableSResponseDto createdCriterion = availableSService.createAvailableS(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/available-s/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<AvailableSResponseDto> getAvailableS(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        AvailableSResponseDto criterion = availableSService.getAvailableSById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<AvailableSResponseDto> getAvailableSByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        AvailableSResponseDto criterion = availableSService.getAvailableSByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<AvailableSResponseDto> updateAvailableS(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody AvailableSPostRequestDto updateRequestDto,
            Authentication authentication) {
        AvailableSResponseDto updatedCriterion = availableSService.updateAvailableS(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAvailableS(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        availableSService.deleteAvailableS(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}