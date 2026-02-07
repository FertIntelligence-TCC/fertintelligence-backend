package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractResponseDto;

import java.util.List;

public interface SaturationExtractAnalysisExtractService {

    SaturationExtractAnalysisExtractResponseDto createSaturationExtractAnalysisExtract(Long rangeExtractId,
                                                                                       Long layerExtractId,
                                                                                       SaturationExtractAnalysisExtractCreateRequestDto createRequestDto,
                                                                                       String username);

    SaturationExtractAnalysisExtractResponseDto getSaturationExtractAnalysisExtractById(Long saturationExtractAnalysisExtractId,
                                                                                        String username);

    List<SaturationExtractAnalysisExtractResponseDto> getAllByPlotId(Long plotId, String username);

    List<SaturationExtractAnalysisExtractResponseDto> getSaturationExtractAnalysisExtractsByRange(Long rangeExtractId,
                                                                                                  String username);

    List<SaturationExtractAnalysisExtractResponseDto> getSaturationExtractAnalysisExtractsByLayer(Long layerExtractId,
                                                                                                  String username);

    SaturationExtractAnalysisExtractResponseDto updateSaturationExtractAnalysisExtract(Long saturationExtractAnalysisExtractId,
                                                                                       SaturationExtractAnalysisExtractPostRequestDto updateRequestDto,
                                                                                       String username);

    void deleteSaturationExtractAnalysisExtract(Long saturationExtractAnalysisExtractId, String username);
}