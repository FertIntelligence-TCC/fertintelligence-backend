package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Adubos Minerais Foliares", description = "Endpoints para gerenciamento de adubos minerais foliares")
@SecurityRequirement(name = "bearerAuth")
public interface MineralFertilizerController {

    ResponseEntity<MineralFertilizerResponseDto> createMineralFertilizer(
            @Parameter(description = "Dados para criação do adubo mineral foliar", required = true)
            @Valid @RequestBody MineralFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<MineralFertilizerResponseDto> getMineralFertilizer(
            @Parameter(description = "ID do adubo mineral foliar a ser buscado", required = true)
            @RequestParam(name = "mineralFertilizerId") Long mineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<MineralFertilizerResponseDto>> getMineralFertilizersByUser(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<MineralFertilizerResponseDto>> getMineralFertilizersByName(
            @Parameter(description = "Nome (ou parte do nome) do adubo a ser buscado", required = true)
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<MineralFertilizerResponseDto> updateMineralFertilizer(
            @Parameter(description = "ID do adubo mineral foliar a ser atualizado", required = true)
            @RequestParam(name = "mineralFertilizerId") Long mineralFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody MineralFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteMineralFertilizer(
            @Parameter(description = "ID do adubo mineral foliar a ser removido", required = true)
            @RequestParam(name = "mineralFertilizerId") Long mineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}