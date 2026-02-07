package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractResponseDto;

import java.util.List;

public interface PhysicalAnalysisExtractService {

    PhysicalAnalysisExtractResponseDto createPhysicalAnalysisExtract(Long rangeExtractId,
                                                                     Long layerExtractId,
                                                                     PhysicalAnalysisExtractCreateRequestDto createRequestDto,
                                                                     String username);

    PhysicalAnalysisExtractResponseDto getPhysicalAnalysisExtractById(Long physicalAnalysisExtractId, String username);

    List<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtractsByRange(Long rangeExtractId, String username);

    List<PhysicalAnalysisExtractResponseDto> getPhysicalAnalysisExtractsByLayer(Long layerExtractId, String username);

    PhysicalAnalysisExtractResponseDto updatePhysicalAnalysisExtract(Long physicalAnalysisExtractId,
                                                                     PhysicalAnalysisExtractPostRequestDto updateRequestDto,
                                                                     String username);

    void deletePhysicalAnalysisExtract(Long physicalAnalysisExtractId, String username);
}