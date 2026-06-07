package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.GreenFertilizerController;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerResponseDto;
import com.migueltcc.fertintelligence.service.documentation.GreenFertilizerService;
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
@RequestMapping("/green-fertilizer")
public class GreenFertilizerControllerImpl implements GreenFertilizerController {

    private static final Logger logger = LoggerFactory.getLogger(GreenFertilizerControllerImpl.class);
    private final GreenFertilizerService greenFertilizerService;

    // Injeção via Construtor
    public GreenFertilizerControllerImpl(GreenFertilizerService greenFertilizerService) {
        this.greenFertilizerService = greenFertilizerService;
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
    public ResponseEntity<GreenFertilizerResponseDto> createGreenFertilizer(
            @Valid @RequestBody GreenFertilizerCreateRequestDto createRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        GreenFertilizerResponseDto createdFertilizer = greenFertilizerService.createGreenFertilizer(
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
    public ResponseEntity<GreenFertilizerResponseDto> getGreenFertilizer(
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(greenFertilizerService.getGreenFertilizerById(greenFertilizerId, username));
    }

    @Override
    @GetMapping("/get-all")
    public ResponseEntity<List<GreenFertilizerResponseDto>> getAllGreenFertilizers(
            Authentication authentication) {

        try {
            String username = getAuthenticatedUsername(authentication);
            List<GreenFertilizerResponseDto> list = greenFertilizerService.getAllGreenFertilizers(username);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Erro ao listar adubos verdes: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao listar adubos.");
        }
    }

    @Override
    @GetMapping("/get-all-public")
    public ResponseEntity<List<GreenFertilizerResponseDto>> getAllPublicGreenFertilizers(Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<GreenFertilizerResponseDto> list = greenFertilizerService.getAllPublicGreenFertilizers(username);
        return ResponseEntity.ok(list);
    }

    @Override
    @GetMapping("/get-all-default")
    public ResponseEntity<List<GreenFertilizerResponseDto>> getAllDefaultGreenFertilizers(Authentication authentication) {
        String username = getAuthenticatedUsername(authentication);
        List<GreenFertilizerResponseDto> list = greenFertilizerService.getAllDefaultGreenFertilizers(username);
        return ResponseEntity.ok(list);
    }


    @Override
    @GetMapping("/get-by-name")
    public ResponseEntity<List<GreenFertilizerResponseDto>> getGreenFertilizersByName(
            @RequestParam(name = "name") String name,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(greenFertilizerService.getGreenFertilizersByName(name, username));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<GreenFertilizerResponseDto> updateGreenFertilizer(
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            @Valid @RequestBody GreenFertilizerPostRequestDto updateRequestDto,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        return ResponseEntity.ok(greenFertilizerService.updateGreenFertilizer(greenFertilizerId, updateRequestDto, username));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteGreenFertilizer(
            @RequestParam(name = "greenFertilizerId") Long greenFertilizerId,
            Authentication authentication) {

        String username = getAuthenticatedUsername(authentication);
        greenFertilizerService.deleteGreenFertilizer(greenFertilizerId, username);
        return ResponseEntity.noContent().build();
    }
}
