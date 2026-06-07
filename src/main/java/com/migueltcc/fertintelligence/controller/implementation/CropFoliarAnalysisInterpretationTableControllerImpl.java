package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CropFoliarAnalysisInterpretationTableController;
import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CropFoliarAnalysisInterpretationTableService;
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
@RequestMapping("/crop-foliar-analysis-interpretation-table")
@CrossOrigin(origins = "*")
public class CropFoliarAnalysisInterpretationTableControllerImpl
        implements CropFoliarAnalysisInterpretationTableController {

    private static final Logger logger = LoggerFactory.getLogger(CropFoliarAnalysisInterpretationTableControllerImpl.class);
    private final CropFoliarAnalysisInterpretationTableService tableService;

    public CropFoliarAnalysisInterpretationTableControllerImpl(CropFoliarAnalysisInterpretationTableService tableService) {
        this.tableService = tableService;
    }

    private String getAuthenticatedUsername(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        return authentication.getName();
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    createCropFoliarAnalysisInterpretationTable(
            @Valid @RequestBody CropFoliarAnalysisInterpretationTableCreateRequestDto createRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        CropFoliarAnalysisInterpretationTableResponseDto table = tableService
                .createCropFoliarAnalysisInterpretationTable(createRequestDto, username);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(table.getId())
                .toUri();

        return ResponseEntity.created(location).body(table);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    getCropFoliarAnalysisInterpretationTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(tableService.getCropFoliarAnalysisInterpretationTableById(tableId, username));
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<CropFoliarAnalysisInterpretationTableResponseDto>>
    getCropFoliarAnalysisInterpretationTables(
            @RequestParam(name = "grupo", required = false) TechnicalTableGroup group,
            Authentication authentication) {

        try {
            String username = getAuthenticatedUsername(authentication);
            List<CropFoliarAnalysisInterpretationTableResponseDto> tables = tableService
                    .getAllCropFoliarAnalysisInterpretationTablesByCreator(username, group);
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            logger.error("Erro ao listar tabelas de interpretação foliar: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/get-all-public")
    public ResponseEntity<List<CropFoliarAnalysisInterpretationTableResponseDto>> getPublicCropFoliarAnalysisInterpretationTables(
            Authentication authentication) {
        getAuthenticatedUsername(authentication);
        try {
            List<CropFoliarAnalysisInterpretationTableResponseDto> tables = tableService
                    .getAllPublicCropFoliarAnalysisInterpretationTables();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            logger.error("Erro ao listar tabelas públicas de interpretação foliar: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/get-all-default")
    public ResponseEntity<List<CropFoliarAnalysisInterpretationTableResponseDto>> getDefaultCropFoliarAnalysisInterpretationTables(
            Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<CropFoliarAnalysisInterpretationTableResponseDto> tables = tableService
                .getAllDefaultCropFoliarAnalysisInterpretationTables(username);
        return ResponseEntity.ok(tables);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CropFoliarAnalysisInterpretationTableResponseDto>
    updateCropFoliarAnalysisInterpretationTable(
            @RequestParam(name = "tableId") Long tableId,
            @Valid @RequestBody CropFoliarAnalysisInterpretationTablePostRequestDto updateRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(tableService.updateCropFoliarAnalysisInterpretationTable(tableId, updateRequestDto, username));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCropFoliarAnalysisInterpretationTable(
            @RequestParam(name = "tableId") Long tableId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        tableService.deleteCropFoliarAnalysisInterpretationTable(tableId, username);
        return ResponseEntity.noContent().build();
    }
}
