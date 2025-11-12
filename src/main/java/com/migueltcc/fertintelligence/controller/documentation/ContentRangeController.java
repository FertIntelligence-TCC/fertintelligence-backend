package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Intervalos de Teor", description = "Endpoints para gerenciamento de intervalos de teor das tabelas de adubação")
@SecurityRequirement(name = "bearerAuth")
public interface ContentRangeController {

    ResponseEntity<ContentRangeResponseDto> createContentRange(
            @Parameter(description = "ID da tabela de adubação associada", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(description = "Dados para criação do intervalo de teor", required = true)
            @Valid @RequestBody ContentRangeCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<ContentRangeResponseDto> getContentRange(
            @Parameter(description = "ID do intervalo de teor a ser buscado", required = true)
            @RequestParam(name = "contentRangeId") Long contentRangeId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<ContentRangeResponseDto>> getContentRangesByTable(
            @Parameter(description = "ID da tabela de adubação", required = true)
            @RequestParam(name = "tableId") Long tableId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<ContentRangeResponseDto> updateContentRange(
            @Parameter(description = "ID do intervalo de teor a ser atualizado", required = true)
            @RequestParam(name = "contentRangeId") Long contentRangeId,
            @Parameter(description = "Dados para atualização do intervalo", required = true)
            @Valid @RequestBody ContentRangePostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteContentRange(
            @Parameter(description = "ID do intervalo de teor a ser removido", required = true)
            @RequestParam(name = "contentRangeId") Long contentRangeId,
            @Parameter(hidden = true) Authentication authentication
    );
}
