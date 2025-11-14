package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.KExchangeableContentController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel.KExchangeableContentCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel.KExchangeableContentPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel.KExchangeableContentResponseDto;
import com.migueltcc.fertintelligence.service.documentation.KExchangeableContentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/k-exchangeable-content")
public class KExchangeableContentControllerImpl implements KExchangeableContentController {

    @Autowired
    private KExchangeableContentService kExchangeableContentService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<KExchangeableContentResponseDto> createKExchangeableContent(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody KExchangeableContentCreateRequestDto createRequestDto,
            Authentication authentication) {

        KExchangeableContentResponseDto createdCriterion = kExchangeableContentService.createKExchangeableContent(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/k-exchangeable-content/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<KExchangeableContentResponseDto> getKExchangeableContent(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        KExchangeableContentResponseDto criterion = kExchangeableContentService.getKExchangeableContentById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<KExchangeableContentResponseDto> getKExchangeableContentByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        KExchangeableContentResponseDto criterion = kExchangeableContentService.getKExchangeableContentByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<KExchangeableContentResponseDto> updateKExchangeableContent(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody KExchangeableContentPostRequestDto updateRequestDto,
            Authentication authentication) {
        KExchangeableContentResponseDto updatedCriterion = kExchangeableContentService.updateKExchangeableContent(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteKExchangeableContent(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        kExchangeableContentService.deleteKExchangeableContent(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}