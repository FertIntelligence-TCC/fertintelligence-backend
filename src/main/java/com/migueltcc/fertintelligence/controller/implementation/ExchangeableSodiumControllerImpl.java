package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.ExchangeableSodiumController;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumResponseDto;
import com.migueltcc.fertintelligence.service.documentation.ExchangeableSodiumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/exchangeable-sodium")
public class ExchangeableSodiumControllerImpl implements ExchangeableSodiumController {

    @Autowired
    private ExchangeableSodiumService exchangeableSodiumService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<ExchangeableSodiumResponseDto> createExchangeableSodium(@RequestParam(name = "tableId") Long tableId, @Valid @RequestBody ExchangeableSodiumCreateRequestDto createRequestDto, Authentication authentication) {
        ExchangeableSodiumResponseDto createdCriterion = exchangeableSodiumService.createExchangeableSodium(tableId, createRequestDto, authentication.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/exchangeable-sodium/get").queryParam("criterionId", createdCriterion.getId()).build().toUri();
        return ResponseEntity.created(location).body(createdCriterion);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<ExchangeableSodiumResponseDto> getExchangeableSodium(@RequestParam(name = "criterionId") Long criterionId, Authentication authentication) {
        return ResponseEntity.ok(exchangeableSodiumService.getExchangeableSodiumById(criterionId, authentication.getName()));
    }

    @Override
    @GetMapping("/get-by-table")
    public ResponseEntity<ExchangeableSodiumResponseDto> getExchangeableSodiumByTable(@RequestParam(name = "tableId") Long tableId, Authentication authentication) {
        return ResponseEntity.ok(exchangeableSodiumService.getExchangeableSodiumByTable(tableId, authentication.getName()));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<ExchangeableSodiumResponseDto> updateExchangeableSodium(@RequestParam(name = "criterionId") Long criterionId, @Valid @RequestBody ExchangeableSodiumPostRequestDto updateRequestDto, Authentication authentication) {
        return ResponseEntity.ok(exchangeableSodiumService.updateExchangeableSodium(criterionId, updateRequestDto, authentication.getName()));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteExchangeableSodium(@RequestParam(name = "criterionId") Long criterionId, Authentication authentication) {
        exchangeableSodiumService.deleteExchangeableSodium(criterionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
