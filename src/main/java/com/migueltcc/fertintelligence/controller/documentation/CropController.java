package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.crop.CropCreateRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropPostRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Culturas", description = "Endpoints para gerenciamento de culturas anuais")
@SecurityRequirement(name = "bearerAuth")
public interface CropController {

    ResponseEntity<CropResponseDto> createCrop(
            @Parameter(description = "ID da pasta de culturas anuais", required = true)
            @RequestParam(name = "folderId") Long folderId,
            @Parameter(description = "Dados da cultura a ser criada", required = true)
            @Valid @RequestBody CropCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CropResponseDto> getCrop(
            @Parameter(description = "ID da cultura a ser buscada", required = true)
            @RequestParam(name = "cropId") Long cropId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<CropResponseDto>> getCropsByFolder(
            @Parameter(description = "ID da pasta de culturas anuais", required = true)
            @RequestParam(name = "folderId") Long folderId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<CropResponseDto> updateCrop(
            @Parameter(description = "ID da cultura a ser atualizada", required = true)
            @RequestParam(name = "cropId") Long cropId,
            @Parameter(description = "Dados da atualização da cultura", required = true)
            @Valid @RequestBody CropPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteCrop(
            @Parameter(description = "ID da cultura a ser removida", required = true)
            @RequestParam(name = "cropId") Long cropId,
            @Parameter(hidden = true) Authentication authentication
    );
}