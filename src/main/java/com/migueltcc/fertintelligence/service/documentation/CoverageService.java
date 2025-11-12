package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoveragePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.coverage.CoverageResponseDto;

import java.util.List;

public interface CoverageService {

    CoverageResponseDto createCoverage(Long contentRangeId, CoverageCreateRequestDto createRequestDto, String username);

    CoverageResponseDto getCoverageById(Long coverageId, String username);

    List<CoverageResponseDto> getAllCoveragesByContentRange(Long contentRangeId, String username);

    CoverageResponseDto updateCoverage(Long coverageId, CoveragePostRequestDto updateRequestDto, String username);

    void deleteCoverage(Long coverageId, String username);
}