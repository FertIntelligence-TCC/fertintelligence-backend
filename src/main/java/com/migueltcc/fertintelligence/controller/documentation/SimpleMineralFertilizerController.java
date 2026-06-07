package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Adubos Minerais Simples", description = "Endpoints para gerenciamento de adubos minerais simples")
@SecurityRequirement(name = "bearerAuth")
public interface SimpleMineralFertilizerController {

    @Operation(summary = "Cadastrar novo adubo mineral simples", description = "Cria um novo registro de adubo associado ao usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Adubo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
    })
    @PostMapping("/register")
    ResponseEntity<SimpleMineralFertilizerResponseDto> createSimpleMineralFertilizer(
            @Parameter(description = "Dados para criação do adubo mineral simples", required = true)
            @Valid @RequestBody SimpleMineralFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos os adubos", description = "Retorna todos os adubos minerais simples cadastrados pelo usuário autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all")
    ResponseEntity<List<SimpleMineralFertilizerResponseDto>> getSimpleMineralFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar adubos públicos", description = "Retorna adubos públicos cadastrados na plataforma.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all-public")
    ResponseEntity<List<SimpleMineralFertilizerResponseDto>> getAllPublicSimpleMineralFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar adubos padrão", description = "Retorna adubos criados pelo usuário supremo.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all-default")
    ResponseEntity<List<SimpleMineralFertilizerResponseDto>> getAllDefaultSimpleMineralFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );


    @Operation(summary = "Buscar por nome", description = "Busca adubos que contenham o texto fornecido no nome.")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping("/get-by-name")
    ResponseEntity<List<SimpleMineralFertilizerResponseDto>> getSimpleMineralFertilizersByName(
            @Parameter(description = "Nome (ou parte do nome) do adubo a ser buscado", required = true)
            @RequestParam(name = "name") String name,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar adubo", description = "Atualiza os dados de um adubo existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adubo atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Adubo não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/update")
    ResponseEntity<SimpleMineralFertilizerResponseDto> updateSimpleMineralFertilizer(
            @Parameter(description = "ID do adubo mineral simples a ser atualizado", required = true)
            @RequestParam(name = "simpleMineralFertilizerId") Long simpleMineralFertilizerId,
            @Parameter(description = "Dados para atualização (prefixo 'novo_')", required = true)
            @Valid @RequestBody SimpleMineralFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Deletar adubo", description = "Remove um adubo do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Adubo removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Adubo não encontrado")
    })
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteSimpleMineralFertilizer(
            @Parameter(description = "ID do adubo mineral simples a ser removido", required = true)
            @RequestParam(name = "simpleMineralFertilizerId") Long simpleMineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}
