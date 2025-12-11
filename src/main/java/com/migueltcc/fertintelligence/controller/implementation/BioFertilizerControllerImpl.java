package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.BioFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.BioFertilizerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/bio-fertilizer")
public class BioFertilizerControllerImpl implements BioFertilizerController {

    @Autowired
    private BioFertilizerService bioFertilizerService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<BioFertilizerResponseDto> createBioFertilizer(
            @Valid @RequestBody BioFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        BioFertilizerResponseDto createdFertilizer = bioFertilizerService.createBioFertilizer(
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/bio-fertilizer/get")
                .queryParam("bioFertilizerId", createdFertilizer.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdFertilizer);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<BioFertilizerResponseDto> getBioFertilizer(
            @RequestParam(name = "bioFertilizerId") Long bioFertilizerId,
            Authentication authentication) {
        BioFertilizerResponseDto fertilizer = bioFertilizerService.getBioFertilizerById(
                bioFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizer);
    }

    @Override
    @GetMapping("/get-by-user")
    public ResponseEntity<List<BioFertilizerResponseDto>> getBioFertilizersByUser(Authentication authentication) {
        List<BioFertilizerResponseDto> fertilizers = bioFertilizerService.getBioFertilizersByUser(
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<BioFertilizerResponseDto>> getBioFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        List<BioFertilizerResponseDto> fertilizers = bioFertilizerService.getBioFertilizersByName(
                name,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<BioFertilizerResponseDto> updateBioFertilizer(
            @RequestParam(name = "bioFertilizerId") Long bioFertilizerId,
            @Valid @RequestBody BioFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {
        BioFertilizerResponseDto updatedFertilizer = bioFertilizerService.updateBioFertilizer(
                bioFertilizerId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedFertilizer);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteBioFertilizer(
            @RequestParam(name = "bioFertilizerId") Long bioFertilizerId,
            Authentication authentication) {
        bioFertilizerService.deleteBioFertilizer(
                bioFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }
}