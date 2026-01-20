package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CropFoliarAnalysisInterpretationTableLineController;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLinePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CropFoliarAnalysisInterpretationTableLineService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/crop-foliar-analysis-interpretation-table-line")
@CrossOrigin(origins = "*") // FIX: Permite requisições do frontend (CORS)
public class CropFoliarAnalysisInterpretationTableLineControllerImpl
        implements CropFoliarAnalysisInterpretationTableLineController {

    private static final Logger logger = LoggerFactory.getLogger(CropFoliarAnalysisInterpretationTableLineControllerImpl.class);
    private final CropFoliarAnalysisInterpretationTableLineService tableLineService;

    public CropFoliarAnalysisInterpretationTableLineControllerImpl(CropFoliarAnalysisInterpretationTableLineService tableLineService) {
        this.tableLineService = tableLineService;
    }

    private String getAuthenticatedUsername(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        return authentication.getName();
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableLineResponseDto>
    createCropFoliarAnalysisInterpretationTableLine(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody CropFoliarAnalysisInterpretationTableLineCreateRequestDto createRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        CropFoliarAnalysisInterpretationTableLineResponseDto line = tableLineService
                .createCropFoliarAnalysisInterpretationTableLine(tableId, createRequestDto, username);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(line.getId())
                .toUri();

        return ResponseEntity.created(location).body(line);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableLineResponseDto>
    getCropFoliarAnalysisInterpretationTableLine(
            @RequestParam(name = "lineId") Long lineId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(tableLineService.getCropFoliarAnalysisInterpretationTableLineById(lineId, username));
    }

    @Override
    @GetMapping("/get-by-table") // FIX: Nome da rota corrigida para evitar erro 404/500
    public ResponseEntity<List<CropFoliarAnalysisInterpretationTableLineResponseDto>>
    getCropFoliarAnalysisInterpretationTableLinesByTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {

        try {
            String username = getAuthenticatedUsername(authentication);
            List<CropFoliarAnalysisInterpretationTableLineResponseDto> lines = tableLineService
                    .getAllCropFoliarAnalysisInterpretationTableLinesByTable(tableId, username);
            return ResponseEntity.ok(lines);
        } catch (Exception e) {
            logger.error("Erro ao listar linhas da tabela foliar: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableLineResponseDto>
    updateCropFoliarAnalysisInterpretationTableLine(
            @RequestParam(name = "lineId") Long lineId,
            @Valid @RequestBody CropFoliarAnalysisInterpretationTableLinePostRequestDto updateRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        CropFoliarAnalysisInterpretationTableLineResponseDto updatedLine = tableLineService
                .updateCropFoliarAnalysisInterpretationTableLine(lineId, updateRequestDto, username);
        return ResponseEntity.ok(updatedLine);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCropFoliarAnalysisInterpretationTableLine(
            @RequestParam(name = "lineId") Long lineId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        tableLineService.deleteCropFoliarAnalysisInterpretationTableLine(lineId, username);
        return ResponseEntity.noContent().build();
    }
}