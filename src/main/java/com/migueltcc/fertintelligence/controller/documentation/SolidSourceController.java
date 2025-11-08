package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourcePostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Fontes Sólidas de Adubação Foliar", description = "Endpoints para gerenciamento de fontes sólidas de adubação foliar")
@SecurityRequirement(name = "bearerAuth")
public interface SolidSourceController {

    ResponseEntity<SolidSourceResponseDto> createSolidSource(
            @Parameter(description = "ID da cultura associada à fonte sólida", required = true)
            @RequestParam(name = "cropId") Long cropId,
            @Parameter(description = "Dados da fonte sólida a ser criada", required = true)
            @Valid @RequestBody SolidSourceCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SolidSourceResponseDto> getSolidSource(
            @Parameter(description = "ID da fonte sólida a ser buscada", required = true)
            @RequestParam(name = "solidSourceId") Long solidSourceId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<SolidSourceResponseDto>> getSolidSourcesByCrop(
            @Parameter(description = "ID da cultura para listar as fontes sólidas", required = true)
            @RequestParam(name = "cropId") Long cropId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SolidSourceResponseDto> updateSolidSource(
            @Parameter(description = "ID da fonte sólida a ser atualizada", required = true)
            @RequestParam(name = "solidSourceId") Long solidSourceId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody SolidSourcePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteSolidSource(
            @Parameter(description = "ID da fonte sólida a ser removida", required = true)
            @RequestParam(name = "solidSourceId") Long solidSourceId,
            @Parameter(hidden = true) Authentication authentication
    );
}