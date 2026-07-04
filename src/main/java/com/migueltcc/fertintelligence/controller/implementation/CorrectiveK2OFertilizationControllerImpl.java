package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CorrectiveK2OFertilizationController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveK2OFertilization.CorrectiveK2OFertilizationResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CorrectiveK2OFertilizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/corrective-k2o-fertilization")
public class CorrectiveK2OFertilizationControllerImpl implements CorrectiveK2OFertilizationController {

    @Autowired
    private CorrectiveK2OFertilizationService service;

    @Override
    @PostMapping("/register")
    public ResponseEntity<CorrectiveK2OFertilizationResponseDto> createCorrectiveK2OFertilization(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody CorrectiveK2OFertilizationCreateRequestDto createRequestDto,
            Authentication authentication) {
        CorrectiveK2OFertilizationResponseDto created = service.createCorrectiveK2OFertilization(tableId, createRequestDto, authentication.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/corrective-k2o-fertilization/get")
                .queryParam("criterionId", created.getId())
                .build()
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CorrectiveK2OFertilizationResponseDto> getCorrectiveK2OFertilization(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        return ResponseEntity.ok(service.getCorrectiveK2OFertilizationById(criterionId, authentication.getName()));
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<List<CorrectiveK2OFertilizationResponseDto>> getCorrectiveK2OFertilizationByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        return ResponseEntity.ok(service.getCorrectiveK2OFertilizationByTable(tableId, authentication.getName()));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CorrectiveK2OFertilizationResponseDto> updateCorrectiveK2OFertilization(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody CorrectiveK2OFertilizationPostRequestDto updateRequestDto,
            Authentication authentication) {
        return ResponseEntity.ok(service.updateCorrectiveK2OFertilization(criterionId, updateRequestDto, authentication.getName()));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCorrectiveK2OFertilization(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        service.deleteCorrectiveK2OFertilization(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
