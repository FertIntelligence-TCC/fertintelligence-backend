package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.SolidSourceController;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourcePostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceResponseDto;
import com.migueltcc.fertintelligence.service.documentation.SolidSourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

    @RestController
    @RequestMapping("/foliar-fertilization/solid-source")
    public class SolidSourceControllerImpl implements SolidSourceController {

        @Autowired
        private SolidSourceService solidSourceService;

        @Override
        @PostMapping("/register")
        public ResponseEntity<SolidSourceResponseDto> createSolidSource(
                @RequestParam(name = "cropId") Long cropId,
                @Valid @RequestBody SolidSourceCreateRequestDto createRequestDto,
                Authentication authentication) {

            SolidSourceResponseDto createdSource = solidSourceService.createSolidSource(
                    cropId,
                    createRequestDto,
                    authentication.getName()
            );

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/foliar-fertilization/solid-source/get")
                    .queryParam("solidSourceId", createdSource.getId())
                    .build()
                    .toUri();

            return ResponseEntity.created(location).body(createdSource);
        }

        @Override
        @GetMapping("/get")
        public ResponseEntity<SolidSourceResponseDto> getSolidSource(
                @RequestParam(name = "solidSourceId") Long solidSourceId,
                Authentication authentication) {
            SolidSourceResponseDto source = solidSourceService.getSolidSourceById(solidSourceId, authentication.getName());
            return ResponseEntity.ok(source);
        }

        @Override
        @GetMapping("/get-by-crop")
        public ResponseEntity<List<SolidSourceResponseDto>> getSolidSourcesByCrop(
                @RequestParam(name = "cropId") Long cropId,
                Authentication authentication) {
            List<SolidSourceResponseDto> sources = solidSourceService.getAllSolidSourcesByCrop(cropId, authentication.getName());
            return ResponseEntity.ok(sources);
        }

        @Override
        @PutMapping("/update")
        public ResponseEntity<SolidSourceResponseDto> updateSolidSource(
                @RequestParam(name = "solidSourceId") Long solidSourceId,
                @Valid @RequestBody SolidSourcePostRequestDto updateRequestDto,
                Authentication authentication) {
            SolidSourceResponseDto updatedSource = solidSourceService.updateSolidSource(
                    solidSourceId,
                    updateRequestDto,
                    authentication.getName()
            );
            return ResponseEntity.ok(updatedSource);
        }

        @Override
        @DeleteMapping("/delete")
        public ResponseEntity<Void> deleteSolidSource(
                @RequestParam(name = "solidSourceId") Long solidSourceId,
                Authentication authentication) {
            solidSourceService.deleteSolidSource(solidSourceId, authentication.getName());
            return ResponseEntity.noContent().build();
        }
    }