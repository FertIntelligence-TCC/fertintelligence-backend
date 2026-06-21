package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumPostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Critérios - Sódio Trocável", description = "Endpoints para gerenciamento dos critérios de sódio trocável")
@SecurityRequirement(name = "bearerAuth")
public interface ExchangeableSodiumController {

    ResponseEntity<ExchangeableSodiumResponseDto> createExchangeableSodium(@Parameter(description = "ID da tabela associada", required = true) @RequestParam(name = "tableId") Long tableId, @Parameter(description = "Dados para criação do critério", required = true) @Valid @RequestBody ExchangeableSodiumCreateRequestDto createRequestDto, @Parameter(hidden = true) Authentication authentication);

    ResponseEntity<ExchangeableSodiumResponseDto> getExchangeableSodium(@Parameter(description = "ID do critério", required = true) @RequestParam(name = "criterionId") Long criterionId, @Parameter(hidden = true) Authentication authentication);

    ResponseEntity<ExchangeableSodiumResponseDto> getExchangeableSodiumByTable(@Parameter(description = "ID da tabela associada", required = true) @RequestParam(name = "tableId") Long tableId, @Parameter(hidden = true) Authentication authentication);

    ResponseEntity<ExchangeableSodiumResponseDto> updateExchangeableSodium(@Parameter(description = "ID do critério", required = true) @RequestParam(name = "criterionId") Long criterionId, @Parameter(description = "Dados para atualização do critério", required = true) @Valid @RequestBody ExchangeableSodiumPostRequestDto updateRequestDto, @Parameter(hidden = true) Authentication authentication);

    ResponseEntity<Void> deleteExchangeableSodium(@Parameter(description = "ID do critério", required = true) @RequestParam(name = "criterionId") Long criterionId, @Parameter(hidden = true) Authentication authentication);
}
