package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.crop.CropCreateRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropPostRequestDto;
import com.migueltcc.fertintelligence.dto.crop.CropResponseDto;

import java.util.List;

public interface CropService {

    CropResponseDto createCrop(Long folderId, CropCreateRequestDto createRequestDto, String username);

    CropResponseDto getCropById(Long cropId, String username);

    List<CropResponseDto> getAllCropsByFolder(Long folderId, String username);

    List<CropResponseDto> getAllByAnnualCropFolderId(Long folderId, String username);

    CropResponseDto updateCrop(Long cropId, CropPostRequestDto updateRequestDto, String username);

    void deleteCrop(Long cropId, String username);
}