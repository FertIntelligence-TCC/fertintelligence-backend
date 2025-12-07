package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Adubos Organo-Minerais", description = "Endpoints para gerenciamento de adubos organo-minerais")
@SecurityRequirement(name = "bearerAuth")
public interface OrganoMineralFertilizerController {

    ResponseEntity<OrganoMineralFertilizerResponseDto> createOrganoMineralFertilizer(
            @Parameter(description = "Dados para criação do adubo organo-mineral", required = true)
            @Valid @RequestBody OrganoMineralFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<OrganoMineralFertilizerResponseDto> getOrganoMineralFertilizer(
            @Parameter(description = "ID do adubo organo-mineral a ser buscado", required = true)
            @RequestParam(name = "organoMineralFertilizerId") Long organoMineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<OrganoMineralFertilizerResponseDto>> getOrganoMineralFertilizersByUser(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<OrganoMineralFertilizerResponseDto>> getOrganoMineralFertilizersByName(
            @Parameter(description = "Nome (ou parte do nome) do adubo a ser buscado", required = true)
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<OrganoMineralFertilizerResponseDto> updateOrganoMineralFertilizer(
            @Parameter(description = "ID do adubo organo-mineral a ser atualizado", required = true)
            @RequestParam(name = "organoMineralFertilizerId") Long organoMineralFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody OrganoMineralFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteOrganoMineralFertilizer(
            @Parameter(description = "ID do adubo organo-mineral a ser removido", required = true)
            @RequestParam(name = "organoMineralFertilizerId") Long organoMineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}
