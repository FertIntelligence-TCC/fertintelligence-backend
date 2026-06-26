package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListCreateRequestDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListPostRequestDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "ShoppingList", description = "Endpoints para gerenciamento da Lista de Compras")
@SecurityRequirement(name = "bearerAuth")
public interface ShoppingListController {

    ResponseEntity<ShoppingListResponseDto> create(
            @Valid @RequestBody ShoppingListCreateRequestDto dto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<ShoppingListResponseDto> get(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<ShoppingListResponseDto> getByRecommendation(
            @RequestParam(name = "recommendationId") Long recommendationId,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<ShoppingListResponseDto> update(
            @RequestParam(name = "id") Long id,
            @Valid @RequestBody ShoppingListPostRequestDto dto,
            @Parameter(hidden = true) Authentication authentication
    );

    ResponseEntity<Void> delete(
            @RequestParam(name = "id") Long id,
            @Parameter(hidden = true) Authentication authentication
    );
}
