package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeResponseDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeReplaceByNutrientRequestDto;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.Nutriente;

import java.util.List;

public interface ContentRangeService {

    ContentRangeResponseDto createContentRange(Long tableId, ContentRangeCreateRequestDto createRequestDto, String username);

    ContentRangeResponseDto getContentRangeById(Long contentRangeId, String username);

    List<ContentRangeResponseDto> getAllContentRangesByTable(Long tableId, String username);

    ContentRangeResponseDto updateContentRange(Long contentRangeId, ContentRangePostRequestDto updateRequestDto, String username);

    List<ContentRangeResponseDto> replaceContentRangesByNutrient(
            Long tableId,
            Nutriente nutrient,
            ContentRangeReplaceByNutrientRequestDto replaceRequestDto,
            String username
    );

    void deleteContentRange(Long contentRangeId, String username);
}