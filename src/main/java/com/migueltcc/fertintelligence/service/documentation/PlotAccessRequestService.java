package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestResponseDto;

import java.util.List;

public interface PlotAccessRequestService {

    PlotAccessRequestResponseDto requestAccess(Long propertyId, String username);

    List<PlotAccessRequestResponseDto> getRequestsForManager(Long propertyId, String managerUsername);

    PlotAccessRequestResponseDto decideRequest(Long requestId, boolean approve, String managerUsername);
}