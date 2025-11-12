package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.contentRange.ContentRangeResponseDto;

import java.util.List;

public interface ContentRangeService {

    ContentRangeResponseDto createContentRange(Long tableId, ContentRangeCreateRequestDto createRequestDto, String username);

    ContentRangeResponseDto getContentRangeById(Long contentRangeId, String username);

    List<ContentRangeResponseDto> getAllContentRangesByTable(Long tableId, String username);

    ContentRangeResponseDto updateContentRange(Long contentRangeId, ContentRangePostRequestDto updateRequestDto, String username);

    void deleteContentRange(Long contentRangeId, String username);
}