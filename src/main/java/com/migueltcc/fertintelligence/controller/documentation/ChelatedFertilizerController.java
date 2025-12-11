package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Adubos Quelatados", description = "Endpoints para gerenciamento de adubos quelatados")
@SecurityRequirement(name = "bearerAuth")
public interface ChelatedFertilizerController {

    ResponseEntity<ChelatedFertilizerResponseDto> createChelatedFertilizer(
            @Parameter(description = "Dados para criação do adubo quelatado", required = true)
            @Valid @RequestBody ChelatedFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<ChelatedFertilizerResponseDto> getChelatedFertilizer(
            @Parameter(description = "ID do adubo quelatado a ser buscado", required = true)
            @RequestParam(name = "chelatedFertilizerId") Long chelatedFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<ChelatedFertilizerResponseDto>> getChelatedFertilizersByUser(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<ChelatedFertilizerResponseDto>> getChelatedFertilizersByName(
            @Parameter(description = "Nome (ou parte do nome) do adubo a ser buscado", required = true)
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<ChelatedFertilizerResponseDto> updateChelatedFertilizer(
            @Parameter(description = "ID do adubo quelatado a ser atualizado", required = true)
            @RequestParam(name = "chelatedFertilizerId") Long chelatedFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody ChelatedFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteChelatedFertilizer(
            @Parameter(description = "ID do adubo quelatado a ser removido", required = true)
            @RequestParam(name = "chelatedFertilizerId") Long chelatedFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}
