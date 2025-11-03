package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.AnnualCropFolderController;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderCreateRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderPostRequestDto;
import com.migueltcc.fertintelligence.dto.annualCropFolder.AnnualCropFolderResponseDto;
import com.migueltcc.fertintelligence.service.documentation.AnnualCropFolderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/annual-crop-folder")
public class AnnualCropFolderControllerImpl implements AnnualCropFolderController {

    @Autowired
    private AnnualCropFolderService annualCropFolderService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<AnnualCropFolderResponseDto> createAnnualCropFolder(
            @RequestParam(name = "plotId") Long plotId,
            @Valid @RequestBody AnnualCropFolderCreateRequestDto createRequestDto,
            Authentication authentication) {

        AnnualCropFolderResponseDto createdAnnualCropFolder = annualCropFolderService
                .createAnnualCropFolder(plotId, createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/annual-crop-folder/get")
                .queryParam("annualCropFolderId", createdAnnualCropFolder.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdAnnualCropFolder);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<AnnualCropFolderResponseDto> getAnnualCropFolder(
            @RequestParam(name = "annualCropFolderId") Long annualCropFolderId,
            Authentication authentication) {
        AnnualCropFolderResponseDto annualCropFolder = annualCropFolderService
                .getAnnualCropFolderById(annualCropFolderId, authentication.getName());
        return ResponseEntity.ok(annualCropFolder);
    }

    @Override
    @GetMapping("/get-by-plot")
    public ResponseEntity<List<AnnualCropFolderResponseDto>> getAnnualCropFoldersByPlot(
            @RequestParam(name = "plotId") Long plotId,
            Authentication authentication) {
        List<AnnualCropFolderResponseDto> annualCropFolders = annualCropFolderService
                .getAllAnnualCropFoldersByPlot(plotId, authentication.getName());
        return ResponseEntity.ok(annualCropFolders);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<AnnualCropFolderResponseDto> updateAnnualCropFolder(
            @RequestParam(name = "annualCropFolderId") Long annualCropFolderId,
            @Valid @RequestBody AnnualCropFolderPostRequestDto updateRequestDto,
            Authentication authentication) {
        AnnualCropFolderResponseDto updatedAnnualCropFolder = annualCropFolderService
                .updateAnnualCropFolder(annualCropFolderId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedAnnualCropFolder);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAnnualCropFolder(
            @RequestParam(name = "annualCropFolderId") Long annualCropFolderId,
            Authentication authentication) {
        annualCropFolderService.deleteAnnualCropFolder(annualCropFolderId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}