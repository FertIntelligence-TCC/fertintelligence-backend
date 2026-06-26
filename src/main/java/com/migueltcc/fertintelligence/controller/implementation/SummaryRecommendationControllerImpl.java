package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.SummaryRecommendationController;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationPostRequestDto;
import com.migueltcc.fertintelligence.dto.summaryRecommendation.SummaryRecommendationResponseDto;
import com.migueltcc.fertintelligence.service.documentation.SummaryRecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/summary-recommendation")
public class SummaryRecommendationControllerImpl implements SummaryRecommendationController {

    @Autowired
    private SummaryRecommendationService summaryRecommendationService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<SummaryRecommendationResponseDto> create(
            @Valid @RequestBody SummaryRecommendationCreateRequestDto dto,
            Authentication authentication) {
        SummaryRecommendationResponseDto created = summaryRecommendationService.create(dto, authentication.getName());
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/summary-recommendation/get")
                .queryParam("id", created.getId())
                .build()
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<SummaryRecommendationResponseDto> get(@RequestParam(name = "id") Long id,
                                                                 Authentication authentication) {
        return ResponseEntity.ok(summaryRecommendationService.get(id, authentication.getName()));
    }

    @Override
    @GetMapping("/get-by-recommendation")
    public ResponseEntity<SummaryRecommendationResponseDto> getByRecommendation(
            @RequestParam(name = "recommendationId") Long recommendationId,
            Authentication authentication) {
        return ResponseEntity.ok(summaryRecommendationService.getByRecommendation(recommendationId, authentication.getName()));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<SummaryRecommendationResponseDto> update(@RequestParam(name = "id") Long id,
                                                                    @Valid @RequestBody SummaryRecommendationPostRequestDto dto,
                                                                    Authentication authentication) {
        return ResponseEntity.ok(summaryRecommendationService.update(id, dto, authentication.getName()));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam(name = "id") Long id,
                                       Authentication authentication) {
        summaryRecommendationService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
