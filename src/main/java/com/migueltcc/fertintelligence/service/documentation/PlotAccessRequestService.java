package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestResponseDto;
import java.util.List;

public interface PlotAccessRequestService {

    PlotAccessRequestResponseDto requestAccess(Long propertyId, Long plotId, String username);

    List<PlotAccessRequestResponseDto> getRequestsForManager(Long propertyId, AccessRequestStatus status, String managerUsername);

    PlotAccessRequestResponseDto decideRequest(Long requestId, boolean approve, String managerUsername);

    PlotAccessRequestResponseDto revokeRequest(Long requestId, String managerUsername);
}