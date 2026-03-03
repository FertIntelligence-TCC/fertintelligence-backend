package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestDecisionRequestDto;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Solicitações de acesso a talhões", description = "Endpoints para solicitar, listar e decidir acessos a talhões")
public interface PlotAccessRequestController {

    @Operation(summary = "Solicitar permissão para editar recursos em talhões", responses = {
            @ApiResponse(responseCode = "200", description = "Solicitação criada",
                    content = @Content(schema = @Schema(implementation = PlotAccessRequestResponseDto.class)))
    })
    ResponseEntity<PlotAccessRequestResponseDto> requestAccess(
            @Valid @RequestBody PlotAccessRequestCreateRequestDto createRequestDto,
            Authentication authentication
    );

    @Operation(summary = "Listar solicitações para o gerente/owner (com filtro opcional por status)", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitações",
                    content = @Content(schema = @Schema(implementation = PlotAccessRequestResponseDto.class)))
    })
    ResponseEntity<List<PlotAccessRequestResponseDto>> getRequests(
            @RequestParam(name = "propertyId") Long propertyId,
            @RequestParam(name = "status", required = false) AccessRequestStatus status,
            Authentication authentication
    );

    @Operation(summary = "Aceitar ou recusar solicitação", responses = {
            @ApiResponse(responseCode = "200", description = "Resultado da decisão",
                    content = @Content(schema = @Schema(implementation = PlotAccessRequestResponseDto.class)))
    })
    ResponseEntity<PlotAccessRequestResponseDto> decideRequest(
            @PathVariable(name = "requestId") Long requestId,
            @Valid @RequestBody PlotAccessRequestDecisionRequestDto decisionRequestDto,
            Authentication authentication
    );

    @Operation(summary = "Revogar uma solicitação aprovada (APPROVED -> REVOKED)", responses = {
            @ApiResponse(responseCode = "200", description = "Solicitação revogada",
                    content = @Content(schema = @Schema(implementation = PlotAccessRequestResponseDto.class)))
    })
    ResponseEntity<PlotAccessRequestResponseDto> revokeRequest(
            @PathVariable(name = "requestId") Long requestId,
            Authentication authentication
    );
}