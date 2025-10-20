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
import org.springframework.web.util.UriComponentsBuilder; // Import para construir URI com params

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/property") // Base path /property
public class PropertyControllerImpl implements PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<PropertyResponseDto> createProperty(
            @Valid @RequestBody PropertyPostRequestDto postRequestDto,
            Authentication authentication) {

        PropertyResponseDto createdProperty = propertyService.createProperty(postRequestDto, authentication.getName());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/property/get")
                .queryParam("propertyId", createdProperty.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location).body(createdProperty);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<PropertyResponseDto> getProperty(
            @RequestParam(name = "propertyId") Long propertyId,
            Authentication authentication) {
        PropertyResponseDto property = propertyService.getPropertyById(propertyId, authentication.getName());
        return ResponseEntity.ok(property);
    }

    @Override
    @GetMapping("/get-my-properties")
    public ResponseEntity<List<PropertyResponseDto>> getMyProperties(Authentication authentication) {
        List<PropertyResponseDto> properties = propertyService.getAllPropertiesByOwner(authentication.getName());
        return ResponseEntity.ok(properties);
    }

    @Override
    @PutMapping("/update") // PUT /property/update?propertyId={id}
    public ResponseEntity<PropertyResponseDto> updateProperty(
            @RequestParam(name = "propertyId") Long propertyId,
            @Valid @RequestBody PropertyUpdateRequestDto updateRequestDto,
            Authentication authentication) {
        PropertyResponseDto updatedProperty = propertyService.updateProperty(propertyId, updateRequestDto, authentication.getName());
        return ResponseEntity.ok(updatedProperty);
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteProperty(
            @RequestParam(name = "propertyId") Long propertyId,
            Authentication authentication) {
        propertyService.deleteProperty(propertyId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}