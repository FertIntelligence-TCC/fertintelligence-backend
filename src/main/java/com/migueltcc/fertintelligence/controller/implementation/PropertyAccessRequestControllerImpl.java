package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.PropertyAccessRequestController;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestCreateRequestDto;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestDecisionRequestDto;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestResponseDto;
import com.migueltcc.fertintelligence.service.documentation.PropertyAccessRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/property-access")
public class PropertyAccessRequestControllerImpl implements PropertyAccessRequestController {

    @Autowired
    private PropertyAccessRequestService propertyAccessRequestService;

    @Override
    @PostMapping("/request")
    public ResponseEntity<PropertyAccessRequestResponseDto> requestAccess(
            @Valid @RequestBody PropertyAccessRequestCreateRequestDto createRequestDto,
            Authentication authentication) {
        PropertyAccessRequestResponseDto responseDto = propertyAccessRequestService.requestAccess(createRequestDto.getPropertyId(), authentication.getName());
        return ResponseEntity.ok(responseDto);
    }

    @Override
    @GetMapping("/requests")
    public ResponseEntity<List<PropertyAccessRequestResponseDto>> getRequestsForProperty(
            @RequestParam(name = "propertyId") Long propertyId,
            Authentication authentication) {
        List<PropertyAccessRequestResponseDto> responses = propertyAccessRequestService.getRequestsForProperty(propertyId, authentication.getName());
        return ResponseEntity.ok(responses);
    }

    @Override
    @PostMapping("/{requestId}/decision")
    public ResponseEntity<PropertyAccessRequestResponseDto> decideRequest(
            @PathVariable(name = "requestId") Long requestId,
            @Valid @RequestBody PropertyAccessRequestDecisionRequestDto decisionRequestDto,
            Authentication authentication) {
        PropertyAccessRequestResponseDto responseDto = propertyAccessRequestService.decideRequest(requestId, decisionRequestDto.getApprove(), authentication.getName());
        return ResponseEntity.ok(responseDto);
    }
}