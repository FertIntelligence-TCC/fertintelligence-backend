package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerCreateRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerPostRequestDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerResponseDto;

import java.util.List;

public interface SimpleMineralFertilizerService {

    SimpleMineralFertilizerResponseDto createSimpleMineralFertilizer(SimpleMineralFertilizerCreateRequestDto createRequestDto,
                                                                     String username);

    SimpleMineralFertilizerResponseDto getSimpleMineralFertilizerById(Long simpleMineralFertilizerId, String username);

    List<SimpleMineralFertilizerResponseDto> getSimpleMineralFertilizersByUser(String username);

    SimpleMineralFertilizerResponseDto updateSimpleMineralFertilizer(Long simpleMineralFertilizerId,
                                                                     SimpleMineralFertilizerPostRequestDto updateRequestDto,
                                                                     String username);

    void deleteSimpleMineralFertilizer(Long simpleMineralFertilizerId, String username);
}