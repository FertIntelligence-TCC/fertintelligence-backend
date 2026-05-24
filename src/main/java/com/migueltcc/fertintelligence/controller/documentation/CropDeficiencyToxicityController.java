package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.CropDeficiencyToxicityCreateRequestDto;
import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.CropDeficiencyToxicityPostRequestDto;
import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.CropDeficiencyToxicityResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Deficiências/Toxidez", description = "Endpoints para gerenciamento de deficiências e toxidez da cultura")
@SecurityRequirement(name = "bearerAuth")
public interface CropDeficiencyToxicityController {
    ResponseEntity<CropDeficiencyToxicityResponseDto> create(@RequestParam(name = "cropId") Long cropId, @Valid @RequestBody CropDeficiencyToxicityCreateRequestDto dto, @Parameter(hidden = true) Authentication authentication);
    ResponseEntity<CropDeficiencyToxicityResponseDto> get(@RequestParam(name = "deficiencyToxicityId") Long deficiencyToxicityId, @Parameter(hidden = true) Authentication authentication);
    ResponseEntity<List<CropDeficiencyToxicityResponseDto>> getByCrop(@RequestParam(name = "cropId") Long cropId, @Parameter(hidden = true) Authentication authentication);
    ResponseEntity<CropDeficiencyToxicityResponseDto> update(@RequestParam(name = "deficiencyToxicityId") Long deficiencyToxicityId, @Valid @RequestBody CropDeficiencyToxicityPostRequestDto dto, @Parameter(hidden = true) Authentication authentication);
    ResponseEntity<Void> delete(@RequestParam(name = "deficiencyToxicityId") Long deficiencyToxicityId, @Parameter(hidden = true) Authentication authentication);
}
