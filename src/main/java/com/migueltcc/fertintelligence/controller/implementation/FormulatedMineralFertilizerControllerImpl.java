package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.FormulatedMineralFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.FormulatedMineralFertilizerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/formulated-mineral-fertilizer")
public class FormulatedMineralFertilizerControllerImpl implements FormulatedMineralFertilizerController {

    @Autowired
    private FormulatedMineralFertilizerService formulatedMineralFertilizerService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<FormulatedMineralFertilizerResponseDto> createFormulatedMineralFertilizer(
            @Valid @RequestBody FormulatedMineralFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        FormulatedMineralFertilizerResponseDto createdFertilizer = formulatedMineralFertilizerService.createFormulatedMineralFertilizer(
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/formulated-mineral-fertilizer/get")
                .queryParam("formulatedMineralFertilizerId", createdFertilizer.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdFertilizer);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<FormulatedMineralFertilizerResponseDto> getFormulatedMineralFertilizer(
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            Authentication authentication) {
        FormulatedMineralFertilizerResponseDto fertilizer = formulatedMineralFertilizerService.getFormulatedMineralFertilizerById(
                formulatedMineralFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizer);
    }

    @Override
    @GetMapping("/get-by-user")
    public ResponseEntity<List<FormulatedMineralFertilizerResponseDto>> getFormulatedMineralFertilizersByUser(
            Authentication authentication) {
        List<FormulatedMineralFertilizerResponseDto> fertilizers = formulatedMineralFertilizerService.getFormulatedMineralFertilizersByUser(
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<FormulatedMineralFertilizerResponseDto> updateFormulatedMineralFertilizer(
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            @Valid @RequestBody FormulatedMineralFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {
        FormulatedMineralFertilizerResponseDto updatedFertilizer = formulatedMineralFertilizerService.updateFormulatedMineralFertilizer(
                formulatedMineralFertilizerId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedFertilizer);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFormulatedMineralFertilizer(
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            Authentication authentication) {
        formulatedMineralFertilizerService.deleteFormulatedMineralFertilizer(
                formulatedMineralFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }
}