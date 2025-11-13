package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLinePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineResponseDto;

import java.util.List;

public interface CropFoliarAnalysisInterpretationTableLineService {

    CropFoliarAnalysisInterpretationTableLineResponseDto createCropFoliarAnalysisInterpretationTableLine(
            Long tableId,
            CropFoliarAnalysisInterpretationTableLineCreateRequestDto createRequestDto,
            String username);

    CropFoliarAnalysisInterpretationTableLineResponseDto getCropFoliarAnalysisInterpretationTableLineById(
            Long lineId,
            String username);

    List<CropFoliarAnalysisInterpretationTableLineResponseDto> getAllCropFoliarAnalysisInterpretationTableLinesByTable(
            Long tableId,
            String username);

    CropFoliarAnalysisInterpretationTableLineResponseDto updateCropFoliarAnalysisInterpretationTableLine(
            Long lineId,
            CropFoliarAnalysisInterpretationTableLinePostRequestDto updateRequestDto,
            String username);

    void deleteCropFoliarAnalysisInterpretationTableLine(Long lineId, String username);
}