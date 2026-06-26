package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.GeneralRecommendationController;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.generalRecommendation.GeneralRecommendationResponseDto;
import com.migueltcc.fertintelligence.service.documentation.GeneralRecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/general-recommendation")
public class GeneralRecommendationControllerImpl implements GeneralRecommendationController {

    @Autowired
    private GeneralRecommendationService generalRecommendationService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<GeneralRecommendationResponseDto> create(
            @Valid @RequestBody GeneralRecommendationCreateRequestDto dto,
            Authentication authentication) {
        GeneralRecommendationResponseDto created = generalRecommendationService.create(dto, authentication.getName());
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/general-recommendation/get")
                .queryParam("id", created.getId())
                .build()
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<GeneralRecommendationResponseDto> get(@RequestParam(name = "id") Long id,
                                                                 Authentication authentication) {
        return ResponseEntity.ok(generalRecommendationService.get(id, authentication.getName()));
    }

    @Override
    @GetMapping("/get-by-recommendation")
    public ResponseEntity<GeneralRecommendationResponseDto> getByRecommendation(
            @RequestParam(name = "recommendationId") Long recommendationId,
            Authentication authentication) {
        return ResponseEntity.ok(generalRecommendationService.getByRecommendation(recommendationId, authentication.getName()));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<GeneralRecommendationResponseDto> update(@RequestParam(name = "id") Long id,
                                                                    @Valid @RequestBody GeneralRecommendationPostRequestDto dto,
                                                                    Authentication authentication) {
        return ResponseEntity.ok(generalRecommendationService.update(id, dto, authentication.getName()));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam(name = "id") Long id,
                                       Authentication authentication) {
        generalRecommendationService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
