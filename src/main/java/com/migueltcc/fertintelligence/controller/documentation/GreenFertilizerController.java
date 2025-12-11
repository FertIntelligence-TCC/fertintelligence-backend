package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Adubos Verdes", description = "Endpoints para gerenciamento de adubos verdes")
@SecurityRequirement(name = "bearerAuth")
public interface GreenFertilizerController {

    ResponseEntity<GreenFertilizerResponseDto> createGreenFertilizer(
            @Parameter(description = "Dados para criação do adubo verde", required = true)
            @Valid @RequestBody GreenFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<GreenFertilizerResponseDto> getGreenFertilizer(
            @Parameter(description = "ID do adubo verde a ser buscado", required = true)
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<GreenFertilizerResponseDto>> getGreenFertilizersByUser(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<GreenFertilizerResponseDto>> getGreenFertilizersByName(
            @Parameter(description = "Nome (ou parte do nome) do adubo a ser buscado", required = true)
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<GreenFertilizerResponseDto> updateGreenFertilizer(
            @Parameter(description = "ID do adubo verde a ser atualizado", required = true)
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody GreenFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteGreenFertilizer(
            @Parameter(description = "ID do adubo verde a ser removido", required = true)
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}
