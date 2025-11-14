package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.AvailablePAnionExchangeResinExtractorController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorResponseDto;
import com.migueltcc.fertintelligence.service.documentation.AvailablePAnionExchangeResinExtractorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/available-p-anion-exchange-resin-extractor")
public class AvailablePAnionExchangeResinExtractorControllerImpl
        implements AvailablePAnionExchangeResinExtractorController {

    @Autowired
    private AvailablePAnionExchangeResinExtractorService availablePAnionExchangeResinExtractorService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<AvailablePAnionExchangeResinExtractorResponseDto> createAvailablePAnionExchangeResinExtractor(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody AvailablePAnionExchangeResinExtractorCreateRequestDto createRequestDto,
            Authentication authentication) {

        AvailablePAnionExchangeResinExtractorResponseDto createdCriterion =
                availablePAnionExchangeResinExtractorService.createAvailablePAnionExchangeResinExtractor(
                        tableId,
                        createRequestDto,
                        authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/available-p-anion-exchange-resin-extractor/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<AvailablePAnionExchangeResinExtractorResponseDto> getAvailablePAnionExchangeResinExtractor(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        AvailablePAnionExchangeResinExtractorResponseDto criterion =
                availablePAnionExchangeResinExtractorService.getAvailablePAnionExchangeResinExtractorById(
                        criterionId,
                        authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<AvailablePAnionExchangeResinExtractorResponseDto> getAvailablePAnionExchangeResinExtractorByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        AvailablePAnionExchangeResinExtractorResponseDto criterion =
                availablePAnionExchangeResinExtractorService.getAvailablePAnionExchangeResinExtractorByTable(
                        tableId,
                        authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<AvailablePAnionExchangeResinExtractorResponseDto> updateAvailablePAnionExchangeResinExtractor(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody AvailablePAnionExchangeResinExtractorPostRequestDto updateRequestDto,
            Authentication authentication) {
        AvailablePAnionExchangeResinExtractorResponseDto updatedCriterion =
                availablePAnionExchangeResinExtractorService.updateAvailablePAnionExchangeResinExtractor(
                        criterionId,
                        updateRequestDto,
                        authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAvailablePAnionExchangeResinExtractor(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        availablePAnionExchangeResinExtractorService.deleteAvailablePAnionExchangeResinExtractor(
                criterionId,
                authentication.getName());
        return ResponseEntity.noContent().build();
    }
}