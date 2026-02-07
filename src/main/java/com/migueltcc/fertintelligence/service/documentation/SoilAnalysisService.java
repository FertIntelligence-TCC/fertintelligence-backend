package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisCreateRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisPostRequestDto;
import com.migueltcc.fertintelligence.dto.soilAnalysis.SoilAnalysisResponseDto;

import java.util.List;

public interface SoilAnalysisService {

    SoilAnalysisResponseDto createSoilAnalysis(SoilAnalysisCreateRequestDto createRequestDto, String username);

    SoilAnalysisResponseDto getSoilAnalysisById(Long soilAnalysisId, String username);

    List<SoilAnalysisResponseDto> getAllSoilAnalysesByPlot(Long plotId, String username);

    List<SoilAnalysisResponseDto> getAllByPlotId(Long plotId, String username);

    SoilAnalysisResponseDto updateSoilAnalysis(Long soilAnalysisId, SoilAnalysisPostRequestDto updateRequestDto, String username);

    void deleteSoilAnalysis(Long soilAnalysisId, String username);
}