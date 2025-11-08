package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.LiquidSourceController;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourcePostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceResponseDto;
import com.migueltcc.fertintelligence.service.documentation.LiquidSourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

    @RestController
    @RequestMapping("/foliar-fertilization/liquid-source")
    public class LiquidSourceControllerImpl implements LiquidSourceController {

        @Autowired
        private LiquidSourceService liquidSourceService;

        @Override
        @PostMapping("/register")
        public ResponseEntity<LiquidSourceResponseDto> createLiquidSource(
                @RequestParam(name = "cropId") Long cropId,
                @Valid @RequestBody LiquidSourceCreateRequestDto createRequestDto,
                Authentication authentication) {

            LiquidSourceResponseDto createdSource = liquidSourceService.createLiquidSource(
                    cropId,
                    createRequestDto,
                    authentication.getName()
            );

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/foliar-fertilization/liquid-source/get")
                    .queryParam("liquidSourceId", createdSource.getId())
                    .build()
                    .toUri();

            return ResponseEntity.created(location).body(createdSource);
        }

        @Override
        @GetMapping("/get")
        public ResponseEntity<LiquidSourceResponseDto> getLiquidSource(
                @RequestParam(name = "liquidSourceId") Long liquidSourceId,
                Authentication authentication) {
            LiquidSourceResponseDto source = liquidSourceService.getLiquidSourceById(liquidSourceId, authentication.getName());
            return ResponseEntity.ok(source);
        }

        @Override
        @GetMapping("/get-by-crop")
        public ResponseEntity<List<LiquidSourceResponseDto>> getLiquidSourcesByCrop(
                @RequestParam(name = "cropId") Long cropId,
                Authentication authentication) {
            List<LiquidSourceResponseDto> sources = liquidSourceService.getAllLiquidSourcesByCrop(cropId, authentication.getName());
            return ResponseEntity.ok(sources);
        }

        @Override
        @PutMapping("/update")
        public ResponseEntity<LiquidSourceResponseDto> updateLiquidSource(
                @RequestParam(name = "liquidSourceId") Long liquidSourceId,
                @Valid @RequestBody LiquidSourcePostRequestDto updateRequestDto,
                Authentication authentication) {
            LiquidSourceResponseDto updatedSource = liquidSourceService.updateLiquidSource(
                    liquidSourceId,
                    updateRequestDto,
                    authentication.getName()
            );
            return ResponseEntity.ok(updatedSource);
        }

        @Override
        @DeleteMapping("/delete")
        public ResponseEntity<Void> deleteLiquidSource(
                @RequestParam(name = "liquidSourceId") Long liquidSourceId,
                Authentication authentication) {
            liquidSourceService.deleteLiquidSource(liquidSourceId, authentication.getName());
            return ResponseEntity.noContent().build();
        }
    }