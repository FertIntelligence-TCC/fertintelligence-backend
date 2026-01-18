package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Adubos Organo-Minerais", description = "Endpoints para gerenciamento de adubos organo-minerais")
@SecurityRequirement(name = "bearerAuth")
public interface OrganoMineralFertilizerController {

    @Operation(summary = "Cadastrar novo adubo organo-mineral", description = "Cria um novo registro de adubo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/register")
    ResponseEntity<OrganoMineralFertilizerResponseDto> createOrganoMineralFertilizer(
            @Parameter(description = "Dados para criação do adubo", required = true)
            @Valid @RequestBody OrganoMineralFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos", description = "Retorna todos os adubos do usuário.")
    @GetMapping("/get-all")
    ResponseEntity<List<OrganoMineralFertilizerResponseDto>> getAllOrganoMineralFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar por nome", description = "Busca adubos por nome.")
    @GetMapping("/get-by-name")
    ResponseEntity<List<OrganoMineralFertilizerResponseDto>> getOrganoMineralFertilizersByName(
            @Parameter(description = "Nome a buscar", required = true)
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar adubo", description = "Atualiza um adubo existente.")
    @PutMapping("/update")
    ResponseEntity<OrganoMineralFertilizerResponseDto> updateOrganoMineralFertilizer(
            @Parameter(description = "ID do adubo", required = true)
            @RequestParam(name = "organoMineralFertilizerId") Long organoMineralFertilizerId,
            @Parameter(description = "Dados novos", required = true)
            @Valid @RequestBody OrganoMineralFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Deletar adubo", description = "Remove um adubo.")
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteOrganoMineralFertilizer(
            @Parameter(description = "ID do adubo", required = true)
            @RequestParam(name = "organoMineralFertilizerId") Long organoMineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}