package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractResponseDto;

import java.util.List;

public interface FertilityAnalysisExtractService {

    FertilityAnalysisExtractResponseDto createFertilityAnalysisExtract(Long rangeExtractId,
                                                                       Long layerExtractId,
                                                                       FertilityAnalysisExtractCreateRequestDto createRequestDto,
                                                                       String username);

    FertilityAnalysisExtractResponseDto getFertilityAnalysisExtractById(Long fertilityAnalysisExtractId, String username);

    List<FertilityAnalysisExtractResponseDto> getAllByPlotId(Long plotId, String username);

    List<FertilityAnalysisExtractResponseDto> getFertilityAnalysisExtractsByRange(Long rangeExtractId, String username);

    List<FertilityAnalysisExtractResponseDto> getFertilityAnalysisExtractsByLayer(Long layerExtractId, String username);

    FertilityAnalysisExtractResponseDto updateFertilityAnalysisExtract(Long fertilityAnalysisExtractId,
                                                                       FertilityAnalysisExtractPostRequestDto updateRequestDto,
                                                                       String username);

    void deleteFertilityAnalysisExtract(Long fertilityAnalysisExtractId, String username);
}