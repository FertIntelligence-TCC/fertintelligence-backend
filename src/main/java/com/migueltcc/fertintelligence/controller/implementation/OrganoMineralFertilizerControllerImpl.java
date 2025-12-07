package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.OrganoMineralFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.OrganoMineralFertilizerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/organo-mineral-fertilizer")
public class OrganoMineralFertilizerControllerImpl implements OrganoMineralFertilizerController {

    @Autowired
    private OrganoMineralFertilizerService organoMineralFertilizerService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<OrganoMineralFertilizerResponseDto> createOrganoMineralFertilizer(
            @Valid @RequestBody OrganoMineralFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        OrganoMineralFertilizerResponseDto createdFertilizer = organoMineralFertilizerService.createOrganoMineralFertilizer(
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/organo-mineral-fertilizer/get")
                .queryParam("organoMineralFertilizerId", createdFertilizer.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdFertilizer);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<OrganoMineralFertilizerResponseDto> getOrganoMineralFertilizer(
            @RequestParam(name = "organoMineralFertilizerId") Long organoMineralFertilizerId,
            Authentication authentication) {
        OrganoMineralFertilizerResponseDto fertilizer = organoMineralFertilizerService.getOrganoMineralFertilizerById(
                organoMineralFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizer);
    }

    @Override
    @GetMapping("/get-by-user")
    public ResponseEntity<List<OrganoMineralFertilizerResponseDto>> getOrganoMineralFertilizersByUser(
            Authentication authentication) {
        List<OrganoMineralFertilizerResponseDto> fertilizers = organoMineralFertilizerService.getOrganoMineralFertilizersByUser(
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<OrganoMineralFertilizerResponseDto>> getOrganoMineralFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        List<OrganoMineralFertilizerResponseDto> fertilizers = organoMineralFertilizerService.getOrganoMineralFertilizersByName(
                name,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizers);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<OrganoMineralFertilizerResponseDto> updateOrganoMineralFertilizer(
            @RequestParam(name = "organoMineralFertilizerId") Long organoMineralFertilizerId,
            @Valid @RequestBody OrganoMineralFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {
        OrganoMineralFertilizerResponseDto updatedFertilizer = organoMineralFertilizerService.updateOrganoMineralFertilizer(
                organoMineralFertilizerId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedFertilizer);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteOrganoMineralFertilizer(
            @RequestParam(name = "organoMineralFertilizerId") Long organoMineralFertilizerId,
            Authentication authentication) {
        organoMineralFertilizerService.deleteOrganoMineralFertilizer(
                organoMineralFertilizerId,
                authentication.getName()
        );
        return ResponseEntity.noContent().build();
    }
}