package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourcePostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Fontes Líquidas de Adubação Foliar", description = "Endpoints para gerenciamento de fontes líquidas de adubação foliar")
@SecurityRequirement(name = "bearerAuth")
public interface LiquidSourceController {

    ResponseEntity<LiquidSourceResponseDto> createLiquidSource(
            @Parameter(description = "ID da cultura associada à fonte líquida", required = true)
            @RequestParam(name = "cropId") Long cropId,
            @Parameter(description = "Dados da fonte líquida a ser criada", required = true)
            @Valid @RequestBody LiquidSourceCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<LiquidSourceResponseDto> getLiquidSource(
            @Parameter(description = "ID da fonte líquida a ser buscada", required = true)
            @RequestParam(name = "liquidSourceId") Long liquidSourceId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<LiquidSourceResponseDto>> getLiquidSourcesByCrop(
            @Parameter(description = "ID da cultura para listar as fontes líquidas", required = true)
            @RequestParam(name = "cropId") Long cropId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<LiquidSourceResponseDto> updateLiquidSource(
            @Parameter(description = "ID da fonte líquida a ser atualizada", required = true)
            @RequestParam(name = "liquidSourceId") Long liquidSourceId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody LiquidSourcePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteLiquidSource(
            @Parameter(description = "ID da fonte líquida a ser removida", required = true)
            @RequestParam(name = "liquidSourceId") Long liquidSourceId,
            @Parameter(hidden = true) Authentication authentication
    );
}