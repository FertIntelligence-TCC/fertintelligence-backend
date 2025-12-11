package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.ChelatedFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.ChelatedFertilizerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/chelated-fertilizer")
public class ChelatedFertilizerControllerImpl implements ChelatedFertilizerController {

    @Autowired
    private ChelatedFertilizerService chelatedFertilizerService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<ChelatedFertilizerResponseDto> createChelatedFertilizer(
            @Valid @RequestBody ChelatedFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        ChelatedFertilizerResponseDto createdFertilizer = chelatedFertilizerService.createChelatedFertilizer(
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/chelated-fertilizer/get")
                .queryParam("chelatedFertilizerId", createdFertilizer.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdFertilizer);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<ChelatedFertilizerResponseDto> getChelatedFertilizer(
            @RequestParam(name = "chelatedFertilizerId") Long chelatedFertilizerId,
            Authentication authentication) {
        ChelatedFertilizerResponseDto fertilizer = chelatedFertilizerService.getChelatedFertilizerById(
                chelatedFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizer);
    }

    @Override
    @GetMapping("/get-by-user")
    public ResponseEntity<List<ChelatedFertilizerResponseDto>> getChelatedFertilizersByUser(
            Authentication authentication) {
        List<ChelatedFertilizerResponseDto> fertilizers = chelatedFertilizerService.getChelatedFertilizersByUser(
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<ChelatedFertilizerResponseDto>> getChelatedFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        List<ChelatedFertilizerResponseDto> fertilizers = chelatedFertilizerService.getChelatedFertilizersByName(
                name,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<ChelatedFertilizerResponseDto> updateChelatedFertilizer(
            @RequestParam(name = "chelatedFertilizerId") Long chelatedFertilizerId,
            @Valid @RequestBody ChelatedFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {
        ChelatedFertilizerResponseDto updatedFertilizer = chelatedFertilizerService.updateChelatedFertilizer(
                chelatedFertilizerId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedFertilizer);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteChelatedFertilizer(
            @RequestParam(name = "chelatedFertilizerId") Long chelatedFertilizerId,
            Authentication authentication) {
        chelatedFertilizerService.deleteChelatedFertilizer(
                chelatedFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }
}