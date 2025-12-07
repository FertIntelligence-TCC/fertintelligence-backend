package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.SimpleMineralFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.SimpleMineralFertilizerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/simple-mineral-fertilizer")
public class SimpleMineralFertilizerControllerImpl implements SimpleMineralFertilizerController {

    @Autowired
    private SimpleMineralFertilizerService simpleMineralFertilizerService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<SimpleMineralFertilizerResponseDto> createSimpleMineralFertilizer(
            @Valid @RequestBody SimpleMineralFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        SimpleMineralFertilizerResponseDto createdFertilizer = simpleMineralFertilizerService.createSimpleMineralFertilizer(
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/simple-mineral-fertilizer/get")
                .queryParam("simpleMineralFertilizerId", createdFertilizer.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdFertilizer);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<SimpleMineralFertilizerResponseDto> getSimpleMineralFertilizer(
            @RequestParam(name = "simpleMineralFertilizerId") Long simpleMineralFertilizerId,
            Authentication authentication) {
        SimpleMineralFertilizerResponseDto fertilizer = simpleMineralFertilizerService.getSimpleMineralFertilizerById(
                simpleMineralFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizer);
    }

    @Override
    @GetMapping("/get-by-user")
    public ResponseEntity<List<SimpleMineralFertilizerResponseDto>> getSimpleMineralFertilizersByUser(
            Authentication authentication) {
        List<SimpleMineralFertilizerResponseDto> fertilizers = simpleMineralFertilizerService.getSimpleMineralFertilizersByUser(
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<SimpleMineralFertilizerResponseDto> updateSimpleMineralFertilizer(
            @RequestParam(name = "simpleMineralFertilizerId") Long simpleMineralFertilizerId,
            @Valid @RequestBody SimpleMineralFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {
        SimpleMineralFertilizerResponseDto updatedFertilizer = simpleMineralFertilizerService.updateSimpleMineralFertilizer(
                simpleMineralFertilizerId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedFertilizer);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSimpleMineralFertilizer(
            @RequestParam(name = "simpleMineralFertilizerId") Long simpleMineralFertilizerId,
            Authentication authentication) {
        simpleMineralFertilizerService.deleteSimpleMineralFertilizer(
                simpleMineralFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }
}