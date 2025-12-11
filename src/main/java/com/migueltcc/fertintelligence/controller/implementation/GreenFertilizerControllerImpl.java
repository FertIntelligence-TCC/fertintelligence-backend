package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.GreenFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.GreenFertilizerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/green-fertilizer")
public class GreenFertilizerControllerImpl implements GreenFertilizerController {

    @Autowired
    private GreenFertilizerService greenFertilizerService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<GreenFertilizerResponseDto> createGreenFertilizer(
            @Valid @RequestBody GreenFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        GreenFertilizerResponseDto createdFertilizer = greenFertilizerService.createGreenFertilizer(
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/green-fertilizer/get")
                .queryParam("greenFertilizerId", createdFertilizer.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdFertilizer);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<GreenFertilizerResponseDto> getGreenFertilizer(
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            Authentication authentication) {
        GreenFertilizerResponseDto fertilizer = greenFertilizerService.getGreenFertilizerById(
                greenFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizer);
    }

    @Override
    @GetMapping("/get-by-user")
    public ResponseEntity<List<GreenFertilizerResponseDto>> getGreenFertilizersByUser(Authentication authentication) {
        List<GreenFertilizerResponseDto> fertilizers = greenFertilizerService.getGreenFertilizersByUser(
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<GreenFertilizerResponseDto>> getGreenFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        List<GreenFertilizerResponseDto> fertilizers = greenFertilizerService.getGreenFertilizersByName(
                name,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<GreenFertilizerResponseDto> updateGreenFertilizer(
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            @Valid @RequestBody GreenFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {
        GreenFertilizerResponseDto updatedFertilizer = greenFertilizerService.updateGreenFertilizer(
                greenFertilizerId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedFertilizer);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteGreenFertilizer(
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            Authentication authentication) {
        greenFertilizerService.deleteGreenFertilizer(
                greenFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }
}