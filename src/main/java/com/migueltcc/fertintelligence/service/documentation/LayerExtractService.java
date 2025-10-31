package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractCreateRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractPostRequestDto;
import com.migueltcc.fertintelligence.dto.extract.layer.LayerExtractResponseDto;

import java.util.List;

public interface LayerExtractService {

    LayerExtractResponseDto createLayerExtract(Long analysisId, LayerExtractCreateRequestDto createRequestDto, String username);

    LayerExtractResponseDto getLayerExtractById(Long layerExtractId, String username);

    List<LayerExtractResponseDto> getAllLayerExtractsByAnalysis(Long analysisId, String username);

    LayerExtractResponseDto updateLayerExtract(Long layerExtractId, LayerExtractPostRequestDto updateRequestDto, String username);

    void deleteLayerExtract(Long layerExtractId, String username);
}