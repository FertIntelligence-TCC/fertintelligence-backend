package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerResponseDto;
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

@Tag(name = "Adubos Orgânicos", description = "Endpoints para gerenciamento de adubos orgânicos")
@SecurityRequirement(name = "bearerAuth")
public interface OrganicFertilizerController {

    @Operation(summary = "Cadastrar novo adubo orgânico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/register")
    ResponseEntity<OrganicFertilizerResponseDto> createOrganicFertilizer(
            @Parameter(description = "Dados para criação", required = true)
            @Valid @RequestBody OrganicFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @GetMapping("/get")
    ResponseEntity<OrganicFertilizerResponseDto> getOrganicFertilizer(
            @RequestParam(name = "organicFertilizerId") Long organicFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos")
    @GetMapping("/get-all")
    ResponseEntity<List<OrganicFertilizerResponseDto>> getAllOrganicFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar adubos públicos", description = "Retorna adubos públicos cadastrados na plataforma.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all-public")
    ResponseEntity<List<OrganicFertilizerResponseDto>> getAllPublicOrganicFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar adubos padrão", description = "Retorna adubos criados pelo usuário supremo.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all-default")
    ResponseEntity<List<OrganicFertilizerResponseDto>> getAllDefaultOrganicFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );


    @GetMapping("/get-by-name")
    ResponseEntity<List<OrganicFertilizerResponseDto>> getOrganicFertilizersByName(
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    @PutMapping("/update")
    ResponseEntity<OrganicFertilizerResponseDto> updateOrganicFertilizer(
            @RequestParam(name = "organicFertilizerId") Long organicFertilizerId,
            @Valid @RequestBody OrganicFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteOrganicFertilizer(
            @RequestParam(name = "organicFertilizerId") Long organicFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}
