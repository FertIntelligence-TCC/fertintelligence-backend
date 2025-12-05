package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestDecisionRequestDto;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Solicitações de acesso a talhões", description = "Endpoints para solicitar, listar e decidir acessos a talhões")
public interface PlotAccessRequestController {

    @Operation(summary = "Solicitar acesso aos talhões", responses = {
            @ApiResponse(responseCode = "200", description = "Solicitação criada",
                    content = @Content(schema = @Schema(implementation = PlotAccessRequestResponseDto.class)))
    })
    ResponseEntity<PlotAccessRequestResponseDto> requestAccess(PlotAccessRequestCreateRequestDto createRequestDto,
                                                               Authentication authentication);

    @Operation(summary = "Listar solicitações pendentes para o gerente", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitações",
                    content = @Content(schema = @Schema(implementation = PlotAccessRequestResponseDto.class)))
    })
    ResponseEntity<List<PlotAccessRequestResponseDto>> getRequests(@RequestParam(name = "propertyId") Long propertyId,
                                                                   Authentication authentication);

    @Operation(summary = "Aceitar ou recusar solicitação", responses = {
            @ApiResponse(responseCode = "200", description = "Resultado da decisão",
                    content = @Content(schema = @Schema(implementation = PlotAccessRequestResponseDto.class)))
    })
    ResponseEntity<PlotAccessRequestResponseDto> decideRequest(@RequestParam(name = "requestId") Long requestId,
                                                               PlotAccessRequestDecisionRequestDto decisionRequestDto,
                                                               Authentication authentication);
}