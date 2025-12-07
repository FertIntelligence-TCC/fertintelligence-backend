package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Adubos Minerais Formulados", description = "Endpoints para gerenciamento de adubos minerais formulados")
@SecurityRequirement(name = "bearerAuth")
public interface FormulatedMineralFertilizerController {

    ResponseEntity<FormulatedMineralFertilizerResponseDto> createFormulatedMineralFertilizer(
            @Parameter(description = "Dados para criação do adubo mineral formulado", required = true)
            @Valid @RequestBody FormulatedMineralFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<FormulatedMineralFertilizerResponseDto> getFormulatedMineralFertilizer(
            @Parameter(description = "ID do adubo mineral formulado a ser buscado", required = true)
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<FormulatedMineralFertilizerResponseDto>> getFormulatedMineralFertilizersByUser(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<FormulatedMineralFertilizerResponseDto> updateFormulatedMineralFertilizer(
            @Parameter(description = "ID do adubo mineral formulado a ser atualizado", required = true)
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody FormulatedMineralFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteFormulatedMineralFertilizer(
            @Parameter(description = "ID do adubo mineral formulado a ser removido", required = true)
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}