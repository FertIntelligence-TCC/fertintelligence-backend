package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
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

@Tag(name = "Adubos Minerais Formulados", description = "Endpoints para gerenciamento de adubos minerais formulados (NPK)")
@SecurityRequirement(name = "bearerAuth")
public interface FormulatedMineralFertilizerController {

    @Operation(summary = "Cadastrar novo adubo formulado", description = "Cria um novo registro de adubo formulado associado ao usuário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Adubo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping("/register")
    ResponseEntity<FormulatedMineralFertilizerResponseDto> createFormulatedMineralFertilizer(
            @Parameter(description = "Dados para criação do adubo", required = true)
            @Valid @RequestBody FormulatedMineralFertilizerCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar adubo por ID", description = "Retorna os detalhes de um adubo específico.")
    @GetMapping("/get")
    ResponseEntity<FormulatedMineralFertilizerResponseDto> getFormulatedMineralFertilizer(
            @Parameter(description = "ID do adubo", required = true)
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar todos os adubos", description = "Retorna todos os adubos formulados do usuário.")
    @GetMapping("/get-all")
    ResponseEntity<List<FormulatedMineralFertilizerResponseDto>> getAllFormulatedMineralFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar adubos públicos", description = "Retorna somente adubos com publico=true cadastrados na plataforma.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping("/get-all-public")
    ResponseEntity<List<FormulatedMineralFertilizerResponseDto>> getAllPublicFormulatedMineralFertilizers(
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar adubo", description = "Atualiza os dados de um adubo existente.")
    @PutMapping("/update")
    ResponseEntity<FormulatedMineralFertilizerResponseDto> updateFormulatedMineralFertilizer(
            @Parameter(description = "ID do adubo a ser atualizado", required = true)
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody FormulatedMineralFertilizerPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );
    @Operation(summary = "Deletar adubo", description = "Remove um adubo do sistema.")
    @DeleteMapping("/delete")
    ResponseEntity<Void> deleteFormulatedMineralFertilizer(
            @Parameter(description = "ID do adubo a ser removido", required = true)
            @RequestParam(name = "formulatedMineralFertilizerId") Long formulatedMineralFertilizerId,
            @Parameter(hidden = true) Authentication authentication
    );
}