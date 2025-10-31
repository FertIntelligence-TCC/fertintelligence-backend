package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Extratos por Camada", description = "Endpoints para gerenciamento de extratos por camada")
@SecurityRequirement(name = "bearerAuth")
public interface LayerExtractController {

    ResponseEntity<LayerExtractResponseDto> createLayerExtract(
            @Parameter(description = "ID da análise de solo para criação do extrato", required = true)
            @RequestParam(name = "analysisId") Long analysisId,
            @Parameter(description = "Dados do extrato de camada a ser criado", required = true)
            @Valid @RequestBody LayerExtractCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<LayerExtractResponseDto> getLayerExtract(
            @Parameter(description = "ID do extrato de camada a ser buscado", required = true)
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<LayerExtractResponseDto>> getLayerExtractsByAnalysis(
            @Parameter(description = "ID da análise de solo para listar os extratos", required = true)
            @RequestParam(name = "analysisId") Long analysisId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<LayerExtractResponseDto> updateLayerExtract(
            @Parameter(description = "ID do extrato de camada a ser atualizado", required = true)
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            @Parameter(description = "Dados para atualização do extrato", required = true)
            @Valid @RequestBody LayerExtractPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteLayerExtract(
            @Parameter(description = "ID do extrato de camada a ser removido", required = true)
            @RequestParam(name = "layerExtractId") Long layerExtractId,
            @Parameter(hidden = true) Authentication authentication
    );
}