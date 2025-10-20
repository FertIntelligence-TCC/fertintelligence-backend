package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.PropertyController;
import com.migueltcc.fertintelligence.dto.property.PropertyPostRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import com.migueltcc.fertintelligence.dto.property.PropertyUpdateRequestDto;
import com.migueltcc.fertintelligence.service.documentation.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyControllerImpl implements PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Override
    @PostMapping
    public ResponseEntity<PropertyResponseDto> createProperty(
            @Valid @RequestBody PropertyPostRequestDto postRequestDto,
            Authentication authentication) {

        // Padrão corrigido: Passa o username (email) para o serviço
        PropertyResponseDto createdProperty = propertyService.createProperty(postRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProperty.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdProperty);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> getPropertyById(
            @PathVariable Long id,
            Authentication authentication) {

        PropertyResponseDto property = propertyService.getPropertyById(id, authentication.getName());
        return ResponseEntity.ok(property);
    }

    @Override
    @GetMapping("/my-properties")
    public ResponseEntity<List<PropertyResponseDto>> getMyProperties(Authentication authentication) {
        List<PropertyResponseDto> properties = propertyService.getAllPropertiesByOwner(authentication.getName());
        return ResponseEntity.ok(properties);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> updateProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyUpdateRequestDto updateRequestDto,
            Authentication authentication) {

        PropertyResponseDto updatedProperty = propertyService.updateProperty(id, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedProperty);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(
            @PathVariable Long id,
            Authentication authentication) {

        propertyService.deleteProperty(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}