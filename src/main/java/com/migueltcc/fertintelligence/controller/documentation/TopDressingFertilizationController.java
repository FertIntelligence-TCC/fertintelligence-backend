package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Adubação de Cobertura", description = "Endpoints para gerenciamento de adubações de cobertura")
@SecurityRequirement(name = "bearerAuth")
public interface TopDressingFertilizationController {

    // POST /top-dressing-fertilization/register
    ResponseEntity<TopDressingFertilizationResponseDto> createTopDressingFertilization(
            @Parameter(description = "ID da cultura associada", required = true) @RequestParam(name = "cropId") Long cropId,
            @Parameter(description = "Dados da adubação de cobertura a ser criada", required = true) @Valid @RequestBody TopDressingFertilizationCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    // GET /top-dressing-fertilization/get?fertilizationId={id}
    ResponseEntity<TopDressingFertilizationResponseDto> getTopDressingFertilization(
            @Parameter(description = "ID da adubação a ser buscada", required = true) @RequestParam(name = "fertilizationId") Long fertilizationId,
            @Parameter(hidden = true) Authentication authentication
    );

    // GET /top-dressing-fertilization/get-by-crop?cropId={id}
    ResponseEntity<List<TopDressingFertilizationResponseDto>> getTopDressingFertilizationsByCrop(
            @Parameter(description = "ID da cultura para listar as adubações", required = true) @RequestParam(name = "cropId") Long cropId,
            @Parameter(hidden = true) Authentication authentication
    );

    // PUT /top-dressing-fertilization/update?fertilizationId={id}
    ResponseEntity<TopDressingFertilizationResponseDto> updateTopDressingFertilization(
            @Parameter(description = "ID da adubação a ser atualizada", required = true) @RequestParam(name = "fertilizationId") Long fertilizationId,
            @Parameter(description = "Dados para atualização", required = true) @Valid @RequestBody TopDressingFertilizationPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    // DELETE /top-dressing-fertilization/delete?fertilizationId={id}
    ResponseEntity<Void> deleteTopDressingFertilization(
            @Parameter(description = "ID da adubação a ser excluída", required = true) @RequestParam(name = "fertilizationId") Long fertilizationId,
            @Parameter(hidden = true) Authentication authentication
    );
}