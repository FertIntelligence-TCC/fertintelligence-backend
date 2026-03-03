package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.permissions.EffectivePermissionsResponseDto;
import com.migueltcc.fertintelligence.dto.permissions.PlotSummaryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(
        name = "Permissões efetivas",
        description = "Consulta flags e relações de permissões efetivas do usuário em uma propriedade"
)
public interface EffectivePermissionsController {

    @Operation(
            summary = "Obter permissões efetivas (somente escalares)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Permissões efetivas retornadas",
                            content = @Content(schema = @Schema(implementation = EffectivePermissionsResponseDto.class))
                    )
            }
    )
    ResponseEntity<EffectivePermissionsResponseDto> getEffectivePermissions(
            @RequestParam(name = "propertyId") Long propertyId,
            Authentication authentication
    );

    @Operation(
            summary = "Listar talhões em que o usuário pode editar análises (id + identification)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de talhões com permissão de edição de análises",
                            content = @Content(schema = @Schema(implementation = PlotSummaryDto.class))
                    )
            }
    )
    ResponseEntity<List<PlotSummaryDto>> getEditableAnalysesPlots(
            @RequestParam(name = "propertyId") Long propertyId,
            Authentication authentication
    );

    @Operation(
            summary = "Listar talhões em que o usuário pode editar culturas (id + identification)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista de talhões com permissão de edição de culturas",
                            content = @Content(schema = @Schema(implementation = PlotSummaryDto.class))
                    )
            }
    )
    ResponseEntity<List<PlotSummaryDto>> getEditableCropsPlots(
            @RequestParam(name = "propertyId") Long propertyId,
            Authentication authentication
    );
}