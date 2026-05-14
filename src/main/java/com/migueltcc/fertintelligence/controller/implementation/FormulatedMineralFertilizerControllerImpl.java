package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.FormulatedMineralFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.FormulatedMineralFertilizerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/formulated-mineral-fertilizer")
public class FormulatedMineralFertilizerControllerImpl implements FormulatedMineralFertilizerController {

    private final FormulatedMineralFertilizerService formulatedMineralFertilizerService;

    // Injeção via Construtor
    public FormulatedMineralFertilizerControllerImpl(FormulatedMineralFertilizerService formulatedMineralFertilizerService) {
        this.formulatedMineralFertilizerService = formulatedMineralFertilizerService;
    }

    // Helper para evitar NullPointerException e Erro 500
    private String getAuthenticatedUsername(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        return authentication.getName();
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<FormulatedMineralFertilizerResponseDto> createFormulatedMineralFertilizer(
            @Valid @RequestBody FormulatedMineralFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);

        FormulatedMineralFertilizerResponseDto createdFertilizer = formulatedMineralFertilizerService.createFormulatedMineralFertilizer(
                createRequestDto,
                username
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdFertilizer.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdFertilizer);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<FormulatedMineralFertilizerResponseDto> getFormulatedMineralFertilizer(
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);

        FormulatedMineralFertilizerResponseDto fertilizer = formulatedMineralFertilizerService.getFormulatedMineralFertilizerById(
                formulatedMineralFertilizerId,
                username
        );
        return ResponseEntity.ok(fertilizer);
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<FormulatedMineralFertilizerResponseDto>> getAllFormulatedMineralFertilizers(
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);

        List<FormulatedMineralFertilizerResponseDto> fertilizers = formulatedMineralFertilizerService.getAllFormulatedMineralFertilizers(
                username
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @GetMapping("/get-all-public")
    public ResponseEntity<List<FormulatedMineralFertilizerResponseDto>> getAllPublicFormulatedMineralFertilizers(Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<FormulatedMineralFertilizerResponseDto> list = formulatedMineralFertilizerService.getAllPublicFormulatedMineralFertilizers(username);
        return ResponseEntity.ok(list);
    }


    @Override
    @PutMapping("/update")
    public ResponseEntity<FormulatedMineralFertilizerResponseDto> updateFormulatedMineralFertilizer(
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            @Valid @RequestBody FormulatedMineralFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);

        FormulatedMineralFertilizerResponseDto updatedFertilizer = formulatedMineralFertilizerService.updateFormulatedMineralFertilizer(
                formulatedMineralFertilizerId,
                updateRequestDto,
                username
        );
        return ResponseEntity.ok(updatedFertilizer);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteFormulatedMineralFertilizer(
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);

        formulatedMineralFertilizerService.deleteFormulatedMineralFertilizer(
                formulatedMineralFertilizerId,
                username
        );
        return ResponseEntity.noContent().build();
    }
}