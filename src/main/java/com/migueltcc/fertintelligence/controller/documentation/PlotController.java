package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.plot.PlotCreateRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotPostRequestDto;
import com.migueltcc.fertintelligence.dto.plot.PlotResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Talhões", description = "Endpoints para gerenciamento de talhões")
@SecurityRequirement(name = "bearerAuth")
public interface PlotController {

    ResponseEntity<PlotResponseDto> createPlot(
            @Parameter(description = "ID da propriedade onde o talhão será criado", required = true)
            @RequestParam(name = "propertyId") Long propertyId,
            @Parameter(description = "Dados do talhão a ser criado", required = true)
            @Valid @RequestBody PlotCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<PlotResponseDto> getPlot(
            @Parameter(description = "ID do talhão a ser buscado", required = true)
            @RequestParam(name = "plotId") Long plotId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<PlotResponseDto>> getPlotsByProperty(
            @Parameter(description = "ID da propriedade para listar os talhões", required = true)
            @RequestParam(name = "propertyId") Long propertyId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<PlotResponseDto> updatePlot(
            @Parameter(description = "ID do talhão a ser atualizado", required = true)
            @RequestParam(name = "plotId") Long plotId,
            @Parameter(description = "Dados para atualização", required = true)
            @Valid @RequestBody PlotPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deletePlot(
            @Parameter(description = "ID do talhão a ser removido", required = true)
            @RequestParam(name = "plotId") Long plotId,
            @Parameter(hidden = true) Authentication authentication
    );
}