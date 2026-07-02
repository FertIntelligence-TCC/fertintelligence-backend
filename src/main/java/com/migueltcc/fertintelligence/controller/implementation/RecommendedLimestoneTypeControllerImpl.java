package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.RecommendedLimestoneTypeController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.recommendedLimestoneType.RecommendedLimestoneTypeResponseDto;
import com.migueltcc.fertintelligence.service.documentation.RecommendedLimestoneTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/recommended-limestone-type")
public class RecommendedLimestoneTypeControllerImpl implements RecommendedLimestoneTypeController {

    @Autowired
    private RecommendedLimestoneTypeService recommendedLimestoneTypeService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<RecommendedLimestoneTypeResponseDto> createRecommendedLimestoneType(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody RecommendedLimestoneTypeCreateRequestDto createRequestDto,
            Authentication authentication) {
        RecommendedLimestoneTypeResponseDto createdCriterion = recommendedLimestoneTypeService.createRecommendedLimestoneType(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/recommended-limestone-type/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<RecommendedLimestoneTypeResponseDto> getRecommendedLimestoneType(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        RecommendedLimestoneTypeResponseDto criterion = recommendedLimestoneTypeService.getRecommendedLimestoneTypeById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<RecommendedLimestoneTypeResponseDto> getRecommendedLimestoneTypeByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        RecommendedLimestoneTypeResponseDto criterion = recommendedLimestoneTypeService.getRecommendedLimestoneTypeByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<RecommendedLimestoneTypeResponseDto> updateRecommendedLimestoneType(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody RecommendedLimestoneTypePostRequestDto updateRequestDto,
            Authentication authentication) {
        RecommendedLimestoneTypeResponseDto updatedCriterion = recommendedLimestoneTypeService.updateRecommendedLimestoneType(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteRecommendedLimestoneType(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        recommendedLimestoneTypeService.deleteRecommendedLimestoneType(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
