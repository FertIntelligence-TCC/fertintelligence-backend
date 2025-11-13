package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.table.CropFoliarAnalysisInterpretationTableResponseDto;

import java.util.List;

public interface CropFoliarAnalysisInterpretationTableService {

    CropFoliarAnalysisInterpretationTableResponseDto createCropFoliarAnalysisInterpretationTable(
            CropFoliarAnalysisInterpretationTableCreateRequestDto createRequestDto,
            String username);

    CropFoliarAnalysisInterpretationTableResponseDto getCropFoliarAnalysisInterpretationTableById(
            Long tableId,
            String username);

    List<CropFoliarAnalysisInterpretationTableResponseDto> getAllCropFoliarAnalysisInterpretationTablesByCreator(
            String username);

    CropFoliarAnalysisInterpretationTableResponseDto updateCropFoliarAnalysisInterpretationTable(
            Long tableId,
            CropFoliarAnalysisInterpretationTablePostRequestDto updateRequestDto,
            String username);

    void deleteCropFoliarAnalysisInterpretationTable(Long tableId, String username);
}
