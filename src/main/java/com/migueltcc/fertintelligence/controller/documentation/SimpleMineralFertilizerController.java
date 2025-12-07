package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Adubos Minerais Simples", description = "Endpoints para gerenciamento de adubos minerais simples")
@SecurityRequirement(name = "bearerAuth")
public interface SimpleMineralFertilizerController {

    ResponseEntity<SimpleMineralFertilizerResponseDto> createSimpleMineralFertilizer(
            @Parameter(description = "Dados para criação do adubo mineral simples", required = true)
            @Valid @RequestBody SimpleMineralFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SimpleMineralFertilizerResponseDto> getSimpleMineralFertilizer(
            @Parameter(description = "ID do adubo mineral simples a ser buscado", required = true)
            @RequestParam(name = "simpleMineralFertilizerId") Long simpleMineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<SimpleMineralFertilizerResponseDto>> getSimpleMineralFertilizersByUser(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<SimpleMineralFertilizerResponseDto> updateSimpleMineralFertilizer(
            @Parameter(description = "ID do adubo mineral simples a ser atualizado", required = true)
            @RequestParam(name = "simpleMineralFertilizerId") Long simpleMineralFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody SimpleMineralFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteSimpleMineralFertilizer(
            @Parameter(description = "ID do adubo mineral simples a ser removido", required = true)
            @RequestParam(name = "simpleMineralFertilizerId") Long simpleMineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}