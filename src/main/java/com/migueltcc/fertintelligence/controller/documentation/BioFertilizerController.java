package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Adubos Biológicos", description = "Endpoints para gerenciamento de adubos foliares biológicos")
@SecurityRequirement(name = "bearerAuth")
public interface BioFertilizerController {

    ResponseEntity<BioFertilizerResponseDto> createBioFertilizer(
            @Parameter(description = "Dados para criação do adubo biológico", required = true)
            @Valid @RequestBody BioFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<BioFertilizerResponseDto> getBioFertilizer(
            @Parameter(description = "ID do adubo biológico a ser buscado", required = true)
            @RequestParam(name = "bioFertilizerId") Long bioFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<BioFertilizerResponseDto>> getBioFertilizersByUser(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<BioFertilizerResponseDto>> getBioFertilizersByName(
            @Parameter(description = "Nome (ou parte do nome) do adubo biológico a ser buscado", required = true)
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<BioFertilizerResponseDto> updateBioFertilizer(
            @Parameter(description = "ID do adubo biológico a ser atualizado", required = true)
            @RequestParam(name = "bioFertilizerId") Long bioFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody BioFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteBioFertilizer(
            @Parameter(description = "ID do adubo biológico a ser removido", required = true)
            @RequestParam(name = "bioFertilizerId") Long bioFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}
