package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.AvailablePMehlich1ExtractorController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePMehlich1Extractor.AvailablePMehlich1ExtractorResponseDto;
import com.migueltcc.fertintelligence.service.documentation.AvailablePMehlich1ExtractorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/available-p-mehlich-1-extractor")
public class AvailablePMehlich1ExtractorControllerImpl implements AvailablePMehlich1ExtractorController {

    @Autowired
    private AvailablePMehlich1ExtractorService availablePMehlich1ExtractorService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<AvailablePMehlich1ExtractorResponseDto> createAvailablePMehlich1Extractor(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody AvailablePMehlich1ExtractorCreateRequestDto createRequestDto,
            Authentication authentication) {

        AvailablePMehlich1ExtractorResponseDto createdCriterion =
                availablePMehlich1ExtractorService.createAvailablePMehlich1Extractor(
                        tableId,
                        createRequestDto,
                        authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/available-p-mehlich-1-extractor/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<AvailablePMehlich1ExtractorResponseDto> getAvailablePMehlich1Extractor(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        AvailablePMehlich1ExtractorResponseDto criterion =
                availablePMehlich1ExtractorService.getAvailablePMehlich1ExtractorById(
                        criterionId,
                        authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<AvailablePMehlich1ExtractorResponseDto> getAvailablePMehlich1ExtractorByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        AvailablePMehlich1ExtractorResponseDto criterion =
                availablePMehlich1ExtractorService.getAvailablePMehlich1ExtractorByTable(
                        tableId,
                        authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<AvailablePMehlich1ExtractorResponseDto> updateAvailablePMehlich1Extractor(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody AvailablePMehlich1ExtractorPostRequestDto updateRequestDto,
            Authentication authentication) {
        AvailablePMehlich1ExtractorResponseDto updatedCriterion =
                availablePMehlich1ExtractorService.updateAvailablePMehlich1Extractor(
                        criterionId,
                        updateRequestDto,
                        authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAvailablePMehlich1Extractor(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        availablePMehlich1ExtractorService.deleteAvailablePMehlich1Extractor(
                criterionId,
                authentication.getName());
        return ResponseEntity.noContent().build();
    }
}