package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.ShoppingListController;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListCreateRequestDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListPostRequestDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListResponseDto;
import com.migueltcc.fertintelligence.service.documentation.ShoppingListService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/shopping-list")
public class ShoppingListControllerImpl implements ShoppingListController {

    @Autowired
    private ShoppingListService shoppingListService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<ShoppingListResponseDto> create(
            @Valid @RequestBody ShoppingListCreateRequestDto dto,
            Authentication authentication) {
        ShoppingListResponseDto created = shoppingListService.create(dto, authentication.getName());
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/shopping-list/get")
                .queryParam("id", created.getId())
                .build()
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Override
    @GetMapping("/get")
    public ResponseEntity<ShoppingListResponseDto> get(@RequestParam(name = "id") Long id,
                                                        Authentication authentication) {
        return ResponseEntity.ok(shoppingListService.get(id, authentication.getName()));
    }

    @Override
    @GetMapping("/get-by-recommendation")
    public ResponseEntity<ShoppingListResponseDto> getByRecommendation(
            @RequestParam(name = "recommendationId") Long recommendationId,
            Authentication authentication) {
        return ResponseEntity.ok(shoppingListService.getByRecommendation(recommendationId, authentication.getName()));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<ShoppingListResponseDto> update(@RequestParam(name = "id") Long id,
                                                           @Valid @RequestBody ShoppingListPostRequestDto dto,
                                                           Authentication authentication) {
        return ResponseEntity.ok(shoppingListService.update(id, dto, authentication.getName()));
    }

    @Override
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam(name = "id") Long id,
                                       Authentication authentication) {
        shoppingListService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
