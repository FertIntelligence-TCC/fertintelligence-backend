package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestResponseDto;

import java.util.List;

public interface PropertyAccessRequestService {

    PropertyAccessRequestResponseDto requestAccess(Long propertyId, String username);

    List<PropertyAccessRequestResponseDto> getRequestsForProperty(Long propertyId, String ownerUsername);

    PropertyAccessRequestResponseDto decideRequest(Long requestId, boolean approve, String ownerUsername);

}