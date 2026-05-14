package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerResponseDto;
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

@Tag(name = "Adubos Quelatados", description = "Endpoints para gerenciamento de adubos quelatados")
@SecurityRequirement(name = "bearerAuth")
public interface ChelatedFertilizerController {

    @Operation(summary = "Cadastrar novo adubo quelatado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/register")
    ResponseEntity<ChelatedFertilizerResponseDto> createChelatedFertilizer(
            @Parameter(description = "Dados para criação", required = true)
            @Valid @RequestBody ChelatedFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar adubo por ID")
    @GetMapping("/get")
    ResponseEntity<ChelatedFertilizerResponseDto> getChelatedFertilizer(
            @Parameter(description = "ID do adubo", required = true)
            @RequestParam(name = "chelatedFertilizerId") Long chelatedFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos os adubos")
    @GetMapping("/get-all")
    ResponseEntity<List<ChelatedFertilizerResponseDto>> getAllChelatedFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar adubos públicos", description = "Retorna somente adubos com publico=true cadastrados na plataforma.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all-public")
    ResponseEntity<List<ChelatedFertilizerResponseDto>> getAllPublicChelatedFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar por nome")
    @GetMapping("/get-by-name")
    ResponseEntity<List<ChelatedFertilizerResponseDto>> getChelatedFertilizersByName(
            @Parameter(description = "Nome a buscar", required = true)
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );
    @Operation(summary = "Atualizar adubo")
    @PutMapping("/update")
    ResponseEntity<ChelatedFertilizerResponseDto> updateChelatedFertilizer(
            @Parameter(description = "ID do adubo a ser atualizado", required = true)
            @RequestParam(name = "chelatedFertilizerId") Long chelatedFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody ChelatedFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );
    @Operation(summary = "Deletar adubo")
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteChelatedFertilizer(
            @Parameter(description = "ID do adubo a ser removido", required = true)
            @RequestParam(name = "chelatedFertilizerId") Long chelatedFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}