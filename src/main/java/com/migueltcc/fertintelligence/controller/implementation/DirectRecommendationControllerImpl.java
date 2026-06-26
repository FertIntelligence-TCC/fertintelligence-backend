package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.DirectRecommendationController;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.directRecommendation.DirectRecommendationResponseDto;
import com.migueltcc.fertintelligence.service.documentation.DirectRecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/direct-recommendation")
public class DirectRecommendationControllerImpl implements DirectRecommendationController {

    @Autowired
    private DirectRecommendationService directRecommendationService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<DirectRecommendationResponseDto> create(
            @Valid @RequestBody DirectRecommendationCreateRequestDto dto,
            Authentication authentication) {
        DirectRecommendationResponseDto created = directRecommendationService.create(dto, authentication.getName());
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/direct-recommendation/get")
                .queryParam("id", created.getId())
                .build()
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<DirectRecommendationResponseDto> get(@RequestParam(name = "id") Long id,
                                                                Authentication authentication) {
        return ResponseEntity.ok(directRecommendationService.get(id, authentication.getName()));
    }

    @Override
    @GetMapping("/get-by-recommendation")
    public ResponseEntity<DirectRecommendationResponseDto> getByRecommendation(
            @RequestParam(name = "recommendationId") Long recommendationId,
            Authentication authentication) {
        return ResponseEntity.ok(directRecommendationService.getByRecommendation(recommendationId, authentication.getName()));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<DirectRecommendationResponseDto> update(@RequestParam(name = "id") Long id,
                                                                   @Valid @RequestBody DirectRecommendationPostRequestDto dto,
                                                                   Authentication authentication) {
        return ResponseEntity.ok(directRecommendationService.update(id, dto, authentication.getName()));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam(name = "id") Long id,
                                       Authentication authentication) {
        directRecommendationService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
