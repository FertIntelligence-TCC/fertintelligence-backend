package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderCreateRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderPostRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Pastas de Culturas Anuais", description = "Endpoints para gerenciamento de pastas de culturas anuais")
@SecurityRequirement(name = "bearerAuth")
public interface AnnualCropFolderController {

    @Operation(summary = "Criar uma nova pasta de cultura anual")
    ResponseEntity<AnnualCropFolderResponseDto> createAnnualCropFolder(
            @Parameter(description = "ID do talhão ao qual a pasta de cultura anual pertence", required = true)
            @RequestParam(name = "plotId") Long plotId,
            @Parameter(description = "Dados da pasta de cultura anual a ser criada", required = true)
            @Valid @RequestBody AnnualCropFolderCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Buscar uma pasta de cultura anual pelo ID")
    ResponseEntity<AnnualCropFolderResponseDto> getAnnualCropFolder(
            @Parameter(description = "ID da pasta de cultura anual a ser buscada", required = true)
            @RequestParam(name = "annualCropFolderId") Long annualCropFolderId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Listar pastas de culturas anuais por talhão")
    ResponseEntity<List<AnnualCropFolderResponseDto>> getAnnualCropFoldersByPlot(
            @Parameter(description = "ID do talhão para listar as pastas de culturas anuais", required = true)
            @RequestParam(name = "plotId") Long plotId,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Atualizar uma pasta de cultura anual")
    ResponseEntity<AnnualCropFolderResponseDto> updateAnnualCropFolder(
            @Parameter(description = "ID da pasta de cultura anual a ser atualizada", required = true)
            @RequestParam(name = "annualCropFolderId") Long annualCropFolderId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody AnnualCropFolderPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(summary = "Deletar uma pasta de cultura anual")
    ResponseEntity<Void> deleteAnnualCropFolder(
            @Parameter(description = "ID da pasta de cultura anual a ser removida", required = true)
            @RequestParam(name = "annualCropFolderId") Long annualCropFolderId,
            @Parameter(hidden = true) Authentication authentication
    );
}