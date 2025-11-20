package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestCreateRequestDto;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestDecisionRequestDto;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Solicitações de acesso a propriedades", description = "Endpoints para gerenciar solicitações de acesso")
@SecurityRequirement(name = "bearerAuth")
public interface PropertyAccessRequestController {

    ResponseEntity<PropertyAccessRequestResponseDto> requestAccess(
            @Parameter(description = "Dados para solicitar acesso", required = true) @Valid @RequestBody PropertyAccessRequestCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<PropertyAccessRequestResponseDto>> getRequestsForProperty(
            @Parameter(description = "ID da propriedade", required = true) @RequestParam(name = "propertyId") Long propertyId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<PropertyAccessRequestResponseDto> decideRequest(
            @Parameter(description = "ID da solicitação", required = true) @PathVariable(name = "requestId") Long requestId,
            @Parameter(description = "Decisão sobre a solicitação", required = true) @Valid @RequestBody PropertyAccessRequestDecisionRequestDto decisionRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );
}
