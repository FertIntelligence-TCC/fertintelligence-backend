package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationCreateRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationPostRequestDto;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationResponseDto;

import java.util.List;

public interface TopDressingFertilizationService {

    TopDressingFertilizationResponseDto createTopDressingFertilization(Long cropId,
                                                                       TopDressingFertilizationCreateRequestDto createRequestDto,
                                                                       String username);

    TopDressingFertilizationResponseDto getTopDressingFertilizationById(Long fertilizationId, String username);

    List<TopDressingFertilizationResponseDto> getAllTopDressingFertilizationsByCrop(Long cropId, String username);

    TopDressingFertilizationResponseDto updateTopDressingFertilization(Long fertilizationId,
                                                                       TopDressingFertilizationPostRequestDto updateRequestDto,
                                                                       String username);

    void deleteTopDressingFertilization(Long fertilizationId, String username);
}