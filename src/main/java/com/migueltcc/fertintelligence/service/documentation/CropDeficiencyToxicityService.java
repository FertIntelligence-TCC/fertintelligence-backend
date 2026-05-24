package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.CropDeficiencyToxicityCreateRequestDto;
import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.CropDeficiencyToxicityPostRequestDto;
import com.migueltcc.fertintelligence.dto.cropDeficiencyToxicity.CropDeficiencyToxicityResponseDto;

import java.util.List;

public interface CropDeficiencyToxicityService {

    CropDeficiencyToxicityResponseDto create(Long cropId, CropDeficiencyToxicityCreateRequestDto dto, String username);
    CropDeficiencyToxicityResponseDto getById(Long id, String username);
    List<CropDeficiencyToxicityResponseDto> getAllByCrop(Long cropId, String username);
    CropDeficiencyToxicityResponseDto update(Long id, CropDeficiencyToxicityPostRequestDto dto, String username);
    void delete(Long id, String username);
}
