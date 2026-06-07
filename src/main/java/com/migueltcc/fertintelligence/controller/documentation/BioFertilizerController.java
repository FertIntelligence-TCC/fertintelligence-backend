package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerResponseDto;
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

@Tag(name = "Adubos Biológicos", description = "Endpoints para gerenciamento de adubos foliares biológicos")
@SecurityRequirement(name = "bearerAuth")
public interface BioFertilizerController {

    @Operation(summary = "Cadastrar novo adubo biológico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/register")
    ResponseEntity<BioFertilizerResponseDto> createBioFertilizer(
            @Parameter(description = "Dados para criação", required = true)
            @Valid @RequestBody BioFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar adubo por ID")
    @GetMapping("/get")
    ResponseEntity<BioFertilizerResponseDto> getBioFertilizer(
            @Parameter(description = "ID do adubo", required = true)
            @RequestParam(name = "bioFertilizerId") Long bioFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos os adubos")
    @GetMapping("/get-all")
    ResponseEntity<List<BioFertilizerResponseDto>> getAllBioFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar adubos públicos", description = "Retorna adubos públicos cadastrados na plataforma.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all-public")
    ResponseEntity<List<BioFertilizerResponseDto>> getAllPublicBioFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar adubos padrão", description = "Retorna adubos criados pelo usuário supremo.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all-default")
    ResponseEntity<List<BioFertilizerResponseDto>> getAllDefaultBioFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );


    @Operation(summary = "Buscar por nome")
    @GetMapping("/get-by-name")
    ResponseEntity<List<BioFertilizerResponseDto>> getBioFertilizersByName(
            @Parameter(description = "Nome a buscar", required = true)
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar adubo")
    @PutMapping("/update")
    ResponseEntity<BioFertilizerResponseDto> updateBioFertilizer(
            @Parameter(description = "ID do adubo a ser atualizado", required = true)
            @RequestParam(name = "bioFertilizerId") Long bioFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody BioFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Deletar adubo")
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteBioFertilizer(
            @Parameter(description = "ID do adubo a ser removido", required = true)
            @RequestParam(name = "bioFertilizerId") Long bioFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}
