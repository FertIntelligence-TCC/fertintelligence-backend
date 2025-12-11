package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.MineralFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.MineralFertilizerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/mineral-fertilizer")
public class MineralFertilizerControllerImpl implements MineralFertilizerController {

    @Autowired
    private MineralFertilizerService mineralFertilizerService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<MineralFertilizerResponseDto> createMineralFertilizer(
            @Valid @RequestBody MineralFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        MineralFertilizerResponseDto createdFertilizer = mineralFertilizerService.createMineralFertilizer(
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/mineral-fertilizer/get")
                .queryParam("mineralFertilizerId", createdFertilizer.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdFertilizer);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<MineralFertilizerResponseDto> getMineralFertilizer(
            @RequestParam(name = "mineralFertilizerId") Long mineralFertilizerId,
            Authentication authentication) {
        MineralFertilizerResponseDto fertilizer = mineralFertilizerService.getMineralFertilizerById(
                mineralFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizer);
    }

    @Override
    @GetMapping("/get-by-user")
    public ResponseEntity<List<MineralFertilizerResponseDto>> getMineralFertilizersByUser(
            Authentication authentication) {
        List<MineralFertilizerResponseDto> fertilizers = mineralFertilizerService.getMineralFertilizersByUser(
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<MineralFertilizerResponseDto>> getMineralFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        List<MineralFertilizerResponseDto> fertilizers = mineralFertilizerService.getMineralFertilizersByName(
                name,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<MineralFertilizerResponseDto> updateMineralFertilizer(
            @RequestParam(name = "mineralFertilizerId") Long mineralFertilizerId,
            @Valid @RequestBody MineralFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {
        MineralFertilizerResponseDto updatedFertilizer = mineralFertilizerService.updateMineralFertilizer(
                mineralFertilizerId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedFertilizer);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMineralFertilizer(
            @RequestParam(name = "mineralFertilizerId") Long mineralFertilizerId,
            Authentication authentication) {
        mineralFertilizerService.deleteMineralFertilizer(
                mineralFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }
}