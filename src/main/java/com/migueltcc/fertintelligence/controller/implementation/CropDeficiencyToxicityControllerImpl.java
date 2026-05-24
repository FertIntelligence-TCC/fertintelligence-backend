package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.CropDeficiencyToxicityController;
import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.CropDeficiencyToxicityCreateRequestDto;
import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.CropDeficiencyToxicityPostRequestDto;
import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.CropDeficiencyToxicityResponseDto;
import com.migueltcc.fertintelligence.service.documentation.CropDeficiencyToxicityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/crop-deficiency-toxicity")
@RequiredArgsConstructor
public class CropDeficiencyToxicityControllerImpl implements CropDeficiencyToxicityController {
    private final CropDeficiencyToxicityService service;

    @PostMapping("/register")
    public ResponseEntity<CropDeficiencyToxicityResponseDto> create(@RequestParam(name = "cropId") Long cropId, @Valid @RequestBody CropDeficiencyToxicityCreateRequestDto dto, Authentication authentication){
        CropDeficiencyToxicityResponseDto created = service.create(cropId, dto, authentication.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/crop-deficiency-toxicity/get").queryParam("deficiencyToxicityId", created.getId()).build().toUri();
        return ResponseEntity.created(location).body(created);
    }
    @GetMapping("/get")
    public ResponseEntity<CropDeficiencyToxicityResponseDto> get(@RequestParam(name = "deficiencyToxicityId") Long deficiencyToxicityId, Authentication authentication){return ResponseEntity.ok(service.getById(deficiencyToxicityId, authentication.getName()));}
    @GetMapping("/get-by-crop")
    public ResponseEntity<List<CropDeficiencyToxicityResponseDto>> getByCrop(@RequestParam(name = "cropId") Long cropId, Authentication authentication){return ResponseEntity.ok(service.getAllByCrop(cropId, authentication.getName()));}
    @PutMapping("/update")
    public ResponseEntity<CropDeficiencyToxicityResponseDto> update(@RequestParam(name = "deficiencyToxicityId") Long deficiencyToxicityId, @Valid @RequestBody CropDeficiencyToxicityPostRequestDto dto, Authentication authentication){return ResponseEntity.ok(service.update(deficiencyToxicityId, dto, authentication.getName()));}
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam(name = "deficiencyToxicityId") Long deficiencyToxicityId, Authentication authentication){service.delete(deficiencyToxicityId, authentication.getName());return ResponseEntity.noContent().build();}
}
