package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListCreateRequestDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListPostRequestDto;
import com.migueltcc.fertintelligence.dto.shoppingList.ShoppingListResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.RecommendationModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ShoppingListModel;

public interface ShoppingListService {
    ShoppingListResponseDto create(ShoppingListCreateRequestDto dto, String username);
    ShoppingListModel createInitial(RecommendationModel recommendation, String technicalReport);
    ShoppingListResponseDto get(Long id, String username);
    ShoppingListResponseDto getByRecommendation(Long recommendationId, String username);
    ShoppingListResponseDto update(Long id, ShoppingListPostRequestDto dto, String username);
    void delete(Long id, String username);
}
