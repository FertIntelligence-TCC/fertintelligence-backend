package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.range.RangeExtractResponseDto;

import java.util.List;

public interface RangeExtractService {

    RangeExtractResponseDto createRangeExtract(Long analysisId, RangeExtractCreateRequestDto createRequestDto, String username);

    RangeExtractResponseDto getRangeExtractById(Long rangeExtractId, String username);

    List<RangeExtractResponseDto> getAllRangeExtractsByAnalysis(Long analysisId, String username);

    RangeExtractResponseDto updateRangeExtract(Long rangeExtractId, RangeExtractPostRequestDto updateRequestDto, String username);

    void deleteRangeExtract(Long rangeExtractId, String username);
}