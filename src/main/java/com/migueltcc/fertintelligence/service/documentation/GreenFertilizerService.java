package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.greenFertilizer.GreenFertilizerResponseDto;

import java.util.List;

public interface GreenFertilizerService {

    GreenFertilizerResponseDto createGreenFertilizer(GreenFertilizerCreateRequestDto createRequestDto, String username);

    GreenFertilizerResponseDto getGreenFertilizerById(Long greenFertilizerId, String username);

    List<GreenFertilizerResponseDto> getAllGreenFertilizers(String username);

    List<GreenFertilizerResponseDto> getGreenFertilizersByName(String name, String username);

    GreenFertilizerResponseDto updateGreenFertilizer(Long greenFertilizerId,
                                                     GreenFertilizerPostRequestDto updateRequestDto,
                                                     String username);

    void deleteGreenFertilizer(Long greenFertilizerId, String username);
}