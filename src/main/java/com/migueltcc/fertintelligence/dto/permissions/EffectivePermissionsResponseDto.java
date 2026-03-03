package com.migueltcc.fertintelligence.dto.permissions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EffectivePermissionsResponseDto {

    private Long propertyId;

    // autoridade
    private boolean canManageProperty;

    // globais
    private boolean canEditAllPlotsAnalyses;
    private boolean canEditAllPlotsCrops;

    // metadados escalares (sem lista)
    private int plotsEditableAnalysesCount;
    private int plotsEditableCropsCount;
}