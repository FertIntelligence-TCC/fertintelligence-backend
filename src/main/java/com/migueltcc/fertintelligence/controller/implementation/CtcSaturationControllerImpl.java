package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CtcSaturationController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.ctcSaturation.CtcSaturationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.ctcSaturation.CtcSaturationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.ctcSaturation.CtcSaturationResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CtcSaturationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/ctc-saturation")
public class CtcSaturationControllerImpl implements CtcSaturationController {

    @Autowired
    private CtcSaturationService ctcSaturationService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<CtcSaturationResponseDto> createCtcSaturation(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody CtcSaturationCreateRequestDto createRequestDto,
            Authentication authentication) {
        CtcSaturationResponseDto createdCriterion = ctcSaturationService.createCtcSaturation(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/ctc-saturation/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CtcSaturationResponseDto> getCtcSaturation(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        CtcSaturationResponseDto criterion = ctcSaturationService.getCtcSaturationById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<CtcSaturationResponseDto> getCtcSaturationByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        CtcSaturationResponseDto criterion = ctcSaturationService.getCtcSaturationByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CtcSaturationResponseDto> updateCtcSaturation(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody CtcSaturationPostRequestDto updateRequestDto,
            Authentication authentication) {
        CtcSaturationResponseDto updatedCriterion = ctcSaturationService.updateCtcSaturation(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCtcSaturation(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        ctcSaturationService.deleteCtcSaturation(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
