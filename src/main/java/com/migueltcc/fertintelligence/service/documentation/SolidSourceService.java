package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourcePostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solid.SolidSourceResponseDto;

import java.util.List;

public interface SolidSourceService {

    SolidSourceResponseDto createSolidSource(Long cropId, SolidSourceCreateRequestDto createRequestDto, String username);

    SolidSourceResponseDto getSolidSourceById(Long solidSourceId, String username);

    List<SolidSourceResponseDto> getAllSolidSourcesByCrop(Long cropId, String username);

    SolidSourceResponseDto updateSolidSource(Long solidSourceId, SolidSourcePostRequestDto updateRequestDto, String username);

    void deleteSolidSource(Long solidSourceId, String username);
}