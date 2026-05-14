package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.BioFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.BioFertilizerService;
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
@RequestMapping("/bio-fertilizer")
public class BioFertilizerControllerImpl implements BioFertilizerController {

    private static final Logger logger = LoggerFactory.getLogger(BioFertilizerControllerImpl.class);
    private final BioFertilizerService service;

    public BioFertilizerControllerImpl(BioFertilizerService service) {
        this.service = service;
    }

    private String getAuthenticatedUsername(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        return authentication.getName();
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<BioFertilizerResponseDto> createBioFertilizer(
            @Valid @RequestBody BioFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        BioFertilizerResponseDto created = service.createBioFertilizer(createRequestDto, username);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<BioFertilizerResponseDto> getBioFertilizer(
            @RequestParam(name = "bioFertilizerId") Long id,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.getBioFertilizerById(id, username));
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<BioFertilizerResponseDto>> getAllBioFertilizers(
            Authentication authentication) {

        try {
            String username = getAuthenticatedUsername(authentication);
            List<BioFertilizerResponseDto> list = service.getAllBioFertilizers(username);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Erro ao listar biofertilizantes: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/get-all-public")
    public ResponseEntity<List<BioFertilizerResponseDto>> getAllPublicBioFertilizers(Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<BioFertilizerResponseDto> list = service.getAllPublicBioFertilizers();
        return ResponseEntity.ok(list);
    }


    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<BioFertilizerResponseDto>> getBioFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.getBioFertilizersByName(name, username));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<BioFertilizerResponseDto> updateBioFertilizer(
            @RequestParam(name = "bioFertilizerId") Long id,
            @Valid @RequestBody BioFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.updateBioFertilizer(id, updateRequestDto, username));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteBioFertilizer(
            @RequestParam(name = "bioFertilizerId") Long id,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        service.deleteBioFertilizer(id, username);
        return ResponseEntity.noContent().build();
    }
}