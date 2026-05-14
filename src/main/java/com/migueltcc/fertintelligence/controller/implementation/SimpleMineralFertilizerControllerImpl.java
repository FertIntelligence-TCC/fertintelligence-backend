package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.SimpleMineralFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.SimpleMineralFertilizerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/simple-mineral-fertilizer")
public class SimpleMineralFertilizerControllerImpl implements SimpleMineralFertilizerController {

    @Autowired
    private SimpleMineralFertilizerService simpleMineralFertilizerService;

    // Helper para validar autenticação e evitar erro 500 (NPE)
    private String getAuthenticatedUsername(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        return authentication.getName();
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<SimpleMineralFertilizerResponseDto> createSimpleMineralFertilizer(
            @Valid @RequestBody SimpleMineralFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);

        SimpleMineralFertilizerResponseDto createdFertilizer = simpleMineralFertilizerService.createSimpleMineralFertilizer(
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
    @GetMapping("/get-all")
    public ResponseEntity<List<SimpleMineralFertilizerResponseDto>> getSimpleMineralFertilizers(
            Authentication authentication) {

        // CORREÇÃO DO ERRO 500: Valida se authentication existe antes de usar
        String username = getAuthenticatedUsername(authentication);

        List<SimpleMineralFertilizerResponseDto> fertilizers = simpleMineralFertilizerService.getAllSimpleMineralFertilizers(username);
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @GetMapping("/get-all-public")
    public ResponseEntity<List<SimpleMineralFertilizerResponseDto>> getAllPublicSimpleMineralFertilizers(Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<SimpleMineralFertilizerResponseDto> list = simpleMineralFertilizerService.getAllPublicSimpleMineralFertilizers();
        return ResponseEntity.ok(list);
    }


    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<SimpleMineralFertilizerResponseDto>> getSimpleMineralFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);

        List<SimpleMineralFertilizerResponseDto> fertilizers = simpleMineralFertilizerService.getSimpleMineralFertilizersByName(
                name,
                username
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<SimpleMineralFertilizerResponseDto> updateSimpleMineralFertilizer(
            @RequestParam(name = "simpleMineralFertilizerId") Long simpleMineralFertilizerId,
            @Valid @RequestBody SimpleMineralFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);

        SimpleMineralFertilizerResponseDto updatedFertilizer = simpleMineralFertilizerService.updateSimpleMineralFertilizer(
                simpleMineralFertilizerId,
                updateRequestDto,
                username
        );
        return ResponseEntity.ok(updatedFertilizer);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSimpleMineralFertilizer(
            @RequestParam(name = "simpleMineralFertilizerId") Long simpleMineralFertilizerId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);

        simpleMineralFertilizerService.deleteSimpleMineralFertilizer(
                simpleMineralFertilizerId,
                username
        );
        return ResponseEntity.noContent().build();
    }
}