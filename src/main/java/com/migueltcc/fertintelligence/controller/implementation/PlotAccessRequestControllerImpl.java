package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.controller.documentation.PlotAccessRequestController;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestDecisionRequestDto;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestResponseDto;
import com.migueltcc.fertintelligence.service.documentation.PlotAccessRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plot-access")
public class PlotAccessRequestControllerImpl implements PlotAccessRequestController {

    @Autowired
    private PlotAccessRequestService plotAccessRequestService;

    @Override
    @PostMapping("/request")
    public ResponseEntity<PlotAccessRequestResponseDto> requestAccess(
            @Valid @RequestBody PlotAccessRequestCreateRequestDto createRequestDto,
            Authentication authentication) {

        PlotAccessRequestResponseDto responseDto = plotAccessRequestService.requestAccess(
                createRequestDto.getPropertyId(),
                createRequestDto.getPlotId(),
                authentication.getName());

        return ResponseEntity.ok(responseDto);
    }

    @Override
    @GetMapping("/requests")
    public ResponseEntity<List<PlotAccessRequestResponseDto>> getRequests(
            @RequestParam(name = "propertyId") Long propertyId,
            @RequestParam(name = "status", required = false) AccessRequestStatus status,
            Authentication authentication) {

        // CORREÇÃO: O parâmetro 'status' agora é repassado corretamente para o serviço
        List<PlotAccessRequestResponseDto> requests = plotAccessRequestService.getRequestsForManager(propertyId, status, authentication.getName());
        return ResponseEntity.ok(requests);
    }

    @Override
    @PostMapping("/{requestId}/decision")
    public ResponseEntity<PlotAccessRequestResponseDto> decideRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody PlotAccessRequestDecisionRequestDto decisionRequestDto,
            Authentication authentication) {

        PlotAccessRequestResponseDto response = plotAccessRequestService.decideRequest(requestId, decisionRequestDto.getApprove(), authentication.getName());
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{requestId}/revoke")
    public ResponseEntity<PlotAccessRequestResponseDto> revokeRequest(
            @PathVariable Long requestId,
            Authentication authentication) {

        PlotAccessRequestResponseDto response = plotAccessRequestService.revokeRequest(requestId, authentication.getName());
        return ResponseEntity.ok(response);
    }
}