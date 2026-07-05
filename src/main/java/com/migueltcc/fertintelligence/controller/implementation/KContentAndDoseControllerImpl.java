package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.KContentAndDoseController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDoseCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDosePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDoseResponseDto;
import com.migueltcc.fertintelligence.service.documentation.KContentAndDoseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping({"/k-content-and-dose", "/potassium-content-and-dose"})
public class KContentAndDoseControllerImpl implements KContentAndDoseController {

    @Autowired
    private KContentAndDoseService kContentAndDoseService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<KContentAndDoseResponseDto> createKContentAndDose(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody KContentAndDoseCreateRequestDto createRequestDto,
            Authentication authentication) {

        KContentAndDoseResponseDto createdCriterion = kContentAndDoseService.createKContentAndDose(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/k-content-and-dose/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<KContentAndDoseResponseDto> getKContentAndDose(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        KContentAndDoseResponseDto criterion = kContentAndDoseService.getKContentAndDoseById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<KContentAndDoseResponseDto> getKContentAndDoseByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        KContentAndDoseResponseDto criterion = kContentAndDoseService.getKContentAndDoseByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<KContentAndDoseResponseDto> updateKContentAndDose(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody KContentAndDosePostRequestDto updateRequestDto,
            Authentication authentication) {
        KContentAndDoseResponseDto updatedCriterion = kContentAndDoseService.updateKContentAndDose(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteKContentAndDose(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        kContentAndDoseService.deleteKContentAndDose(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
