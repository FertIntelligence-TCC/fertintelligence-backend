package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.TopDressingFertilizationController;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationResponseDto;
import com.migueltcc.fertintelligence.service.documentation.TopDressingFertilizationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/top-dressing-fertilization")
public class TopDressingFertilizationControllerImpl implements TopDressingFertilizationController {

    @Autowired
    private TopDressingFertilizationService topDressingFertilizationService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<TopDressingFertilizationResponseDto> createTopDressingFertilization(
            @RequestParam(name = "cropId") Long cropId,
            @Valid @RequestBody TopDressingFertilizationCreateRequestDto createRequestDto,
            Authentication authentication) {

        TopDressingFertilizationResponseDto createdFertilization = topDressingFertilizationService.createTopDressingFertilization(
                cropId,
                createRequestDto,
                authentication.getName()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/top-dressing-fertilization/get")
                .queryParam("fertilizationId", createdFertilization.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdFertilization);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<TopDressingFertilizationResponseDto> getTopDressingFertilization(
            @RequestParam(name = "fertilizationId") Long fertilizationId,
            Authentication authentication) {
        TopDressingFertilizationResponseDto fertilization = topDressingFertilizationService.getTopDressingFertilizationById(
                fertilizationId,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilization);
    }

    @Override
    @GetMapping("/get-by-crop")
    public ResponseEntity<List<TopDressingFertilizationResponseDto>> getTopDressingFertilizationsByCrop(
            @RequestParam(name = "cropId") Long cropId,
            Authentication authentication) {
        List<TopDressingFertilizationResponseDto> fertilizations = topDressingFertilizationService.getAllTopDressingFertilizationsByCrop(
                cropId,
                authentication.getName()
        );
        return ResponseEntity.ok(fertilizations);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<TopDressingFertilizationResponseDto> updateTopDressingFertilization(
            @RequestParam(name = "fertilizationId") Long fertilizationId,
            @Valid @RequestBody TopDressingFertilizationPostRequestDto updateRequestDto,
            Authentication authentication) {
        TopDressingFertilizationResponseDto updatedFertilization = topDressingFertilizationService.updateTopDressingFertilization(
                fertilizationId,
                updateRequestDto,
                authentication.getName()
        );
        return ResponseEntity.ok(updatedFertilization);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteTopDressingFertilization(
            @RequestParam(name = "fertilizationId") Long fertilizationId,
            Authentication authentication) {
        topDressingFertilizationService.deleteTopDressingFertilization(fertilizationId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}