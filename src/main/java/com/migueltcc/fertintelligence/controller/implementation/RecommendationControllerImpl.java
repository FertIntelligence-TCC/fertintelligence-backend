package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.RecommendationController;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.recommendation.RecommendationResponseDto;
import com.migueltcc.fertintelligence.service.documentation.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendation")
public class RecommendationControllerImpl implements RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Override
    @PostMapping("/generate")
    public ResponseEntity<RecommendationResponseDto> generate(@Valid @RequestBody RecommendationCreateRequestDto dto,
                                                              Authentication authentication) {
        return ResponseEntity.ok(recommendationService.generate(dto, authentication.getName()));
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<RecommendationResponseDto> get(@RequestParam(name = "id") Long id,
                                                          Authentication authentication) {
        return ResponseEntity.ok(recommendationService.get(id, authentication.getName()));
    }

    @Override
    @GetMapping("/print")
    public ResponseEntity<RecommendationResponseDto> preparePrint(@RequestParam(name = "id") Long id,
                                                                   Authentication authentication) {
        return ResponseEntity.ok(recommendationService.preparePrint(id, authentication.getName()));
    }

    @Override
    @GetMapping("/my")
    public ResponseEntity<List<RecommendationResponseDto>> getMine(Authentication authentication) {
        return ResponseEntity.ok(recommendationService.getMine(authentication.getName()));
    }

    @Override
    @GetMapping("/property")
    public ResponseEntity<List<RecommendationResponseDto>> getByProperty(@RequestParam(name = "propertyId") Long propertyId,
                                                                          Authentication authentication) {
        return ResponseEntity.ok(recommendationService.getByProperty(propertyId, authentication.getName()));
    }

    @Override
    @GetMapping("/plot")
    public ResponseEntity<List<RecommendationResponseDto>> getByPlot(@RequestParam(name = "plotId") Long plotId,
                                                                      Authentication authentication) {
        return ResponseEntity.ok(recommendationService.getByPlot(plotId, authentication.getName()));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam(name = "id") Long id,
                                       Authentication authentication) {
        recommendationService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
