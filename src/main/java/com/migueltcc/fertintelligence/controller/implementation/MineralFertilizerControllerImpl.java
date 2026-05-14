package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.MineralFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.mineralFertilizer.MineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.MineralFertilizerService;
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
@RequestMapping("/mineral-fertilizer")
public class MineralFertilizerControllerImpl implements MineralFertilizerController {

    private static final Logger logger = LoggerFactory.getLogger(MineralFertilizerControllerImpl.class);
    private final MineralFertilizerService service;

    public MineralFertilizerControllerImpl(MineralFertilizerService service) {
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
    public ResponseEntity<MineralFertilizerResponseDto> createMineralFertilizer(
            @Valid @RequestBody MineralFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        MineralFertilizerResponseDto created = service.createMineralFertilizer(createRequestDto, username);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<MineralFertilizerResponseDto> getMineralFertilizer(
            @RequestParam(name = "mineralFertilizerId") Long id,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.getMineralFertilizerById(id, username));
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<MineralFertilizerResponseDto>> getAllMineralFertilizers(
            Authentication authentication) {

        try {
            String username = getAuthenticatedUsername(authentication);
            List<MineralFertilizerResponseDto> list = service.getAllMineralFertilizers(username);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Erro ao listar adubos minerais foliares: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/get-all-public")
    public ResponseEntity<List<MineralFertilizerResponseDto>> getAllPublicMineralFertilizers(Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<MineralFertilizerResponseDto> list = service.getAllPublicMineralFertilizers();
        return ResponseEntity.ok(list);
    }


    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<MineralFertilizerResponseDto>> getMineralFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.getMineralFertilizersByName(name, username));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<MineralFertilizerResponseDto> updateMineralFertilizer(
            @RequestParam(name = "mineralFertilizerId") Long id,
            @Valid @RequestBody MineralFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(service.updateMineralFertilizer(id, updateRequestDto, username));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteMineralFertilizer(
            @RequestParam(name = "mineralFertilizerId") Long id,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        service.deleteMineralFertilizer(id, username);
        return ResponseEntity.noContent().build();
    }
}