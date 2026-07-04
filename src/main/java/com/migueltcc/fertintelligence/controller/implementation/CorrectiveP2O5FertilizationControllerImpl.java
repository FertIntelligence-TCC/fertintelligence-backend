package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CorrectiveP2O5FertilizationController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.correctiveP2O5Fertilization.CorrectiveP2O5FertilizationResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CorrectiveP2O5FertilizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/corrective-p2o5-fertilization")
public class CorrectiveP2O5FertilizationControllerImpl implements CorrectiveP2O5FertilizationController {

    @Autowired
    private CorrectiveP2O5FertilizationService service;

    @Override
    @PostMapping("/register")
    public ResponseEntity<CorrectiveP2O5FertilizationResponseDto> createCorrectiveP2O5Fertilization(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody CorrectiveP2O5FertilizationCreateRequestDto createRequestDto,
            Authentication authentication) {
        CorrectiveP2O5FertilizationResponseDto created = service.createCorrectiveP2O5Fertilization(tableId, createRequestDto, authentication.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/corrective-p2o5-fertilization/get")
                .queryParam("criterionId", created.getId())
                .build()
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CorrectiveP2O5FertilizationResponseDto> getCorrectiveP2O5Fertilization(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        return ResponseEntity.ok(service.getCorrectiveP2O5FertilizationById(criterionId, authentication.getName()));
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<List<CorrectiveP2O5FertilizationResponseDto>> getCorrectiveP2O5FertilizationByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        return ResponseEntity.ok(service.getCorrectiveP2O5FertilizationByTable(tableId, authentication.getName()));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CorrectiveP2O5FertilizationResponseDto> updateCorrectiveP2O5Fertilization(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody CorrectiveP2O5FertilizationPostRequestDto updateRequestDto,
            Authentication authentication) {
        return ResponseEntity.ok(service.updateCorrectiveP2O5Fertilization(criterionId, updateRequestDto, authentication.getName()));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCorrectiveP2O5Fertilization(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        service.deleteCorrectiveP2O5Fertilization(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
