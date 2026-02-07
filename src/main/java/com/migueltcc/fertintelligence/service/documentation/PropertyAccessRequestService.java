package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestResponseDto;

import java.util.List;

public interface PropertyAccessRequestService {

    PropertyAccessRequestResponseDto requestAccess(Long propertyId, String username);

    List<PropertyAccessRequestResponseDto> getRequestsForProperty(Long propertyId, String ownerUsername);

    PropertyAccessRequestResponseDto decideRequest(Long requestId, boolean approve, String ownerUsername);

    List<PropertyResponseDto> getApprovedPropertiesForUser(String username);

    List<PropertyAccessRequestResponseDto> getReceivedRequests(String ownerUsername);

    void revokeAccess(Long propertyId, String username);

    boolean hasAccessToProperty(Long propertyId, String username);
}