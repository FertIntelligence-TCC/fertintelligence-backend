package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.OrganoMineralFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organoMineralFertilizer.OrganoMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.OrganoMineralFertilizerService;
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
@RequestMapping("/organo-mineral-fertilizer")
public class OrganoMineralFertilizerControllerImpl implements OrganoMineralFertilizerController {

    private static final Logger logger = LoggerFactory.getLogger(OrganoMineralFertilizerControllerImpl.class);
    private final OrganoMineralFertilizerService service;

    // Injeção via Construtor
    public OrganoMineralFertilizerControllerImpl(OrganoMineralFertilizerService service) {
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
    public ResponseEntity<OrganoMineralFertilizerResponseDto> createOrganoMineralFertilizer(
            @Valid @RequestBody OrganoMineralFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        OrganoMineralFertilizerResponseDto created = service.createOrganoMineralFertilizer(createRequestDto, username);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<OrganoMineralFertilizerResponseDto>> getAllOrganoMineralFertilizers(
            Authentication authentication) {

        try {
            String username = getAuthenticatedUsername(authentication);
            List<OrganoMineralFertilizerResponseDto> list = service.getAllOrganoMineralFertilizers(username);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            // LOG DO ERRO 500 PARA DEBUG
            logger.error("Erro ao listar adubos organominerais: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/get-all-public")
    public ResponseEntity<List<OrganoMineralFertilizerResponseDto>> getAllPublicOrganoMineralFertilizers(Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<OrganoMineralFertilizerResponseDto> list = service.getAllPublicOrganoMineralFertilizers(username);
        return ResponseEntity.ok(list);
    }


    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<OrganoMineralFertilizerResponseDto>> getOrganoMineralFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.getOrganoMineralFertilizersByName(name, username));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<OrganoMineralFertilizerResponseDto> updateOrganoMineralFertilizer(
            @RequestParam(name = "organoMineralFertilizerId") Long id,
            @Valid @RequestBody OrganoMineralFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.updateOrganoMineralFertilizer(id, updateRequestDto, username));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteOrganoMineralFertilizer(
            @RequestParam(name = "organoMineralFertilizerId") Long id,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        service.deleteOrganoMineralFertilizer(id, username);
        return ResponseEntity.noContent().build();
    }
}