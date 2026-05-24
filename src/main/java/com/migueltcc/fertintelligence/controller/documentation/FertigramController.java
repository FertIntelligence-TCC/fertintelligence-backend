package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.fertigram.FertigramResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Fertigrama", description = "Endpoints para geração de fertigrama")
@SecurityRequirement(name = "bearerAuth")
public interface FertigramController {

    ResponseEntity<FertigramResponseDto> generate(
            @Parameter(description = "ID da análise foliar", required = true)
            @RequestParam(name = "foliarAnalysisId") Long foliarAnalysisId,
            @Parameter(description = "ID da tabela de interpretação foliar", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );
}
