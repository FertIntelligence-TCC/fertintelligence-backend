package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.DiverseContentRangeController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.diverseContentRange.DiverseContentRangeResponseDto;
import com.migueltcc.fertintelligence.service.documentation.DiverseContentRangeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/diverse-content-range")
public class DiverseContentRangeControllerImpl implements DiverseContentRangeController {

    @Autowired
    private DiverseContentRangeService diverseContentRangeService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<DiverseContentRangeResponseDto> createDiverseContentRange(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody DiverseContentRangeCreateRequestDto createRequestDto,
            Authentication authentication) {

        DiverseContentRangeResponseDto createdCriterion = diverseContentRangeService.createDiverseContentRange(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/diverse-content-range/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<DiverseContentRangeResponseDto> getDiverseContentRange(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        DiverseContentRangeResponseDto criterion = diverseContentRangeService.getDiverseContentRangeById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<DiverseContentRangeResponseDto> getDiverseContentRangeByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        DiverseContentRangeResponseDto criterion = diverseContentRangeService.getDiverseContentRangeByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<DiverseContentRangeResponseDto> updateDiverseContentRange(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody DiverseContentRangePostRequestDto updateRequestDto,
            Authentication authentication) {
        DiverseContentRangeResponseDto updatedCriterion = diverseContentRangeService.updateDiverseContentRange(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteDiverseContentRange(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        diverseContentRangeService.deleteDiverseContentRange(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}