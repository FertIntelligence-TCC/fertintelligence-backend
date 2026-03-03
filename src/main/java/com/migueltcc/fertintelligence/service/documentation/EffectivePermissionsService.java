package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.permissions.EffectivePermissionsResponseDto;
import com.migueltcc.fertintelligence.dto.permissions.PlotSummaryDto;

import java.util.List;

public interface EffectivePermissionsService {

    EffectivePermissionsResponseDto getEffectivePermissions(Long propertyId, String username);

    List<PlotSummaryDto> getEditableAnalysesPlots(Long propertyId, String username);

    List<PlotSummaryDto> getEditableCropsPlots(Long propertyId, String username);
}