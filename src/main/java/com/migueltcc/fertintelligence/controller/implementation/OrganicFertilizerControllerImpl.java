package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.OrganicFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.OrganicFertilizerService;
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
@RequestMapping("/organic-fertilizer")
public class OrganicFertilizerControllerImpl implements OrganicFertilizerController {

    private static final Logger logger = LoggerFactory.getLogger(OrganicFertilizerControllerImpl.class);
    private final OrganicFertilizerService organicFertilizerService;

    // Injeção via Construtor
    public OrganicFertilizerControllerImpl(OrganicFertilizerService organicFertilizerService) {
        this.organicFertilizerService = organicFertilizerService;
    }

    // Validação de Segurança
    private String getAuthenticatedUsername(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        return authentication.getName();
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<OrganicFertilizerResponseDto> createOrganicFertilizer(
            @Valid @RequestBody OrganicFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        OrganicFertilizerResponseDto createdFertilizer = organicFertilizerService.createOrganicFertilizer(
                createRequestDto,
                username
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdFertilizer.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdFertilizer);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<OrganicFertilizerResponseDto> getOrganicFertilizer(
            @RequestParam(name = "organicFertilizerId") Long organicFertilizerId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(organicFertilizerService.getOrganicFertilizerById(organicFertilizerId, username));
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<OrganicFertilizerResponseDto>> getAllOrganicFertilizers(
            Authentication authentication) {

        try {
            String username = getAuthenticatedUsername(authentication);
            List<OrganicFertilizerResponseDto> list = organicFertilizerService.getAllOrganicFertilizers(username);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Erro ao listar adubos orgânicos: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao listar adubos.");
        }
    }

    @Override
    @GetMapping("/get-all-public")
    public ResponseEntity<List<OrganicFertilizerResponseDto>> getAllPublicOrganicFertilizers(Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<OrganicFertilizerResponseDto> list = organicFertilizerService.getAllPublicOrganicFertilizers(username);
        return ResponseEntity.ok(list);
    }

    @Override
    @GetMapping("/get-all-default")
    public ResponseEntity<List<OrganicFertilizerResponseDto>> getAllDefaultOrganicFertilizers(Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<OrganicFertilizerResponseDto> list = organicFertilizerService.getAllDefaultOrganicFertilizers(username);
        return ResponseEntity.ok(list);
    }


    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<OrganicFertilizerResponseDto>> getOrganicFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(organicFertilizerService.getOrganicFertilizersByName(name, username));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<OrganicFertilizerResponseDto> updateOrganicFertilizer(
            @RequestParam(name = "organicFertilizerId") Long organicFertilizerId,
            @Valid @RequestBody OrganicFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(organicFertilizerService.updateOrganicFertilizer(organicFertilizerId, updateRequestDto, username));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteOrganicFertilizer(
            @RequestParam(name = "organicFertilizerId") Long organicFertilizerId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        organicFertilizerService.deleteOrganicFertilizer(organicFertilizerId, username);
        return ResponseEntity.noContent().build();
    }
}
