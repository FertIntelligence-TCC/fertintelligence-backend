package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerResponseDto;
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

@Tag(name = "Adubos Verdes", description = "Endpoints para gerenciamento de adubos verdes")
@SecurityRequirement(name = "bearerAuth")
public interface GreenFertilizerController {

    @Operation(summary = "Cadastrar novo adubo verde")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/register")
    ResponseEntity<GreenFertilizerResponseDto> createGreenFertilizer(
            @Parameter(description = "Dados para criação", required = true)
            @Valid @RequestBody GreenFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @GetMapping("/get")
    ResponseEntity<GreenFertilizerResponseDto> getGreenFertilizer(
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos")
    @GetMapping("/get-all")
    ResponseEntity<List<GreenFertilizerResponseDto>> getAllGreenFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar adubos públicos", description = "Retorna adubos públicos cadastrados na plataforma.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all-public")
    ResponseEntity<List<GreenFertilizerResponseDto>> getAllPublicGreenFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );


    @GetMapping("/get-by-name")
    ResponseEntity<List<GreenFertilizerResponseDto>> getGreenFertilizersByName(
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    @PutMapping("/update")
    ResponseEntity<GreenFertilizerResponseDto> updateGreenFertilizer(
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            @Valid @RequestBody GreenFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteGreenFertilizer(
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}