package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.composedAttributes.recommendation.TechnicalTableGroup;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableCreateRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTablePostRequestDto;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableResponseDto;

import java.util.List;

public interface CropFertilizationTableService {

    CropFertilizationTableResponseDto createCropFertilizationTable(
            CropFertilizationTableCreateRequestDto createRequestDto,
            String username
    );

    CropFertilizationTableResponseDto getCropFertilizationTableById(
            Long tableId,
            String username
    );

    List<CropFertilizationTableResponseDto> getAllCropFertilizationTables(String username);

    List<CropFertilizationTableResponseDto> getAllCropFertilizationTables(String username, TechnicalTableGroup group);

    List<CropFertilizationTableResponseDto> getAllPublicCropFertilizationTables();

    CropFertilizationTableResponseDto updateCropFertilizationTable(
            Long tableId,
            CropFertilizationTablePostRequestDto updateRequestDto,
            String username
    );

    void deleteCropFertilizationTable(Long tableId, String username);
}
