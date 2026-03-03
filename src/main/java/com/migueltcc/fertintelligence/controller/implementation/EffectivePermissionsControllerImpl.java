package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.EffectivePermissionsController;
import com.migueltcc.fertintelligence.dto.permissions.EffectivePermissionsResponseDto;
import com.migueltcc.fertintelligence.dto.permissions.PlotSummaryDto;
import com.migueltcc.fertintelligence.service.documentation.EffectivePermissionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/property-permissions")
public class EffectivePermissionsControllerImpl implements EffectivePermissionsController {

    private final EffectivePermissionsService effectivePermissionsService;

    @Override
    @GetMapping("/effective")
    public ResponseEntity<EffectivePermissionsResponseDto> getEffectivePermissions(
            @RequestParam(name = "propertyId") Long propertyId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                effectivePermissionsService.getEffectivePermissions(propertyId, authentication.getName())
        );
    }

    @Override
    @GetMapping("/effective/plots/analyses")
    public ResponseEntity<List<PlotSummaryDto>> getEditableAnalysesPlots(
            @RequestParam(name = "propertyId") Long propertyId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                effectivePermissionsService.getEditableAnalysesPlots(propertyId, authentication.getName())
        );
    }

    @Override
    @GetMapping("/effective/plots/crops")
    public ResponseEntity<List<PlotSummaryDto>> getEditableCropsPlots(
            @RequestParam(name = "propertyId") Long propertyId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                effectivePermissionsService.getEditableCropsPlots(propertyId, authentication.getName())
        );
    }
}