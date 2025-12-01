package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.property.PropertyCreateRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyPostRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam; // Import necessário

import java.util.List;

@Tag(name = "Propriedades", description = "Endpoints para gerenciamento de propriedades")
@SecurityRequirement(name = "bearerAuth")
public interface PropertyController {

    // POST /property/register
    ResponseEntity<PropertyResponseDto> createProperty(
            @Parameter(description = "Dados da propriedade a ser criada", required = true) @Valid @RequestBody PropertyCreateRequestDto createRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    // GET /property/get?propertyId={id}
    ResponseEntity<PropertyResponseDto> getProperty(
            @Parameter(description = "ID da propriedade a ser buscada", required = true) @RequestParam(name = "propertyId") Long propertyId, // ID como RequestParam
            @Parameter(hidden = true) Authentication authentication
    );

    // GET /property/get-my-properties
    ResponseEntity<List<PropertyResponseDto>> getMyProperties(
            @Parameter(hidden = true) Authentication authentication
    );

    // PUT /property/update?propertyId={id}
    ResponseEntity<PropertyResponseDto> updateProperty(
            @Parameter(description = "ID da propriedade a ser atualizada", required = true) @RequestParam(name = "propertyId") Long propertyId,
            @Parameter(description = "Dados para atualização", required = true) @Valid @RequestBody PropertyPostRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    // DELETE /property/delete?propertyId={id}
    ResponseEntity<Void> deleteProperty(
            @Parameter(description = "ID da propriedade a ser excluída", required = true) @RequestParam(name = "propertyId") Long propertyId,
            @Parameter(hidden = true) Authentication authentication
    );

    // GET /property/search?nome={nome}
    ResponseEntity<List<PropertyResponseDto>> searchPropertiesByName(
            @Parameter(description = "Trecho do nome da propriedade para buscar", required = true) @RequestParam(name = "nome") String nome,
            @Parameter(hidden = true) Authentication authentication
    );

}