package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.ExchangeableBaseRatioController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableBaseRatio.ExchangeableBaseRatioCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableBaseRatio.ExchangeableBaseRatioPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableBaseRatio.ExchangeableBaseRatioResponseDto;
import com.migueltcc.fertintelligence.service.documentation.ExchangeableBaseRatioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/exchangeable-base-ratio")
public class ExchangeableBaseRatioControllerImpl implements ExchangeableBaseRatioController {

    @Autowired
    private ExchangeableBaseRatioService exchangeableBaseRatioService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<ExchangeableBaseRatioResponseDto> createExchangeableBaseRatio(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody ExchangeableBaseRatioCreateRequestDto createRequestDto,
            Authentication authentication) {
        ExchangeableBaseRatioResponseDto createdCriterion = exchangeableBaseRatioService.createExchangeableBaseRatio(
                tableId,
                createRequestDto,
                authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/exchangeable-base-ratio/get")
                .queryParam("criterionId", createdCriterion.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<ExchangeableBaseRatioResponseDto> getExchangeableBaseRatio(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        ExchangeableBaseRatioResponseDto criterion = exchangeableBaseRatioService.getExchangeableBaseRatioById(
                criterionId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<ExchangeableBaseRatioResponseDto> getExchangeableBaseRatioByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {
        ExchangeableBaseRatioResponseDto criterion = exchangeableBaseRatioService.getExchangeableBaseRatioByTable(
                tableId,
                authentication.getName());
        return ResponseEntity.ok(criterion);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<ExchangeableBaseRatioResponseDto> updateExchangeableBaseRatio(
            @RequestParam(name = "criterionId") Long criterionId,
            @Valid @RequestBody ExchangeableBaseRatioPostRequestDto updateRequestDto,
            Authentication authentication) {
        ExchangeableBaseRatioResponseDto updatedCriterion = exchangeableBaseRatioService.updateExchangeableBaseRatio(
                criterionId,
                updateRequestDto,
                authentication.getName());
        return ResponseEntity.ok(updatedCriterion);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteExchangeableBaseRatio(
            @RequestParam(name = "criterionId") Long criterionId,
            Authentication authentication) {
        exchangeableBaseRatioService.deleteExchangeableBaseRatio(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
