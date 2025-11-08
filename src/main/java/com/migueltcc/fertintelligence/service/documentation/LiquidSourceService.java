package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceCreateRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourcePostRequestDto;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceResponseDto;

import java.util.List;

public interface LiquidSourceService {

    LiquidSourceResponseDto createLiquidSource(Long cropId, LiquidSourceCreateRequestDto createRequestDto, String username);

    LiquidSourceResponseDto getLiquidSourceById(Long liquidSourceId, String username);

    List<LiquidSourceResponseDto> getAllLiquidSourcesByCrop(Long cropId, String username);

    LiquidSourceResponseDto updateLiquidSource(Long liquidSourceId, LiquidSourcePostRequestDto updateRequestDto, String username);

    void deleteLiquidSource(Long liquidSourceId, String username);
}