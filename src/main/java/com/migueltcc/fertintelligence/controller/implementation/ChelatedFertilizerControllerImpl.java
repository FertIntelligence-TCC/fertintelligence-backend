package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.ChelatedFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.ChelatedFertilizerService;
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
@RequestMapping("/chelated-fertilizer")
public class ChelatedFertilizerControllerImpl implements ChelatedFertilizerController {

    private static final Logger logger = LoggerFactory.getLogger(ChelatedFertilizerControllerImpl.class);
    private final ChelatedFertilizerService service;

    public ChelatedFertilizerControllerImpl(ChelatedFertilizerService service) {
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
    public ResponseEntity<ChelatedFertilizerResponseDto> createChelatedFertilizer(
            @Valid @RequestBody ChelatedFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        ChelatedFertilizerResponseDto created = service.createChelatedFertilizer(createRequestDto, username);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<ChelatedFertilizerResponseDto> getChelatedFertilizer(
            @RequestParam(name = "chelatedFertilizerId") Long id,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.getChelatedFertilizerById(id, username));
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<ChelatedFertilizerResponseDto>> getAllChelatedFertilizers(
            Authentication authentication) {

        try {
            String username = getAuthenticatedUsername(authentication);
            List<ChelatedFertilizerResponseDto> list = service.getAllChelatedFertilizers(username);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Erro ao listar adubos quelatados: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<ChelatedFertilizerResponseDto>> getChelatedFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.getChelatedFertilizersByName(name, username));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<ChelatedFertilizerResponseDto> updateChelatedFertilizer(
            @RequestParam(name = "chelatedFertilizerId") Long id,
            @Valid @RequestBody ChelatedFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.updateChelatedFertilizer(id, updateRequestDto, username));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteChelatedFertilizer(
            @RequestParam(name = "chelatedFertilizerId") Long id,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        service.deleteChelatedFertilizer(id, username);
        return ResponseEntity.noContent().build();
    }
}