package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerResponseDto;
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

@Tag(name = "Adubos Minerais Foliares", description = "Endpoints para gerenciamento de adubos minerais foliares")
@SecurityRequirement(name = "bearerAuth")
public interface MineralFertilizerController {

    @Operation(summary = "Cadastrar novo adubo mineral foliar")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/register")
    ResponseEntity<MineralFertilizerResponseDto> createMineralFertilizer(
            @Parameter(description = "Dados para criação", required = true)
            @Valid @RequestBody MineralFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar adubo por ID")
    @GetMapping("/get")
    ResponseEntity<MineralFertilizerResponseDto> getMineralFertilizer(
            @Parameter(description = "ID do adubo", required = true)
            @RequestParam(name = "mineralFertilizerId") Long mineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos os adubos")
    @GetMapping("/get-all")
    ResponseEntity<List<MineralFertilizerResponseDto>> getAllMineralFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar adubos públicos", description = "Retorna adubos públicos cadastrados na plataforma.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all-public")
    ResponseEntity<List<MineralFertilizerResponseDto>> getAllPublicMineralFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );


    @Operation(summary = "Buscar por nome")
    @GetMapping("/get-by-name")
    ResponseEntity<List<MineralFertilizerResponseDto>> getMineralFertilizersByName(
            @Parameter(description = "Nome a buscar", required = true)
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar adubo")
    @PutMapping("/update")
    ResponseEntity<MineralFertilizerResponseDto> updateMineralFertilizer(
            @Parameter(description = "ID do adubo a ser atualizado", required = true)
            @RequestParam(name = "mineralFertilizerId") Long mineralFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody MineralFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Deletar adubo")
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteMineralFertilizer(
            @Parameter(description = "ID do adubo a ser removido", required = true)
            @RequestParam(name = "mineralFertilizerId") Long mineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}