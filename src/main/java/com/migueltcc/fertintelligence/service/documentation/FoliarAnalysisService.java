package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisResponseDto;

import java.util.List;

public interface FoliarAnalysisService {

    FoliarAnalysisResponseDto createFoliarAnalysis(Long cropId, FoliarAnalysisCreateRequestDto createRequestDto, String username);
    FoliarAnalysisResponseDto getFoliarAnalysisById(Long foliarAnalysisId, String username);
    List<FoliarAnalysisResponseDto> getAllFoliarAnalysesByCrop(Long cropId, String username);
    FoliarAnalysisResponseDto updateFoliarAnalysis(Long foliarAnalysisId, FoliarAnalysisPostRequestDto updateRequestDto, String username);
    void deleteFoliarAnalysis(Long foliarAnalysisId, String username);
}