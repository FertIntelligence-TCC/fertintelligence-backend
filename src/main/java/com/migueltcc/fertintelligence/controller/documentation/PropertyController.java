package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.property.PropertyPostRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import com.migueltcc.fertintelligence.dto.property.PropertyUpdateRequestDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Propriedades", description = "Endpoints para gerenciamento de propriedades")
@SecurityRequirement(name = "bearerAuth")
public interface PropertyController {

    ResponseEntity<PropertyResponseDto> createProperty(
            @Parameter(description = "Dados da propriedade a ser criada", required = true) @Valid @RequestBody PropertyPostRequestDto postRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<PropertyResponseDto> getPropertyById(
            @Parameter(description = "ID da propriedade a ser buscada", required = true) @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<List<PropertyResponseDto>> getMyProperties(
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<PropertyResponseDto> updateProperty(
            @Parameter(description = "ID da propriedade a ser atualizada", required = true) @PathVariable Long id,
            @Parameter(description = "Dados para atualização", required = true) @Valid @RequestBody PropertyUpdateRequestDto updateRequestDto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> deleteProperty(
            @Parameter(description = "ID da propriedade a ser excluída", required = true) @PathVariable Long id,
            @Parameter(hidden = true) Authentication authentication
    );
}