package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CropController;
import com.migueltcc.fertintelligence.dto.crop.CropCreateRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropPostRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CropService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/crop")
public class CropControllerImpl implements CropController {

    @Autowired
    private CropService cropService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<CropResponseDto> createCrop(
            @RequestParam(name = "folderId") Long folderId,
            @Valid @RequestBody CropCreateRequestDto createRequestDto,
            Authentication authentication) {

        CropResponseDto createdCrop = cropService.createCrop(folderId, createRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/crop/get")
                .queryParam("cropId", createdCrop.getId())
                .build().toUri();

        return ResponseEntity.created(location).body(createdCrop);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<CropResponseDto> getCrop(
            @RequestParam(name = "cropId") Long cropId,
            Authentication authentication) {
        CropResponseDto crop = cropService.getCropById(cropId, authentication.getName());
        return ResponseEntity.ok(crop);
    }

    @Override
    @GetMapping("/get-by-folder")
    public ResponseEntity<List<CropResponseDto>> getCropsByFolder(
            @RequestParam(name = "folderId") Long folderId,
            Authentication authentication) {
        // ATUALIZADO: Usa o novo método que valida permissão via Propriedade
        List<CropResponseDto> crops = cropService.getAllByAnnualCropFolderId(folderId, authentication.getName());
        return ResponseEntity.ok(crops);
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<CropResponseDto> updateCrop(
            @RequestParam(name = "cropId") Long cropId,
            @Valid @RequestBody CropPostRequestDto updateRequestDto,
            Authentication authentication) {
        CropResponseDto updatedCrop = cropService.updateCrop(cropId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedCrop);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCrop(
            @RequestParam(name = "cropId") Long cropId,
            Authentication authentication) {
        cropService.deleteCrop(cropId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}